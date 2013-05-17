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

import de.arago.rike.data.DataHelperRike;
import de.arago.rike.data.Milestone;
import de.arago.rike.data.Task;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

/**
 *
 */
public class ZombieHelper {
    private static int asInt(Object o) {
        if (o == null) return 0;

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
    public static List<OverdueMilestone> getOverdueMilestones() {
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

            ret.add(new OverdueMilestone
                    (
                        asInt(a[0]),
                        asInt(a[1]),
                        asInt(a[2]),
                        helper.find(a[3].toString()))
                   );
        }
        Collections.sort(ret, new EtaSorter());
        return ret;
    }
    /*=======
    public class ZombieHelper {

        public static List<Milestone> getOverdueMilestones() {
            DataHelperRike<Milestone> helper = new DataHelperRike<Milestone>(Milestone.class);

            String str = "select id, (select sum(size_estimated) from tasks t where t.milestone_id = m.id and t.task_status != 'done') as hours_left from milestones m group by m.id having hours_left is not null;";

            System.err.println("querying");
            List<Milestone> list = helper.list();
            List<Milestone> result = new ArrayList<Milestone>();

            for (final Milestone o : list) {
                System.err.println(o.getTitle()+" "+o.getDays());
                if(o.getDays()>0)
                    result.add(o);
            }
            /*Criteria crit = helper.filter()
             .add(Restrictions.isNotNull("dueDate"))
             .add(Restrictions.lt("dueDate", new Date()))
             .add(Restrictions.ne("status", Task.Status.DONE.toString().toLowerCase()))
             .addOrder(Order.asc("dueDate"));
    >>>>>>> f2f509ff655c01c16e72d669cdb9c35b7799b66c

             return helper.list(crit);

            return result;
        }*/

    public static List<Task> getOverdueTasks() {
        DataHelperRike<Task> helper = new DataHelperRike<Task>(Task.class);
        Criteria crit = helper.filter()
                        .add(Restrictions.isNotNull("dueDate"))
                        .add(Restrictions.lt("dueDate", new Date()))
                        .add(Restrictions.ne("status", Task.Status.DONE.toString().toLowerCase()))
                        .addOrder(Order.asc("dueDate"));


        return helper.list(crit);
    }

    private static class EtaSorter implements Comparator<OverdueMilestone> {

        @Override
        public int compare(OverdueMilestone t, OverdueMilestone t1) {
            return t1.getLate()-t.getLate();
        }
    }
}
