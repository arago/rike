/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 *//*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 *//*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 *//*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
