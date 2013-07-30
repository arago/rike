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
package de.arago.rike.task.action;

import de.arago.portlet.Action;
import de.arago.portlet.util.SecurityHelper;

import de.arago.data.IDataWrapper;
import de.arago.rike.commons.data.GlobalConfig;
import static de.arago.rike.commons.data.GlobalConfig.*;
import de.arago.rike.commons.util.TaskHelper;
import de.arago.rike.commons.data.Task;
import de.arago.rike.commons.util.ActivityLogHelper;
import de.arago.rike.commons.util.StatisticHelper;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import org.apache.commons.lang.StringEscapeUtils;

public class StartTask implements Action {

    @Override
    public void execute(IDataWrapper data) {
        if (data.getRequestAttribute("id") != null) {
            String user = SecurityHelper.getUserEmail(data.getUser());

            if (TaskHelper.getTasksInProgressForUser(user).size() < Integer.parseInt(GlobalConfig.get(WORKFLOW_WIP_LIMIT))) {
                Task task = TaskHelper.getTask(data.getRequestAttribute("id"));

                if (!TaskHelper.canDoTask(user, task) || task.getStatusEnum() != Task.Status.OPEN) {
                    return;
                }

                task.setOwner(user);
                task.setStart(new Date());
                task.setStatus(Task.Status.IN_PROGRESS);
                if(GlobalConfig.get(WORKFLOW_TYPE).equalsIgnoreCase("arago Technologies")) {
                    GregorianCalendar c = new GregorianCalendar();
                    c.setTime(task.getStart());
                    c.add(Calendar.DAY_OF_MONTH, Integer.parseInt(GlobalConfig.get(WORKFLOW_DAYS_TO_FINISH_TASK)));
                    task.setDueDate(c.getTime());
                }

                TaskHelper.save(task);
                StatisticHelper.update();


                data.setSessionAttribute("task", task);

                HashMap<String, Object> notificationParam = new HashMap<String, Object>();

                notificationParam.put("id", task.getId().toString());
                data.setEvent("TaskUpdateNotification", notificationParam);

                ActivityLogHelper.log(" started Task #" + task.getId() + " <a href=\"/web/guest/rike/-/show/task/" + task.getId() + "\">" + StringEscapeUtils.escapeHtml(task.getTitle()) + "</a> ", task.getStatus(), user, data, task.toMap());
            }
        }
    }
}
