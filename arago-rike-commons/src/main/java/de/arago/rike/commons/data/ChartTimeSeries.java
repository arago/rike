/**
 * Copyright (c) 2010 arago AG, http://www.arago.de/
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package de.arago.rike.commons.data;

import de.arago.rike.commons.util.ViewHelper;
import java.util.Map.Entry;
import java.util.*;
import net.minidev.json.JSONArray;
import org.hibernate.Hibernate;

public class ChartTimeSeries {

    private ChartTimeSeries() {
        //not called
    }

    public final static String releaseTasksStatus =
        "SELECT sum(summe_size) as value, task_status as name, moment "
        + "FROM task_stat,milestones "
        + "WHERE milestone_id=milestones.id AND milestones.release_name=? "
        + "GROUP BY task_status, moment";
    public final static String milestoneTasksStatus =
        "SELECT sum(summe_size) as value, task_status as name, moment "
        + "FROM task_stat "
        + "WHERE milestone_id = ? "
        + "GROUP BY task_status, moment";
    public final static String allTasksStatus =
        "SELECT sum(summe_size) as value, task_status as name, moment "
        + "FROM task_stat "
        + "GROUP BY task_status, moment";
    public final static String milestoneBurndown =
        "SELECT sum( summe_size ) as value, milestones.title as name, moment "
        + "FROM task_stat, milestones "
        + "WHERE milestone_id = milestones.id "
        + "AND task_status != 'done' "
        + "AND due_date IS NOT NULL "
        + "and milestones.id = ? "
        + "GROUP BY milestones.title, moment "
        + "ORDER BY due_date, moment";
    public final static String allBurndown =
        "SELECT sum( summe_size ) as value, milestones.release_name as name, moment "
        + "FROM task_stat, milestones "
        + "WHERE milestone_id = milestones.id "
        + "AND task_status != 'done' "
        + "AND due_date IS NOT NULL "
        + "GROUP BY milestones.release_name, moment "
        + "ORDER BY moment";
    public final static String releaseBurndown =
        "SELECT sum( summe_size ) as value, milestones.title as name, moment "
        + "FROM task_stat, milestones "
        + "WHERE milestone_id = milestones.id "
        + "AND task_status != 'done' "
        + "AND milestones.release_name=? "
        + "AND due_date IS NOT NULL "
        + "GROUP BY milestones.title, moment "
        + "ORDER BY due_date, moment";

    public static Map<String, List<List<Long>>> query(String str, Object[] parameters) {
        DataHelperRike<Object> helper = new DataHelperRike<Object>(Object.class);
        org.hibernate.SQLQuery query = helper.createSQLQuery(str).addScalar("name", Hibernate.STRING).addScalar("value", Hibernate.LONG).addScalar("moment", Hibernate.DATE);

        if (parameters != null) {
            for (int i = 0; i < parameters.length; ++i) {
                query.setParameter(i, parameters[i]);
            }

        }

        Map<String, List<List<Long>>> data = new LinkedHashMap<String, List<List<Long>>>();
        for (Object first : query.list()) {
            Object[] arr = (Object[]) first;
            String name = (String) arr[0];
            Long value = (Long) arr[1];
            Date moment = (Date) arr[2];
            List<List<Long>> ts;
            if (!data.containsKey(name)) {
                ts = new LinkedList<List<Long>>();
                data.put(name, ts);
            } else {
                ts = data.get(name);
            }
            List<Long> tmp = new ArrayList<Long>(2);
            tmp.add(moment.getTime());
            tmp.add(value);
            ts.add(tmp);
        }

        helper.finish(query);
        return data;
    }

    public static List<Map<String, Object>> taskStatusJSON(String query, Object[] parameters) {
        Map<String, List<List<Long>>> data = query(query, parameters);
        ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>(data.size());
        List<String> stats = new ArrayList<String>(ViewHelper.getStatus());
        Collections.reverse(stats);
        for (String u : stats) {
            String name = u.toLowerCase();
            if (data.containsKey(name)) {
                TreeMap<String, Object> map = new TreeMap<String, Object>();
                map.put("label", ViewHelper.getStatus(name));
                map.put("key", name);
                map.put("color", ViewHelper.getColor(name));
                map.put("data", data.get(name));
                list.add(map);
            }
        }
        return list;
    }

    private static void stackData(List<Map<String, Object>> list) {
        TreeMap<Long, Long> dates = new TreeMap<Long, Long>();
        for (Map<String, Object> m : list) {
            List<List<Long>> tmp = (List<List<Long>>) m.get("data");
            for (List<Long> l : tmp) {
                Long sum;
                if (dates.containsKey(l.get(0))) {
                    sum = dates.get(l.get(0));
                } else {
                    sum = Long.valueOf(0);
                }
                sum = Long.valueOf(sum.longValue() + l.get(1).longValue());
                l.set(1, sum);
                dates.put(l.get(0), sum);
            }
        }
    }

    private static void fillEmptyValues(List<Map<String, Object>> list) {
        //Hier werden alle Daten gespeichert
        TreeSet<Long> allDates = new TreeSet<Long>();
        for (Map<String, Object> m : list) {
            List<List<Long>> tmp = (List<List<Long>>) m.get("data");
            for (List<Long> l : tmp) {
                allDates.add(l.get(0));
            }
        }
        //Schleife sucht alle Daten(Datum)
        for (Map<String, Object> m : list) {
            //Hier werden fehlende Daten zur "list" hinzugefügt
            //Existierende Daten für eine Farbe,die an der Reihe ist.
            TreeMap<Long, Long> fullTimeseries = new TreeMap<Long, Long>();
            List<List<Long>> tmp = (List<List<Long>>) m.get("data");
            for (List<Long> l : tmp) {
                fullTimeseries.put(l.get(0), l.get(1));
            }
            for (Long keyAkt : allDates) {
                if (!fullTimeseries.containsKey(keyAkt)) {
                    fullTimeseries.put(keyAkt, 0l);
                }
            }
            tmp.clear();
            for (Entry<Long, Long> keyAkt2 : fullTimeseries.entrySet()) {
                List<Long> lg = new ArrayList<Long>();
                lg.add(keyAkt2.getKey());
                lg.add(keyAkt2.getValue());
                tmp.add(lg);
            }
        }
    }

    private static void clearData(List<Map<String, Object>> list) {
        TreeMap<Long, Long> dates = new TreeMap<Long, Long>();
        for (Map<String, Object> m : list) {
            List<List<Long>> tmp = (List<List<Long>>) m.get("data");
            for (List<Long> l : tmp) {
                Long sum;
                if (dates.containsKey(l.get(0))) {
                    sum = Long.valueOf(dates.get(l.get(0)).longValue() + 1);
                } else {
                    sum = Long.valueOf(0);
                }
                dates.put(l.get(0), sum);
            }
        }
        List<List<Long>> toRemove = new ArrayList<List<Long>>(dates.size());
        for (Map<String, Object> m : list) {
            List<List<Long>> tmp = (List<List<Long>>) m.get("data");
            toRemove.clear();
            for (List<Long> l : tmp) {
                Long sum = dates.get(l.get(0));
                if (sum.longValue() == 0) {
                    toRemove.add(l);
                }
            }
            tmp.removeAll(toRemove);
        }
    }

    public static List<Map<String, Object>> toBurndownJSON(String query, Object[] parameters) {
        Map<String, List<List<Long>>> data = query(query, parameters);
        ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>(data.size());
        for (Entry<String, List<List<Long>>> e : data.entrySet()) {
            TreeMap<String, Object> map = new TreeMap<String, Object>();
            map.put("label", e.getKey());
            map.put("key", e.getKey());
            map.put("data", e.getValue());
            list.add(map);
        }
        return list;
    }

    public static String toPrettyJSON(String type, String milestone) {
        List<Map<String, Object>> list = null;
        if (type.equals("burndown")) {
            if (milestone.startsWith("release_")) {
                list = toBurndownJSON(releaseBurndown, new Object[] {milestone.substring(8)});
            } else if (milestone.startsWith("milestone_")) {
                list = toBurndownJSON(milestoneBurndown, new Object[] {milestone.substring(10)});
            } else {
                list = toBurndownJSON(allBurndown, null);
            }
        } else if (type.equals("taskstatus")) {
            if (milestone == null || milestone.isEmpty()) {
                list = taskStatusJSON(allTasksStatus, null);
            } else {
                if (milestone.startsWith("milestone_")) {
                    list = taskStatusJSON(milestoneTasksStatus, new Object[] {milestone.substring(10)});
                } else if (milestone.startsWith("release_")) {
                    list = taskStatusJSON(releaseTasksStatus, new Object[] {milestone.substring(8)});
                }
            }
        }
        if (list == null) {
            return "";
        }
        if (type.equals("taskstatus")) {
            clearData(list);
            fillEmptyValues(list);
        }
        stackData(list);
        return JSONArray.toJSONString(list);
    }
}
