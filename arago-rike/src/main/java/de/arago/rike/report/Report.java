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
package de.arago.rike.report;

import de.arago.portlet.AragoPortlet;
import de.arago.portlet.util.SecurityHelper;

import de.arago.data.IDataWrapper;
import de.arago.rike.commons.util.TaskHelper;
import de.arago.rike.commons.data.TaskUser;
import de.arago.rike.commons.util.PortletTitleWithMilestone;
import de.arago.rike.commons.util.ViewHelper;

import java.io.IOException;
import javax.portlet.PortletException;

public class Report extends AragoPortlet {

    @Override
    public void initSession(IDataWrapper data) throws PortletException, IOException {
        if (!SecurityHelper.isLoggedIn(data.getUser())) {
            return;
        }
        String user = SecurityHelper.getUserEmail(data.getUser());
        if(user!=null&&!user.isEmpty()) {
            TaskUser tu = TaskHelper.checkIfUserExists(user);
            if(tu!=null) {
                String lastSelectedMilestone = tu.getLastms();
                if(lastSelectedMilestone!=null&&!lastSelectedMilestone.isEmpty())
                    data.setSessionAttribute("milestone",lastSelectedMilestone);
            }
        }

        data.setSessionAttribute("type", getPortletConfig().getInitParameter("type"));
        data.setSessionAttribute("typeName", getPortletConfig().getInitParameter("typeName"));
    }

    @Override
    protected boolean checkViewData(IDataWrapper data) {
        if(!SecurityHelper.isLoggedIn(data.getUser())) {
            return false;
        }
        if(data.getSessionAttribute("portletTitle")==null) {
            data.setSessionAttribute("portletTitle",
                                     new PortletTitleWithMilestone(
                                         (String)data.getSessionAttribute("milestone"),
                                         getPortletConfig().getInitParameter("typeName")));
        }
        return true;
    }
}
