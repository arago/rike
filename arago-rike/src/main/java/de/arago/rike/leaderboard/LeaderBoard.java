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
package de.arago.rike.leaderboard;

import de.arago.portlet.AragoPortlet;
import de.arago.portlet.util.SecurityHelper;
import de.arago.data.IDataWrapper;

import de.arago.rike.data.DataHelperRike;
import de.arago.rike.data.TaskUser;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import javax.portlet.PortletException;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

public class LeaderBoard extends AragoPortlet {
    @Override
    public void initSession(IDataWrapper data) throws PortletException, IOException {
        if (!SecurityHelper.isLoggedIn(data.getUser())) {
            return;
        }

        try {
            data.setSessionAttribute("list", getData());
            data.setSessionAttribute("listDate", new Date());
        } catch(Throwable t) {
            t.printStackTrace(System.err);
        }
    }

    @Override
    protected boolean checkViewData(IDataWrapper data) {
        if (!SecurityHelper.isLoggedIn(data.getUser())) return false;

        Date then = (Date) data.getSessionAttribute("listDate");

        if (then == null || then.getTime() < System.currentTimeMillis() - 5 * 60 * 1000) {
            data.setSessionAttribute("list", getData());
            data.setSessionAttribute("listDate", new Date());
        }

        return true;
    }

    private static List<TaskUser> getData() {
        DataHelperRike<TaskUser> helper = new DataHelperRike<TaskUser>(TaskUser.class);

        return helper.list(helper.filter().add(Restrictions.eq("isDeleted", 0)).addOrder(Order.desc("account")).addOrder(Order.asc("email")));
    }

}
