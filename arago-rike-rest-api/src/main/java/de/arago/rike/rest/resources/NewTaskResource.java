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
package de.arago.rike.rest.resources;

import com.liferay.portal.model.User;
import de.arago.portlet.util.SecurityHelper;
import de.arago.rike.commons.data.Artifact;
import de.arago.rike.commons.data.DataHelperRike;
import de.arago.rike.commons.data.GlobalConfig;
import de.arago.rike.commons.data.Milestone;
import de.arago.rike.commons.data.Task;
import de.arago.rike.commons.util.ActivityLogHelper;
import de.arago.rike.commons.util.MilestoneHelper;
import de.arago.rike.commons.util.StatisticHelper;
import de.arago.rike.commons.util.TaskHelper;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang.StringEscapeUtils;
import org.restlet.data.Form;
import org.restlet.data.Status;
import org.restlet.ext.servlet.ServletUtils;
import org.restlet.representation.Representation;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;

import static de.arago.rike.commons.data.GlobalConfig.*;

public class NewTaskResource extends ServerResource 
{
  @Post(":json")
  public Map storeItem(Representation entity) throws IOException {
    User user = SecurityHelper.getUserFromRequest(ServletUtils.getRequest(getRequest()));
    
    if (user == null)
    {
      setStatus(Status.CLIENT_ERROR_FORBIDDEN);
      return Collections.EMPTY_MAP;
    }
    
    Form form = new Form(entity);
    Task task = create(form, user);
    
    if ("true".equals(getQuery().getFirstValue("doRateAndStartAndFinish")))
    {
      rateAndStartAndFinish(task, user, form);
    } else if ("true".equals(getQuery().getFirstValue("doRateAndStart"))) {
      rateAndStart(task, user);
    } else if ("true".equals(getQuery().getFirstValue("doRate"))) {
      rate(task, user);
    }
    
    setStatus(Status.SUCCESS_CREATED);
    
    final Map ret = new HashMap();
    
    ret.put("id", task.getId());
    
    return ret;
  }
  
  private Task create(Form form, User user)
  {
    Task task         = new Task();
    String email      = user.getEmailAddress();
    Artifact artifact = new DataHelperRike<Artifact>(Artifact.class).find(form.getFirstValue("artifact"));

    task.setTitle(form.getFirstValue("title"));
    task.setUrl(form.getFirstValue("url"));
    task.setArtifact(artifact);
    task.setCreated(new Date());
    task.setCreator(email);
    task.setDescription(form.getFirstValue("description"));

    task.setStatus(Task.Status.UNKNOWN);
    task.setMilestone(new DataHelperRike<Milestone>(Milestone.class).find(form.getFirstValue("milestone")));

    try {
        task.setSizeEstimated(Integer.valueOf(form.getFirstValue("size_estimated"), 10));
    } catch (Exception ignored) {
    }

    int priority = Integer.parseInt(GlobalConfig.get(PRIORITY_NORMAL));

    task.setPriority(priority);

    try 
    {
      SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
      task.setDueDate(format.parse(form.getFirstValue("due_date")));
    } catch(Exception ignored) {}

    TaskHelper.save(task);
    StatisticHelper.update();

    ActivityLogHelper.log(" created Task #" + task.getId() + " <a href=\"/web/guest/rike/-/show/task/" + task.getId() + "\">" + StringEscapeUtils.escapeHtml(task.getTitle()) + "</a>", task.getStatus(), email, null, task.toMap());
    
    return task;
  }

  private void rate(Task task, User user)
  {
    if (!rate(task, user.getEmailAddress()))
    {
      throw new IllegalStateException("could not rate task");
    }  
  }

  private void rateAndStart(Task task, User user)
  {
    rate(task, user);
    
    if (!start(task, user.getEmailAddress()))
    {
      throw new IllegalStateException("could not start task");
    }
  }

  private void rateAndStartAndFinish(Task task, User user, Form form)
  {
    rateAndStart(task, user);
    
    if (!finish(task, user.getEmailAddress(), form))
    {
      throw new IllegalStateException("could not finish task");
    }
  }
  
  private boolean start(Task task, String email)
  {
    if (TaskHelper.getTasksInProgressForUser(email).size() < Integer.parseInt(GlobalConfig.get(WORKFLOW_WIP_LIMIT))) 
    {
      if (!TaskHelper.canDoTask(email, task) || task.getStatusEnum() != Task.Status.OPEN) {
          return false;
      }

      task.setOwner(email);
      task.setStart(new Date());
      task.setStatus(Task.Status.IN_PROGRESS);
      
      if(GlobalConfig.get(WORKFLOW_TYPE).equalsIgnoreCase("arago Technologies")) {
          GregorianCalendar c = new GregorianCalendar();
          c.setTime(task.getStart());
          c.add(Calendar.DAY_OF_MONTH, Integer.parseInt(GlobalConfig.get(WORKFLOW_DAYS_TO_FINISH_TASK)));
          task.setDueDate(c.getTime());
      }

      TaskHelper.save(task);
      StatisticHelper.update();

      ActivityLogHelper.log(" started Task #" + task.getId() + " <a href=\"/web/guest/rike/-/show/task/" + task.getId() + "\">" + StringEscapeUtils.escapeHtml(task.getTitle()) + "</a> ", task.getStatus(), email, null, task.toMap());
      return true;
    }
    
    return false;
  }
  
  private boolean rate(Task task, String email)
  {
    if (task.getStatusEnum() == Task.Status.UNKNOWN || task.getStatusEnum() == Task.Status.OPEN) 
    {
      task.setRated(new Date());
      task.setRatedBy(email);
      task.setStatus(Task.Status.OPEN);
      
      if (GlobalConfig.get(WORKFLOW_TYPE).equalsIgnoreCase("arago Technologies") && task.getPriority()==1) {
          GregorianCalendar c = new GregorianCalendar();
          c.setTime(task.getRated());
          c.add(Calendar.DAY_OF_MONTH, Integer.parseInt(GlobalConfig.get(WORKFLOW_DAYS_TOP_PRIO_TASK)));
          task.setDueDate(c.getTime());
      }

      TaskHelper.save(task);
      StatisticHelper.update();

      ActivityLogHelper.log(" rated Task #" + task.getId() + " <a href=\"/web/guest/rike/-/show/task/" + task.getId() + "\">" + StringEscapeUtils.escapeHtml(task.getTitle()) + "</a> ", task.getStatus(), email, null, task.toMap());

      return true;
    }
    
    return false;
  }

  private boolean finish(Task task, String user, Form form)
  {
      if (task.getStatusEnum() == Task.Status.IN_PROGRESS && task.getOwner().equals(user)) {
          task.setEnd(new Date());
          int hours = Integer.valueOf(form.getFirstValue("hours_spent"), 10);
          task.setHoursSpent(hours);
          task.setStatus(Task.Status.DONE);

          TaskHelper.save(task);
          StatisticHelper.update();

          ActivityLogHelper.log(" completed Task #" + task.getId() +
                                " <a href=\"/web/guest/rike/-/show/task/" + task.getId() + "\">" +
                                StringEscapeUtils.escapeHtml(task.getTitle()) + "</a> ", task.getStatus(), user, null, task.toMap());


          Milestone milestone = task.getMilestone();
          if (MilestoneHelper.isMilestoneDone(milestone)) {
              ActivityLogHelper.log(" finished Milestone #" + milestone.getId() + " <a href=\"/web/guest/rike/-/show/milestone/" + milestone.getId() + "\">" + StringEscapeUtils.escapeHtml(milestone.getTitle()) + "</a>", "done", user, null, milestone.toMap());
          }
          
          return true;
      }
      
      return false;

  }
}
