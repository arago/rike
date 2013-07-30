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
/**
 *
 */
package de.arago.rike.task.action;

import de.arago.portlet.Action;
import de.arago.portlet.util.SecurityHelper;

import de.arago.data.IDataWrapper;
import de.arago.rike.commons.util.TaskHelper;
import de.arago.rike.commons.data.Artifact;
import de.arago.rike.commons.data.DataHelperRike;
import de.arago.rike.commons.data.GlobalConfig;
import de.arago.rike.commons.data.Milestone;
import de.arago.rike.commons.data.Task;
import de.arago.rike.commons.data.Task.Status;
import de.arago.rike.commons.util.StatisticHelper;
import de.arago.rike.commons.util.ActivityLogHelper;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.HashMap;
import org.apache.commons.lang.StringEscapeUtils;
import static de.arago.rike.commons.data.GlobalConfig.PRIORITY_NORMAL;

public class SaveTask implements Action {

    @Override
    public void execute(IDataWrapper data) {

        Task task = new Task();
        String user = SecurityHelper.getUserEmail(data.getUser());
        Artifact artifact = new DataHelperRike<Artifact>(Artifact.class).find(data.getRequestAttribute("artifact"));

        task.setTitle(data.getRequestAttribute("title"));
        task.setUrl(data.getRequestAttribute("url"));
        task.setArtifact(artifact);
        task.setCreated(new Date());
        task.setCreator(user);
        task.setDescription(data.getRequestAttribute("description"));

        task.setStatus(Status.UNKNOWN);
        task.setMilestone(new DataHelperRike<Milestone>(Milestone.class).find(data.getRequestAttribute("milestone")));

        try {
            task.setSizeEstimated(Integer.valueOf(data.getRequestAttribute("size_estimated"), 10));
        } catch (Exception ignored) {
        }

        int priority = Integer.parseInt(GlobalConfig.get(PRIORITY_NORMAL));

        task.setPriority(priority);

        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            task.setDueDate(format.parse(data.getRequestAttribute("due_date")));
        } catch(Exception ignored) {}

        TaskHelper.save(task);
        StatisticHelper.update();

        HashMap<String, Object> notificationParam = new HashMap<String, Object>();

        notificationParam.put("id", task.getId().toString());
        data.setEvent("TaskUpdateNotification", notificationParam);
        data.setEvent("TaskSelectNotification", notificationParam);

        ActivityLogHelper.log(" created Task #" + task.getId() + " <a href=\"/web/guest/rike/-/show/task/" + task.getId() + "\">" + StringEscapeUtils.escapeHtml(task.getTitle()) + "</a>", task.getStatus(), user, data, task.toMap());
    }
}
