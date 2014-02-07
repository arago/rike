/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.arago.rike.rest.resources;

import de.arago.rike.commons.data.Task;
import de.arago.rike.commons.util.TaskHelper;
import org.restlet.data.Status;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

/**
 *
 
 */
public class TaskResource extends ServerResource 
{
  @Get("json")
  public Task json() 
  {
    try
    {  
      return TaskHelper.getTask((String) getRequest().getAttributes().get("id"));
    } catch(Exception ex) {
      setStatus(Status.CLIENT_ERROR_NOT_FOUND);
      return null;
    }
  }
}
