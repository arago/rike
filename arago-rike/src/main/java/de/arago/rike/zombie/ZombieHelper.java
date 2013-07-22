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
package de.arago.rike.zombie;

import de.arago.rike.commons.data.DataHelperRike;
import de.arago.rike.commons.data.Milestone;
import de.arago.rike.commons.data.Task;
import de.arago.rike.commons.util.ViewHelper;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minidev.json.JSONArray;
import org.apache.commons.lang.StringEscapeUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
/**
 *
 */
public class ZombieHelper {


    public static List<OverdueMilestone> getOverdueMilestones(boolean getAll) {
        DataHelperRike<Milestone> helper = new DataHelperRike<Milestone>(Milestone.class);

        String str = "select "
                     + "(select sum(size_estimated) from tasks t where t.milestone_id = m.id and t.task_status != 'done') as hours_left, "
                     + "(select sum(size_estimated) from tasks t where t.milestone_id = m.id and t.task_status = 'in_progress') as hours_in_progress, "
                     + "(select sum(size_estimated) from tasks t where t.milestone_id = m.id and t.task_status = 'done') as hours_done, "
                     + "id "
                     + " from milestones m where m.due_date is not null and m.performance > 0 group by m.id having hours_left > 0;";


        List<OverdueMilestone> ret = new ArrayList<OverdueMilestone>();
        List<Object> list          = helper.list(helper.createSQLQuery(str));

        for (final Object o: list) {
            Object[] a = (Object[]) o;
            OverdueMilestone ms = new OverdueMilestone
            (
                ViewHelper.asInt(a[0]),
                ViewHelper.asInt(a[1]),
                ViewHelper.asInt(a[2]),
                helper.find(a[3].toString()));
            if(ms.getLate()>0||getAll)
                ret.add(ms);
        }
        return ret;
    }

    public static List<OverdueMilestone> getOverdueMilestones() {
        List<OverdueMilestone> ret = getOverdueMilestones(false);
        Collections.sort(ret, new EtaSorter());
        return ret;
    }

    public static List<Task> getOverdueTasks() {
        DataHelperRike<Task> helper = new DataHelperRike<Task>(Task.class);
        Criteria crit = helper.filter()
                        .add(Restrictions.isNotNull("dueDate"))
                        .add(Restrictions.lt("dueDate", new Date()))
                        .add(Restrictions.ne("status", Task.Status.DONE.toString().toLowerCase()))
                        .addOrder(Order.asc("dueDate"));


        return helper.list(crit);
    }

    static String toPrettyJSON(List ticks) {
        List<OverdueMilestone> milestones = getOverdueMilestones(true);
        Collections.sort(milestones, new DoneSorter());
        final List data = new ArrayList();
        final Map open = new HashMap();
        final Map in_progress = new HashMap();
        final Map done = new HashMap();

        data.add(open);
        data.add(in_progress);
        data.add(done);

        open.put("Label", "open");
        open.put("color", "red");
        in_progress.put("Label", "in_progress");
        in_progress.put("color", "yellow");
        done.put("Label", "done");
        done.put("color", "green");

        List openData = new ArrayList();
        List in_progressData = new ArrayList();
        List doneData = new ArrayList();

        open.put("data", openData);
        in_progress.put("data", in_progressData);
        done.put("data", doneData);
        long now = new Date().getTime();

        int i = 1;

        for (OverdueMilestone o : milestones) {
            Milestone stone = o.getMilestone();

            ticks.add("<a href='/web/guest/rike/-/show/milestone/"+stone.getId()+"'>"+StringEscapeUtils.escapeHtml(stone.getTitle()) + "</a>");

            GregorianCalendar c = new GregorianCalendar();
            c.setTime(stone.getDueDate());
            c.add(Calendar.HOUR_OF_DAY, -((o.getWorkLeftInHours()-o.getWorkInProgressInHours()) * 7 * 24) / stone.getPerformance());
            long time1 = c.getTimeInMillis();
            c.add(Calendar.HOUR_OF_DAY, -(o.getWorkInProgressInHours() * 7 * 24) / stone.getPerformance());
            long time2 = c.getTimeInMillis();
            c.add(Calendar.HOUR_OF_DAY, -(o.getWorkDoneInHours() * 7 * 24) / stone.getPerformance());
            long time3 = c.getTimeInMillis();

            List item = new ArrayList();
            item.add(time1);
            item.add(i);
            item.add(stone.getDueDate().getTime());
            item.add("");
            openData.add(item);


            item = new ArrayList();
            item.add(time2);
            item.add(i);
            item.add(time1);
            item.add("");
            in_progressData.add(item);

            item = new ArrayList();
            item.add(time3);
            item.add(i);
            item.add(time2);
            item.add("");
            doneData.add(item);

            ++i;
        }

        return JSONArray.toJSONString(data);
    }

    private static class EtaSorter implements Comparator<OverdueMilestone> {

        @Override
        public int compare(OverdueMilestone t, OverdueMilestone t1) {
            return t1.getLate()-t.getLate();
        }
    }

    private static class DoneSorter implements Comparator<OverdueMilestone> {

        @Override
        public int compare(OverdueMilestone t, OverdueMilestone t1) {
            return t1.getMilestone().getDueDate().compareTo(t.getMilestone().getDueDate());
        }
    }
}
