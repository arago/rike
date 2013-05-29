/**
 * Copyright (c) 2010 arago AG, http://www.arago.de/
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package de.arago.rike.task;

import com.liferay.portal.util.PortalUtil;
import de.arago.portlet.AragoPortlet;
import de.arago.portlet.util.SecurityHelper;

import de.arago.data.IDataWrapper;
import de.arago.portlet.PortletDataWrapper;
import de.arago.rike.data.Milestone;
import de.arago.rike.util.MilestoneHelper;
import de.arago.rike.util.TaskHelper;

import java.io.IOException;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

public class Task extends AragoPortlet {

    @Override
    public void init(PortletConfig config) throws PortletException {
        super.init(config);
    }

    @Override
    public void initSession(IDataWrapper data) throws PortletException, IOException {
        if (!checkViewData(data)) {
            return;
        }
    }


    @Override
    public void doView(RenderRequest request, RenderResponse response)
    throws PortletException, IOException {
        PortletDataWrapper data = new PortletDataWrapper(request, response);
        checkForArtifact(request, data);
        checkForMilestone(request, data);
        checkForTask(request, data);

        super.doView(request, response);
    }

    @Override
    protected boolean checkViewData(IDataWrapper data) {
        data.setSessionAttribute("userEmail", SecurityHelper.getUserEmail(data.getUser()));
        return SecurityHelper.isLoggedIn(data.getUser());
    }

    private void checkForMilestone(RenderRequest request, IDataWrapper data) {
        try {
            String id = PortalUtil.getOriginalServletRequest(PortalUtil.getHttpServletRequest(request)).getParameter("perm_milestone");

            if (id != null && !id.isEmpty()) {
                Milestone milestone = MilestoneHelper.getMilestone(id);

                if (milestone != null) {
                    data.setSessionAttribute("milestone", milestone);

                    data.setSessionAttribute("targetView", "viewMilestone");
                }
            }
        } catch(Throwable ignored) {
            // blank
        }
    }

    private void checkForTask(RenderRequest request, IDataWrapper data) {
        try {
            String id = PortalUtil.getOriginalServletRequest(PortalUtil.getHttpServletRequest(request)).getParameter("perm_task");

            if (id != null && !id.isEmpty()) {
                de.arago.rike.data.Task task = TaskHelper.getTask(id);

                if (task != null) {
                    data.setSessionAttribute("task", task);

                    data.setSessionAttribute("targetView", "defaultView");
                }
            }
        } catch(Throwable ignored) {
            // blank
        }
    }

    private void checkForArtifact(RenderRequest request, IDataWrapper data) {
        try {
            String id = PortalUtil.getOriginalServletRequest(PortalUtil.getHttpServletRequest(request)).getParameter("perm_artifact");

            if (id != null && !id.isEmpty()) {
                Milestone milestone = MilestoneHelper.getMilestone(id);

                if (milestone != null) {
                    data.setSessionAttribute("artifact", milestone);

                    data.setSessionAttribute("targetView", "viewArtifact");
                }
            }
        } catch(Throwable ignored) {
            // blank
        }
    }
}
