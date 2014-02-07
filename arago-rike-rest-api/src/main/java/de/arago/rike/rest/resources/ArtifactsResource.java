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

import de.arago.rike.commons.util.ArtifactHelper;
import java.util.HashMap;
import java.util.Map;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

/**
 *
 */
public class ArtifactsResource extends ServerResource 
{ 
  @Get("json")
  public Map json() 
  {
    final Map ret = new HashMap();
    
    ret.put("items", ArtifactHelper.list());
    
    return ret;
  }
}
