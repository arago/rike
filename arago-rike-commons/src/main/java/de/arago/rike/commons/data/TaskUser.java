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
package de.arago.rike.commons.data;

import java.util.HashMap;
import java.util.Map;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;

public class TaskUser {

    private long id;
    private String email;
    private String lastms;
    private long account;
    private long yesterday;
    private int isDeleted;
    private String flags;
    private int[] endedTasks;

    public String getLastms() {
        return lastms;
    }

    public void setLastms(String lastMs) {
        lastms = lastMs;
    }

    public void setId(Long what) {
        id = what;
    }

    public Long getId() {
        return id;
    }

    public void setEmail(String what) {
        email = what;
    }

    public String getEmail() {
        return email;
    }

    public Long getAccount() {
        return account;
    }

    public void setAccount(Long account) {
        this.account = account;
    }

    public Long getYesterday() {
        return yesterday;
    }

    public void setYesterday(Long yesterday) {
        this.yesterday = yesterday;
    }

    public int getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(int isDeleted) {
        this.isDeleted = isDeleted;
    }

    public boolean isDeleted() {
        return isDeleted == 1;
    }

    public int[] getEndedTasks() {
        return endedTasks;
    }

    public void setEndedTasks(int[] ended_tasks) {
        this.endedTasks = ended_tasks;
    }

    public String getFlags() {
        return flags;
    }

    public void setFlags(String flags) {
        this.flags = flags;
    }

    public Map getFlagsAsMap() {
        if (flags != null && !flags.isEmpty()) {
            JSONParser parser = new JSONParser();
            try {
                return (Map) parser.parse(flags);
            } catch (Exception ex) {
            }
        }
        return new HashMap();
    }

    public void addFlag(String name, String value) {
        Map map = getFlagsAsMap();
        map.put(name, value);
        this.flags = JSONObject.toJSONString(map);
    }

    public String getAlias() {
        return email.replaceAll("\\@.*$", "");
    }

}
