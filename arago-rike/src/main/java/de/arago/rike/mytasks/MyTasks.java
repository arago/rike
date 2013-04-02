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
package de.arago.rike.mytasks;

import com.liferay.portal.model.User;
import de.arago.portlet.AragoPortlet;
import de.arago.portlet.util.SecurityHelper;
import de.arago.data.IDataWrapper;
import de.arago.rike.mytasks.action.ShowInProgress;

import java.io.IOException;
import javax.portlet.PortletException;

public class MyTasks extends AragoPortlet {

    @Override
    public void initSession(IDataWrapper data) throws PortletException, IOException {
        if (!SecurityHelper.isLoggedIn(data.getUser())) {
            return;
        }

        try {
            User user = SecurityHelper.getUser(data.getUser());
            data.setSessionAttribute("currentUser", user.getEmailAddress());
            new ShowInProgress().execute(data);
        } catch(Throwable t) {
            t.printStackTrace(System.err);
        }
    }

    @Override
    protected boolean checkViewData(IDataWrapper data) {
        return SecurityHelper.isLoggedIn(data.getUser());
    }

}
