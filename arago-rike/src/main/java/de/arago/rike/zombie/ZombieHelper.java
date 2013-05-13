/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.arago.rike.zombie;

import de.arago.rike.data.DataHelperRike;
import de.arago.rike.data.Milestone;
import de.arago.rike.data.Task;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

/**
 *
 */
public class ZombieHelper
{
  public static List<Milestone> getOverdueMilestones()
  {
    DataHelperRike<Milestone> helper = new DataHelperRike<Milestone>(Milestone.class);
    
    String str = "select id, (select sum(size_estimated) from tasks t where t.milestone_id = m.id and t.task_status != 'done') as hours_left from milestones m group by m.id having hours_left is not null;";
    
    System.err.println("querying");
    
      List<Object> list = helper.list(helper.createSQLQuery(str));
      
      for (final Object o: list)
      {
        System.err.println(o);
      }  
    /*Criteria crit = helper.filter()
    .add(Restrictions.isNotNull("dueDate"))
    .add(Restrictions.lt("dueDate", new Date()))
    .add(Restrictions.ne("status", Task.Status.DONE.toString().toLowerCase()))
    .addOrder(Order.asc("dueDate"));
    
    return helper.list(crit);*/
    
    return Collections.EMPTY_LIST;
  }  
  
  public static List<Task> getOverdueTasks()
  {
    DataHelperRike<Task> helper = new DataHelperRike<Task>(Task.class);
    Criteria crit = helper.filter()
                          .add(Restrictions.isNotNull("dueDate"))
                          .add(Restrictions.lt("dueDate", new Date()))
                          .add(Restrictions.ne("status", Task.Status.DONE.toString().toLowerCase()))
                          .addOrder(Order.asc("dueDate"));
    
    
    return helper.list(crit);
  }  
}
