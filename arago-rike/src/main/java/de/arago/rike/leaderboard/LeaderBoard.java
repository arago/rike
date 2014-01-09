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
package de.arago.rike.leaderboard;

import de.arago.portlet.AragoPortlet;
import de.arago.portlet.util.SecurityHelper;
import de.arago.data.IDataWrapper;

import de.arago.rike.commons.data.DataHelperRike;
import de.arago.rike.commons.data.GlobalConfig;
import static de.arago.rike.commons.data.GlobalConfig.CHECK_PERIOD_SECONDS;
import de.arago.rike.commons.data.TaskUser;
import java.math.BigInteger;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import org.hibernate.criterion.Restrictions;
import static de.arago.rike.commons.data.GlobalConfig.PRIORITY_MAXIMAL_NUMBER;

public class LeaderBoard extends AragoPortlet {
    final static String query_now =
        "SELECT owner, priority, count( id ) FROM tasks " +
        "WHERE END >= DATE_SUB( CURDATE( ) , INTERVAL 7 DAY ) " +
        "GROUP BY owner, priority";
    final static String query_last =
        "SELECT owner, priority, count( id ) FROM tasks " +
        "WHERE END >= DATE_SUB( CURDATE( ) , INTERVAL 14 DAY ) " +
        "AND END < DATE_SUB( CURDATE( ) , INTERVAL 7 DAY ) " +
        "GROUP BY owner, priority";

    @Override
    protected boolean checkViewData(IDataWrapper data) {
        if (!SecurityHelper.isLoggedIn(data.getUser())) return false;

        Long nextUpdate = (Long) data.getSessionAttribute("nextUpdate");
        if(nextUpdate==null||nextUpdate<System.currentTimeMillis()||data.getSessionAttribute("list")==null) {
            data.setSessionAttribute("nextUpdate",
                                     System.currentTimeMillis() + Long.parseLong(GlobalConfig.get(CHECK_PERIOD_SECONDS))*1000);
            data.setSessionAttribute("list", getData());
        }

        return true;
    }

    public static List<TaskUser> getData() {
        DataHelperRike<TaskUser> helper = new DataHelperRike<TaskUser>(TaskUser.class);
        List<TaskUser> list = helper.list(helper.filter().add(Restrictions.eq("isDeleted", 0)));
        HashMap<String,TaskUser> map = new HashMap<String,TaskUser>();
        int priorities = Integer.parseInt(GlobalConfig.get(PRIORITY_MAXIMAL_NUMBER));
        for(TaskUser task:list) {
            map.put(task.getEmail(), task);
            task.setEndedTasks(new int[priorities]);
        }

        fillTasksCount(helper, map, query_last);
        Collections.sort(list, new UserComparator());

        int i=1;
        for(TaskUser task:list) {
            task.setYesterday(new Long(i++));
            task.setEndedTasks(new int[priorities]);
        }

        fillTasksCount(helper, map, query_now);
        Collections.sort(list, new UserComparator());

        i=1;
        for(TaskUser task:list) {
            task.setAccount(new Long(i++));
        }

        return list;
    }

    private static void fillTasksCount(DataHelperRike<TaskUser> helper,HashMap<String,TaskUser> map, String query) {
        List<Object> values = helper.list(helper.createSQLQuery(query));

        for (final Object o: values) {
            Object[] a = (Object[]) o;
            String email = a[0].toString();
            Integer prio = (Integer)a[1];
            BigInteger count = (BigInteger)a[2];
            if(map.containsKey(email)) {
                int[] points = map.get(email).getEndedTasks();
                if(prio<1)
                    prio = 1;
                if(prio>points.length)
                    prio = points.length;
                points[prio-1] = (int) count.longValue();
            }
        }
    }

    private static class UserComparator implements Comparator<TaskUser> {

        @Override
        public int compare(TaskUser t, TaskUser t1) {
            for(int i=0; i<t.getEndedTasks().length; i++) {
                if(t.getEndedTasks()[i]!=t1.getEndedTasks()[i])
                    return t1.getEndedTasks()[i]-t.getEndedTasks()[i];
            }
            return (int) (t1.getId()-t.getId());
        }
    }

}
