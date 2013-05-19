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
import de.arago.rike.data.Artifact;
import de.arago.rike.util.TaskHelper;
import de.arago.rike.data.DataHelperRike;
import de.arago.rike.data.Milestone;
import de.arago.rike.data.Task;
import de.arago.rike.task.StatisticHelper;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.HashMap;
import org.apache.commons.lang.StringEscapeUtils;

public class EvaluateTask implements Action {

    @Override
    public void execute(IDataWrapper data) throws Exception {

        if (data.getRequestAttribute("id") != null) {

            Task task = TaskHelper.getTask(data.getRequestAttribute("id"));

            String user = SecurityHelper.getUserEmail(data.getUser());

            if (task.getStatusEnum() == Task.Status.UNKNOWN || task.getStatusEnum() == Task.Status.OPEN) {
                task.setMilestone(new DataHelperRike<Milestone>(Milestone.class).find(data.getRequestAttribute("milestone")));
                task.setArtifact(new DataHelperRike<Artifact>(Artifact.class).find(data.getRequestAttribute("artifact")));

                try {
                    task.setSizeEstimated(Integer.valueOf(data.getRequestAttribute("size_estimated"), 10));
                } catch (Exception ignored) {
                }

                try {
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                    task.setDueDate(format.parse(data.getRequestAttribute("due_date")));
                } catch(Exception ignored) {}

                task.setTitle(data.getRequestAttribute("title"));
                task.setUrl(data.getRequestAttribute("url"));
                int priority = 5;

                try {
                    priority = Integer.valueOf(data.getRequestAttribute("priority"), 10);
                } catch (Exception ignored) {
                }

                task.setPriority(priority);
                task.setRated(new Date());
                task.setRatedBy(user);
                task.setStatus(Task.Status.OPEN);

                TaskHelper.save(task);

                StatisticHelper.update();

                data.setSessionAttribute("task", task);

                HashMap<String, Object> notificationParam = new HashMap<String, Object>();

                notificationParam.put("id", data.getRequestAttribute("id"));
                data.setEvent("TaskUpdateNotification", notificationParam);

                data.removeSessionAttribute("targetView");

                TaskHelper.log(" rated Task #" + task.getId().toString() + " <a href=\"[selectTask:" + task.getId().toString() + "]\">" + StringEscapeUtils.escapeHtml(task.getTitle()) + "</a> ", task, SecurityHelper.getUserEmail(data.getUser()), data);
            }
        }
    }
}
