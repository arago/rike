/**
 * Copyright (c) 2010 arago AG, http://www.arago.de/
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package de.arago.rike.commons.util;

import de.arago.data.IDataWrapper;
import de.arago.rike.commons.data.ActivityLog;
import de.arago.rike.commons.data.DataHelperRike;
import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minidev.json.JSONValue;

public class ActivityLogHelper {
    private static final String POST_HOOK = System.getProperty(TaskHelper.class.getName() + ".postLogHook", "").trim();

    private ActivityLogHelper() {
        //not called
    }

    public static void log(String content, String icon, String user, IDataWrapper data, Map objectState) {
        final ActivityLog log = new ActivityLog();

        log.setContent(content);
        log.setUser(user);
        log.setCreated(new Date());
        log.setIcon(icon);

        objectState.put("log_content", content);
        objectState.put("log_user", user);
        objectState.put("log_time", log.getCreated().getTime() + "");
        log.setJsonData(JSONValue.toJSONString(objectState));

        new DataHelperRike<ActivityLog>(ActivityLog.class).save(log);

        runPostHook(log);

        if(data!=null) {
            HashMap<String, Object> notificationParam = new HashMap<String, Object>();
            data.setEvent("ActivityLogNotification", notificationParam);
        }
    }

    private static void runPostHook(final ActivityLog log) {
        if (POST_HOOK.isEmpty()) return;

        System.err.println("running posthook " + POST_HOOK + " " + log.getContent().replaceAll("<[^>]+>", ""));

        new Thread(new Runnable() {
            @Override
            public void run() {
                Closeable[] todo = new Closeable[3];

                try {
                    List<String> cmd = new ArrayList<String>(Arrays.asList(POST_HOOK.split("\\ +")));
                    cmd.add("rike: "  + log.getUser() + " " + log.getContent().replaceAll("<[^>]+>", ""));

                    Process proc = Runtime.getRuntime().exec(cmd.toArray(new String[0]));

                    todo[0] = proc.getErrorStream();
                    todo[1] = proc.getInputStream();
                    todo[2] = proc.getOutputStream();

                    int res = proc.waitFor();

                    if (res != 0) throw new IllegalStateException("posthook failed " + res);
                } catch(InterruptedException e) {
                    Thread.interrupted();
                } catch(IOException e) {
                    throw new RuntimeException(e);
                } finally {
                    for (Closeable c: todo) {
                        try {
                            if (c != null) c.close();
                        } catch(IOException ignored) {
                            // blank
                        }
                    }
                }
            }
        }).start();
    }

}
