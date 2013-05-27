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
package de.arago.rike.util;

import de.arago.data.IDataWrapper;
import de.arago.rike.data.Artifact;
import de.arago.rike.data.DataHelperRike;
import de.arago.rike.data.Milestone;
import de.arago.rike.data.Task;
import de.arago.rike.data.TaskLog;
import de.arago.rike.data.TaskUser;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
        } catch(Exception ex) {
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

    public static List<TaskLog> getRecentTaskLogs() {
        DataHelperRike<TaskLog> helper = new DataHelperRike<TaskLog>(TaskLog.class);

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
            return newUser;
        } else
            return list.get(0);
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
            e.printStackTrace();
        }
        return false;
    }

    public static boolean canDoTask(String user, Task task) {
        if (task.getStatusEnum() != Task.Status.OPEN) return false;

        if (!task.getRatedBy().equals(user)) return true;

        if (task.getCreated().getTime() < (System.currentTimeMillis() - hourOffsetToStartTask)) return true;

        return false;
    }

    public static void log(String content, Task task, String user, IDataWrapper data) {
        final TaskLog log = new TaskLog();

        log.setContent(content);
        log.setUser(user);
        log.setCreated(new Date());
        log.setStatus(task.getStatus());

        new DataHelperRike<TaskLog>(TaskLog.class).save(log);

        HashMap<String, Object> notificationParam = new HashMap<String, Object>();

        notificationParam.put("id", task.getId().toString());
        data.setEvent("TaskLogNotification", notificationParam);

        runPostHook(log);
    }

    private static void runPostHook(final TaskLog log) {
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
