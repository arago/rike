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

package de.arago.rike.help.action;

import de.arago.portlet.Action;

import de.arago.data.IDataWrapper;
import de.arago.portlet.util.SecurityHelper;
import de.arago.rike.commons.data.DataHelperRike;
import de.arago.rike.commons.data.TaskUser;
import java.util.List;
import org.hibernate.criterion.Restrictions;

public class CloseOverview implements Action {

    @Override
    public void execute(IDataWrapper data) {
        data.setSessionAttribute("help.shown", "true");
        if ("true".equals(data.getRequestAttribute("hide"))) {
            DataHelperRike<TaskUser> userHelper = new DataHelperRike<TaskUser>(TaskUser.class);
            String email = SecurityHelper.getUserEmail(data.getUser());
            List<TaskUser> userData = userHelper.list(userHelper.filter().add(Restrictions.eq("email", email)));
            if (userData.size() > 0) {
                TaskUser tu = userData.get(0);
                tu.addFlag("DisableOverlay", "true");
                userHelper.save(tu);
            }
        }
    }
}
