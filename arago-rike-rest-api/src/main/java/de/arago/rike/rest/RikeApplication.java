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
package de.arago.rike.rest;

import de.arago.rike.commons.data.GlobalConfig;
import de.arago.rike.rest.resources.ArtifactsResource;
import de.arago.rike.rest.resources.MilestonesResource;
import de.arago.rike.rest.resources.NewTaskResource;
import de.arago.rike.rest.resources.TaskResource;
import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.routing.Router;

import static de.arago.rike.commons.data.GlobalConfig.WORKFLOW_TIME_OFFSET;


/**
 *

 */
public class RikeApplication extends Application 
{
  @Override
  public Restlet createInboundRoot() {
    GlobalConfig.fetchFromDatabase();
    GlobalConfig.set(WORKFLOW_TIME_OFFSET, "0");
    
    Router mainRouter = new Router(getContext());
    mainRouter.attach("/milestones", MilestonesResource.class);
    mainRouter.attach("/artifacts", ArtifactsResource.class);
    
    mainRouter.attach("/task/", NewTaskResource.class);
    mainRouter.attach("/task/{id}", TaskResource.class);

    return mainRouter;
  }
}
