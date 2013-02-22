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
package de.arago.rike.data;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import de.arago.rike.util.ViewHelper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import net.minidev.json.JSONArray;

import org.hibernate.Hibernate;

public class ChartTimeSeries {

    //private static final SessionFactory factory;
    public static String releaseTasksStatus =
            "SELECT sum(summe_size) as value, task_status as name, moment "
            + "FROM task_stat,milestones "
            + "WHERE milestone_id=milestones.id AND milestones.release_name=? "
            + "GROUP BY task_status, moment";
    public static String milestoneTasksStatus =
            "SELECT sum(summe_size) as value, task_status as name, moment "
            + "FROM task_stat "
            + "WHERE milestone_id = ? "
            + "GROUP BY task_status, moment";
    public static String allTasksStatus =
            "SELECT sum(summe_size) as value, task_status as name, moment "
            + "FROM task_stat "
            + "GROUP BY task_status, moment";
    public static String milestoneBurndown =
            "SELECT sum( summe_size ) as value, milestones.title as name, moment "
            + "FROM task_stat, milestones "
            + "WHERE milestone_id = milestones.id "
            + "AND task_status != 'done' "
            + "AND due_date IS NOT NULL "
            + "GROUP BY milestones.title, moment "
            + "ORDER BY due_date, moment";
    public static String allBurndown =
            "SELECT sum( summe_size ) as value, milestones.release_name as name, moment "
            + "FROM task_stat, milestones "
            + "WHERE milestone_id = milestones.id "
            + "AND task_status != 'done' "
            + "AND due_date IS NOT NULL "
            + "GROUP BY milestones.release_name, moment "
            + "ORDER BY moment";
    public static String releaseBurndown =
            "SELECT sum( summe_size ) as value, milestones.title as name, moment "
            + "FROM task_stat, milestones "
            + "WHERE milestone_id = milestones.id "
            + "AND task_status != 'done' "
            + "AND milestones.release_name=? "
            + "AND due_date IS NOT NULL "
            + "GROUP BY milestones.title, moment "
            + "ORDER BY due_date, moment";

    public static Map<String, List<List<Long>>> query(String str, Object[] parameters) {
        //Session s = factory.getCurrentSession();
        //Transaction tr = s.beginTransaction();
        DataHelperRike<Object> helper = new DataHelperRike<Object>(Object.class);
        org.hibernate.SQLQuery query = helper.createSQLQuery(str)
                .addScalar("name", Hibernate.STRING)
                .addScalar("value", Hibernate.LONG)
                .addScalar("moment", Hibernate.DATE);

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
        //tr.commit();
        return data;
    }

    public static List<Map<String, Object>> taskStatusJSON(String query, Object[] parameters) {
        Map<String, List<List<Long>>> data = query(query, parameters);
        ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>(data.size());
        List<String> stats = new ArrayList<String>(ViewHelper.getStatus());
        Collections.reverse(stats);
        for (String upper_name : stats) {
            String name = upper_name.toLowerCase();
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

    
	private static void stackData(List<Map<String,Object>> list){
		TreeMap<Long,Long> dates = new TreeMap<Long,Long>();
		for(Map<String,Object> m:list){
			List<List<Long>> tmp = (List<List<Long>>)m.get("data");
			for(List<Long> l:tmp){
				Long sum;
				if(dates.containsKey(l.get(0))) {
                                sum = dates.get(l.get(0));
                                    }
				else {
                                sum = new Long(0);
                                    }
				sum = new Long(sum.longValue()+l.get(1).longValue());
				l.set(1, sum);
				dates.put(l.get(0),sum);
			}
		}
	}
        
 /*       public static class BehalterMap{
            public long fGr;
            public long fGe;
            public long fRo;
            public long fBl; 
            public BehalterMap(long grün, long gelb, long rot, long blau){
                this.fGr=grün;
                this.fGe=gelb;
                this.fRo=rot;
                this.fBl=blau;
            }           
            public String toString()
            {
                return fGr + ", " + fGe+ ", "+ fRo+", "+fBl;
            }        
        }
        
	static void printDataAsTable(List<Map<String,Object>> data){
            TreeMap <Date,BehalterMap> ersteZeile = new TreeMap <Date,BehalterMap>();
            BehalterMap bh;
                for(Map<String,Object> m:data){
                        List<List<Long>> tmp = (List<List<Long>>)m.get("data");
                        long grün=0,gelb=0,rot=0,blau=0;
                        Object c = m.get("color");
                        String cl=c.toString();
                        for(List<Long> l:tmp){
                            Date d = new Date();
                            d.setTime(l.get(0));
                            if("green".equals(cl))
                                grün=l.get(1);
                            else if("yellow".equals(cl))
                                gelb=l.get(1);
                            else if("red".equals(cl))
                                rot=l.get(1);
                            else if("blue".equals(cl))
                                blau=l.get(1);
                            
                    if(ersteZeile.containsKey(d))
                    {
                      bh = ersteZeile.get(d);
                      bh.fGr+=grün;
                      bh.fGe+=gelb; 
                      bh.fRo+=rot;
                      bh.fBl+=blau;
                    } else{
                      bh=new BehalterMap(grün,gelb,rot,blau);  
                    }   
                    
                    ersteZeile.put(d,bh);
                     }

                }
            SimpleDateFormat sdf = new SimpleDateFormat();
            sdf.applyPattern( "d. MMM yyyy" );
             for(Map.Entry<Date,BehalterMap> entry : ersteZeile.entrySet()){
                 System.out.println(sdf.format(entry.getKey())+"\t "+ entry.getValue());
             }
}*/
       
        public static List<Map<String,Object>>fillEmptyValues(List<Map<String,Object>> list){
            //Hier werden alle Daten gespeichert
            TreeSet<Long> DatumMap2= new TreeSet<Long>();
            for(Map<String,Object> m:list){
                List<List<Long>> tmp= (List<List<Long>>)m.get("data");
                Object c = m.get("color");
                for(List<Long>l :tmp){
                    long d=l.get(0);
                    if(DatumMap2.equals(d)){}
                    else{
                       DatumMap2.add(d);
                    }
                 }
            }  
            //Schleife sucht alle Daten(Datum)
            for(Map<String,Object> m:list){                             
            //Hier werden fehlende Daten zur "list" hinzugefügt
                Object c = m.get("color");
                //Existierende Daten für eine Farbe,die an der Reihe ist.
                TreeMap<Long,Long>DatumExist=new TreeMap<Long,Long>();
                List<List<Long>> tmp= (List<List<Long>>)m.get("data");                
                for(List<Long> l:tmp){
                    if (DatumExist.containsKey(l.get(0))){}
                    else{
                        DatumExist.put(l.get(0),l.get(1));}
                }                                
                long k=0;
                TreeMap<Long,Long> ListeFull = new TreeMap<Long,Long>();                    
                for(Long keyAkt: DatumMap2){
                    if(DatumExist.containsKey(keyAkt)){
                        ListeFull.put(keyAkt, DatumExist.get(keyAkt));}
                    else{
                        ListeFull.put(keyAkt, k);}
                }
                tmp.clear();
                for(Long keyAkt2:DatumMap2){
                        List<Long> lg=new ArrayList<Long>();
                        lg.add(keyAkt2);
                        lg.add(ListeFull.get(keyAkt2));
                        tmp.add(lg);
                }             
            }
            return list;
        }
        
	private static void clearData(List<Map<String,Object>> list){
		TreeMap<Long,Long> dates = new TreeMap<Long,Long>();
		for(Map<String,Object> m:list){
			List<List<Long>> tmp = (List<List<Long>>)m.get("data");
			for(List<Long> l:tmp){
				Long sum;
				if(dates.containsKey(l.get(0)))
					sum = new Long(dates.get(l.get(0)).longValue()+1);
				else
					sum = new Long(0);
				dates.put(l.get(0),sum);
			}
		}
		List<List<Long>> toRemove = new ArrayList<List<Long>>(dates.size());
		for(Map<String,Object> m:list){
			List<List<Long>> tmp = (List<List<Long>>)m.get("data");
			toRemove.clear();
			for(List<Long> l:tmp){
				Long sum = dates.get(l.get(0));
				if(sum.longValue()==0)
					toRemove.add(l);
			}
			tmp.removeAll(toRemove);
		}
	}

	public static List<Map<String,Object>> toBurndownJSON(String query, Object[] parameters){
		Map<String,List<List<Long>>> data = query(query,parameters);
		ArrayList<Map<String,Object>> list = new ArrayList<Map<String,Object>>(data.size());
		for(String name:data.keySet()){
			TreeMap<String,Object> map = new TreeMap<String, Object>();
			map.put("label",name);
      map.put("key", name);
			map.put("data",data.get(name));
			list.add(map);
		}
		return list;
	}
	
	public static String toPrettyJSON(String type, String milestone) {
		List<Map<String,Object>> list = null;
		if (type.equals("burndown")) {
			if (milestone.startsWith("release_"))
				list = toBurndownJSON(releaseBurndown, new Object[]{milestone.substring(8)});
			else if(milestone.startsWith("milestone_"))
				list = toBurndownJSON(milestoneBurndown, null);
			else
				list = toBurndownJSON(allBurndown, null);
		} else if (type.equals("taskstatus")) {
			if (milestone == null || milestone.isEmpty()) {
				list = taskStatusJSON(allTasksStatus, null);
			} else {
				if (milestone.startsWith("milestone_")) {
					list = taskStatusJSON(milestoneTasksStatus, new Object[]{milestone.substring(10)});
				} else if (milestone.startsWith("release_")) {
					list = taskStatusJSON(releaseTasksStatus, new Object[]{milestone.substring(8)});
				}
			}
		}
		if(list==null)
			return "";
		if (type.equals("taskstatus"))
			clearData(list);                
                list = fillEmptyValues(list);                               
		stackData(list);
		return JSONArray.toJSONString(list);
	}

}
