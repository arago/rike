/**
 * Copyright (c) 2010 arago AG, http://www.arago.de/
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the
 * Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package de.arago.rike.util;

import de.arago.data.IDataWrapper;
import de.arago.portlet.util.SecurityHelper;
import de.arago.rike.data.Artifact;
import de.arago.rike.data.DataHelperRike;
import de.arago.rike.data.Milestone;
import de.arago.rike.data.Task;
import de.arago.rike.data.ActivityLog;
import de.arago.rike.data.Dependency;
import de.arago.rike.data.TaskUser;
import java.util.HashMap;

import java.util.List;
import org.apache.commons.lang.StringEscapeUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

public class TaskHelper {

    private static final String POST_HOOK = System.getProperty(TaskHelper.class.getName() + ".postLogHook", "").trim();
    public static final long OTHER_ARTEFACT_ID = 18;
    private static final int hourOffsetToStartTask;

    static {
        try {
            hourOffsetToStartTask = Integer.valueOf(System.getProperty("de.arago.rike.timeOffset", "0"), 10) * 60 * 60 * 1000;
        } catch (Exception ex) {
            throw new ExceptionInInitializerError(ex);
        }
    }

    private static DataHelperRike<Task> taskHelper() {
        return new DataHelperRike<Task>(Task.class);
    }

    private static Order getOrderFromFilter(TaskListFilter filter) {
        if (filter.getSortDirection() == TaskListFilter.SortDirection.ASC) {
            return Order.asc(filter.getSortField().toString().toLowerCase());
        } else {
            return Order.desc(filter.getSortField().toString().toLowerCase());
        }
    }

    private static void mungeFilter(Criteria crit, TaskListFilter filter) {
        if (filter.getUser().length() > 0) {
            crit.add(Restrictions.eq("owner", filter.getUser()));
        }

        if (filter.getCreator().length() > 0) {
            crit.add(Restrictions.eq("creator", filter.getCreator()));
        }

        if (filter.getMilestone().length() > 0) {
            String tmp = filter.getMilestone();

            if (tmp.startsWith("milestone_")) {
                Milestone m = new DataHelperRike<Milestone>(Milestone.class).find(filter.getMilestone().substring(10));

                crit.add(Restrictions.eq("milestone", m));
            } else if (tmp.startsWith("release_")) {
                DataHelperRike<Milestone> milestoneHelper = new DataHelperRike<Milestone>(Milestone.class);

                List<Milestone> list = milestoneHelper.list(milestoneHelper.filter().add(Restrictions.eq("release", filter.getMilestone().substring(8))));

                crit.add(Restrictions.in("milestone", list));
            }
        }

        if (filter.getArtifact().length() > 0) {
            Artifact a = new DataHelperRike<Artifact>(Artifact.class).find(filter.getArtifact());
            crit.add(Restrictions.eq("artifact", a));
        }

        if (filter.getStatus().length() > 0) {
            crit.add(Restrictions.eq("status", filter.getStatus()));
        }

        if (filter.getPriority().length() > 0) {
            crit.add(Restrictions.eq("priority", Integer.valueOf(filter.getPriority(), 10)));
        }
    }

    public static List<Task> getAllTasks(TaskListFilter filter) {
        DataHelperRike<Task> helper = taskHelper();

        Criteria crit = helper.filter().addOrder(getOrderFromFilter(filter)).addOrder(Order.asc("title"));

        mungeFilter(crit, filter);

        return helper.list(crit);
    }

    public static List<ActivityLog> getRecentActivityLogs() {
        DataHelperRike<ActivityLog> helper = new DataHelperRike<ActivityLog>(ActivityLog.class);

        return helper.list(helper.filter().addOrder(Order.desc("id")).setMaxResults(30));
    }

    public static List<Task> getTasksInProgressForUser(String user) {
        DataHelperRike<Task> helper = taskHelper();

        return helper.list(helper.filter().add(Restrictions.eq("owner", user)).add(Restrictions.eq("status", Task.Status.IN_PROGRESS.toString().toLowerCase())));
    }

    public static Task getTask(Long id) {
        try {
            return taskHelper().find(id);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static Task getTask(String id) {
        return getTask(Long.valueOf(id));
    }

    public static Task save(Task task) {
        return taskHelper().save(task);
    }

    public static TaskUser checkIfUserExists(String user) {
        DataHelperRike<TaskUser> helper = new DataHelperRike<TaskUser>(TaskUser.class);
        List<TaskUser> list = helper.list(helper.filter().add(Restrictions.eq("email", user)));

        if (list.isEmpty()) {
            TaskUser newUser = new TaskUser();
            newUser.setEmail(user);
            newUser.setLast_ms("");

            helper.save(newUser);

            ActivityLogHelper.log(" joined", "unknown", user, null, new HashMap());

            return newUser;
        } else {
            return list.get(0);
        }
    }

    public static boolean changeAccount(String user, long change) {
        try {
            TaskUser data = checkIfUserExists(user);

            if (data != null) {
                System.err.println("{balance} " + user + " balance was: " + data.getAccount() + " and will be set to " + (data.getAccount() + change));
                data.setAccount(data.getAccount().longValue() + change);
                new DataHelperRike<TaskUser>(TaskUser.class).save(data);

                return true;
            }
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
        return false;
    }

    public static boolean canDoTask(String user, Task task) {
        if (task.getStatusEnum() != Task.Status.OPEN) {
            return false;
        }

        if (!task.getRatedBy().equals(user)) {
            return true;
        }

        if (task.getCreated().getTime() < (System.currentTimeMillis() - hourOffsetToStartTask)) {
            return true;
        }

        return false;
    }

    public static void changeConnections(IDataWrapper data, boolean isCreate) {
        String user = SecurityHelper.getUserEmail(data.getUser());
        String fromstr = data.getRequestAttribute("from");
        String tostr = data.getRequestAttribute("to");
        if (fromstr != null && tostr != null) {
            long from = Long.parseLong(fromstr);
            long to = Long.parseLong(tostr);
            DataHelperRike<Dependency> helper = new DataHelperRike<Dependency>(Dependency.class);
            Criteria crit = helper.filter();

            crit.add(Restrictions.eq("premise", isCreate ? to : from));
            crit.add(Restrictions.eq("sequel", isCreate ? from : to));

            for (Dependency dependency : helper.list(crit)) {
                helper.kill(dependency);
            }

            String message = " removed";
            if (isCreate) {
                Dependency dependency = new Dependency();
                dependency.setPremise(from);
                dependency.setSequel(to);

                helper.save(dependency);
                message = " added";
            }

            HashMap map = new HashMap();
            map.put("type", "Dependency");
            map.put("from", fromstr);
            map.put("to", tostr);
            map.put("message", message);

            ActivityLogHelper.log(message + " dependency Task <a href=\"/web/guest/rike/-/show/task/" + to + "\">#" + to + "</a>"
                    + " from Task <a href=\"/web/guest/rike/-/show/task/" + from + "\">#" + from + "</a>", "unknown",
                    user, data, map);
        }
    }
}
