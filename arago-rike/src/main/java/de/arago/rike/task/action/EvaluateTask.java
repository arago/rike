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
import de.arago.rike.commons.data.Artifact;
import de.arago.rike.commons.util.TaskHelper;
import de.arago.rike.commons.data.DataHelperRike;
import de.arago.rike.commons.data.GlobalConfig;
import de.arago.rike.commons.data.Milestone;
import de.arago.rike.commons.data.Task;
import de.arago.rike.commons.util.StatisticHelper;
import de.arago.rike.commons.util.ActivityLogHelper;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import org.apache.commons.lang.StringEscapeUtils;
import static de.arago.rike.commons.data.GlobalConfig.*;

public class EvaluateTask implements Action {

    @Override
    public void execute(IDataWrapper data) {

        if (data.getRequestAttribute("id") != null) {

            Task task = TaskHelper.getTask(data.getRequestAttribute("id"));

            String user = SecurityHelper.getUserEmail(data.getUser());

            if (task.getStatusEnum() == Task.Status.UNKNOWN || task.getStatusEnum() == Task.Status.OPEN) {
                task.setMilestone(new DataHelperRike<Milestone>(Milestone.class).find(data.getRequestAttribute("milestone")));
                task.setArtifact(new DataHelperRike<Artifact>(Artifact.class).find(data.getRequestAttribute("artifact")));

                task.setDescription(data.getRequestAttribute("description"));

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
                int priority = Integer.parseInt(GlobalConfig.get(PRIORITY_NORMAL));

                try {
                    priority = Integer.valueOf(data.getRequestAttribute("priority"), 10);
                } catch (Exception ignored) {
                }

                task.setPriority(priority);
                task.setRated(new Date());
                task.setRatedBy(user);
                task.setStatus(Task.Status.OPEN);
                if(GlobalConfig.get(WORKFLOW_TYPE).equalsIgnoreCase("arago Technologies")&&priority==1) {
                    GregorianCalendar c = new GregorianCalendar();
                    c.setTime(task.getRated());
                    c.add(Calendar.DAY_OF_MONTH, Integer.parseInt(GlobalConfig.get(WORKFLOW_DAYS_TOP_PRIO_TASK)));
                    task.setDueDate(c.getTime());
                }

                TaskHelper.save(task);

                StatisticHelper.update();

                data.setSessionAttribute("task", task);

                HashMap<String, Object> notificationParam = new HashMap<String, Object>();

                notificationParam.put("id", data.getRequestAttribute("id"));
                data.setEvent("TaskUpdateNotification", notificationParam);

                data.removeSessionAttribute("targetView");

                ActivityLogHelper.log(" rated Task #" + task.getId() + " <a href=\"/web/guest/rike/-/show/task/" + task.getId() + "\">" + StringEscapeUtils.escapeHtml(task.getTitle()) + "</a> ", task.getStatus(), SecurityHelper.getUserEmail(data.getUser()), data, task.toMap());
            }
        }
    }
}
