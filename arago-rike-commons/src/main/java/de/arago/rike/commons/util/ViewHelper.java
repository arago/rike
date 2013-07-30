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

import de.arago.rike.commons.data.Artifact;
import de.arago.rike.commons.data.DataHelperRike;
import de.arago.rike.commons.data.GlobalConfig;
import de.arago.rike.commons.data.Milestone;
import de.arago.rike.commons.data.Task;
import de.arago.rike.commons.data.Task.Status;
import de.arago.rike.commons.data.TaskUser;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.apache.commons.lang.StringEscapeUtils;
import org.hibernate.criterion.Order;
import static de.arago.rike.commons.data.GlobalConfig.*;
import java.text.SimpleDateFormat;

public class ViewHelper {

    private ViewHelper() {
        //not called
    }

    private static final Map<String, String> priorityNames = new HashMap<String, String>();
    private static final List<String> priorities = new LinkedList<String>();
    private static final Map<String, String> statusNames = new HashMap<String, String>();
    private static final List<String> status = new LinkedList<String>();
    private static final Map<String, String> statusColors = new HashMap<String, String>();

    static {
        for (int i = 1; i <= Integer.parseInt(GlobalConfig.get(PRIORITY_MAXIMAL_NUMBER)); ++i) {
            priorities.add(i + "");
            priorityNames.put(i + "", i + "");
        }

        status.add(Status.UNKNOWN.toString());
        status.add(Status.OPEN.toString());
        status.add(Status.IN_PROGRESS.toString());
        status.add(Status.DONE.toString());

        statusNames.put(Status.UNKNOWN.toString(), "not rated");
        statusNames.put(Status.OPEN.toString(), "open");
        statusNames.put(Status.IN_PROGRESS.toString(), "in progress");
        statusNames.put(Status.DONE.toString(), "completed");

        statusColors.put(Status.UNKNOWN.toString(), "blue");
        statusColors.put(Status.OPEN.toString(), "red");
        statusColors.put(Status.IN_PROGRESS.toString(), "yellow");
        statusColors.put(Status.DONE.toString(), "green");
    }

    private static String encode(String what) {
        try {
            return URLEncoder.encode(what, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex);
        }
    }

    private static String escape(String what) {
        return StringEscapeUtils.escapeHtml(what);
    }

    public static String formatUser(String user) {
        if (user.contains("@")) {
            user = user.replaceAll("\\@.*$", "");
        }

        return escape(user);
    }

    public static String formatUser(Task task) {
        return formatUser(task.getOwner());
    }

    public static String formatURL(String path) {
        if (path == null || path.length() == 0) {
            return "";
        }

        try {
            URL url = new URL(path);
            return "<a target='_blank' title='" + escape(path) + "' href='" + escape(path) + "'>" + escape(url.getHost()) + "</a>";
        } catch (MalformedURLException ex) {
            return "<a target='_blank' href='" + escape(path) + "'>" + escape(path) + "</a>";
        }
    }

    public static List<String> getPriorities() {
        return priorities;
    }


    public static String getPriority(int what) {
        return priorityNames.get(what + "");
    }

    public static String getPriority(String what) {
        return priorityNames.get(what);
    }

    public static String getTaskStatusColorClass(Task task) {
        switch (task.getStatusEnum()) {
        case OPEN:
            return "status-critical";
        case IN_PROGRESS:
            return "status-warning";
        case DONE:
            return "status-ok";
        default:
            return "status-unknown";
        }
    }

    public static String getTaskPriorityColorClass(Task task) {

        int p = task.getPriority();

        if (p==1) {
            return "priority-high";
        } else if (p <= Integer.parseInt(GlobalConfig.get(PRIORITY_NORMAL))) {
            return "priority-normal";
        } else {
            return "priority-low";
        }
    }

    public static List<TaskUser> getAvailableUsers() {
        DataHelperRike<TaskUser> helper = new DataHelperRike<TaskUser>(TaskUser.class);
        return helper.list(helper.filter().addOrder(Order.asc("email")));
    }

    public static List<String> getAvailableReleases() {
        DataHelperRike<Milestone> helper = new DataHelperRike<Milestone>(Milestone.class);

        List<String> releases = new ArrayList<String>();

        for (Milestone m : helper.list()) {
            if (m.getRelease() != null && !m.getRelease().isEmpty() && !releases.contains(m.getRelease())) {
                releases.add(m.getRelease());
            }
        }

        Collections.sort(releases);

        return releases;
    }

    public static List<String[]> getAvailableMilestones() {
        List<String[]> data = new ArrayList<String[]>();

        List<Milestone> list = MilestoneHelper.list();

        Set<String> releases = new TreeSet<String>();

        for (Milestone m : list) {
            String id = m.getRelease();
            if (!id.isEmpty()&&!releases.contains(id)) {
                releases.add(id);
                data.add(new String[] {"release_" + id, "[RELEASE] " + id});
            }

            String dateString = "[?]";
            if(m.getDueDate()!=null) {
                SimpleDateFormat f = new SimpleDateFormat("[dd.MM.yyyy] ");
                dateString = f.format(m.getDueDate());
            }

            data.add(new String[] {"milestone_" + m.getId().toString(), dateString + m.getTitle()});
        }
        return data;
    }

    public static int getDayDifference(Date date) {
        return (int) Math.ceil((date.getTime() - new Date().getTime()) / (1000 * 60 * 60 * 24)) ;
    }

    public static List<Artifact> getAvailableArtifacts() {
        DataHelperRike<Artifact> helper = new DataHelperRike<Artifact>(Artifact.class);
        return helper.list(helper.filter().addOrder(Order.asc("name")));
    }

    public static List<String> getStatus() {
        return status;
    }

    public static String getStatus(Status what) {
        return getStatus(what.toString());
    }

    public static String getStatus(String what) {
        return statusNames.get(what.toUpperCase());
    }

    public static String getColor(String what) {
        return statusColors.get(what.toUpperCase());
    }

    public static int asInt(Object o) {
        if (o == null) return 0;

        if (o instanceof BigInteger) {
            return ((BigInteger) o).intValue();
        }

        if (o instanceof BigDecimal) {
            return ((BigDecimal) o).intValue();
        }

        if (o instanceof String) {
            try {
                return Integer.valueOf(o.toString(), 10);
            } catch(NumberFormatException ignored) {
                return 0;
            }
        }

        return (Integer) o;
    }
}
