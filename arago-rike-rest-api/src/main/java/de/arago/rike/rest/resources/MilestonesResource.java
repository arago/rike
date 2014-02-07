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
