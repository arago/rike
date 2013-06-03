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
package de.arago.rike.svg;

import de.arago.portlet.AragoPortlet;
import de.arago.portlet.util.SecurityHelper;
import de.arago.data.IDataWrapper;
import de.arago.rike.util.TaskListFilter;
import de.arago.rike.data.DataHelperRike;
import de.arago.rike.data.Milestone;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import javax.portlet.PortletException;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

public class SVG extends AragoPortlet {

    @Override
    public void initSession(IDataWrapper data) throws PortletException, IOException {
        if (!SecurityHelper.isLoggedIn(data.getUser())) {
            return;
        }
    }

    @Override
    protected boolean checkViewData(IDataWrapper data) {

        if (data.getSessionAttribute("taskListFilter") == null) {
            data.setSessionAttribute("taskListFilter", new TaskListFilter() {
                @Override
                public void setDefaultOptions() {
                    super.setDefaultOptions();

                    DataHelperRike<Milestone> helper = new DataHelperRike<Milestone>(Milestone.class);
                    List<Milestone> list = 
                            helper.list(helper.filter().addOrder(Order.asc("dueDate")).add(
                                    Restrictions.and(
                                            Restrictions.isNotNull("dueDate"), 
                                            Restrictions.ge("dueDate", new Date())
                                    )
                            ).setMaxResults(1));

                    if (list.size() > 0) {
                        setIsActive(true);
                        setMilestone("milestone_" + list.get(0).getId().toString());
                    }
                }
            });
        }

        return SecurityHelper.isLoggedIn(data.getUser());
    }
}
