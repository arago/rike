/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 *//*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.arago.rike.rest.resources;

import de.arago.rike.commons.util.MilestoneHelper;
import java.util.HashMap;
import java.util.Map;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

/**
 *
 */
public class MilestonesResource extends ServerResource 
{ 
  @Get("json")
  public Map json() 
  {
    final Map ret = new HashMap();
    
    if ("true".equals(getQuery().getFirstValue("valid")))
    {
      ret.put("items", MilestoneHelper.listNotExpired());
    } else {
      ret.put("items", MilestoneHelper.list());  
    } 
    
    return ret;
  }
}
