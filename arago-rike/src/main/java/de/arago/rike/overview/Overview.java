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
package de.arago.rike.overview;

import de.arago.portlet.AragoPortlet;

import de.arago.data.IDataWrapper;
import de.arago.rike.commons.util.TaskHelper;
import de.arago.rike.commons.util.TaskListFilter;
import de.arago.rike.commons.data.TaskUser;

import java.io.IOException;

import javax.portlet.PortletException;

import de.arago.portlet.util.SecurityHelper;
import de.arago.rike.commons.data.GlobalConfig;
import de.arago.rike.commons.util.StatisticHelper;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Overview extends AragoPortlet {

    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @Override
    public void init() throws PortletException {
        super.init();

        scheduler.scheduleAtFixedRate(new StatisticHelper(), 1, 1, TimeUnit.HOURS);
        GlobalConfig.fetchFromDatabase();
        StatisticHelper.update();
    }

    @Override
    public void destroy() {
        scheduler.shutdownNow();
    }

    @Override
    public void initSession(IDataWrapper data) throws PortletException, IOException {
        if (!SecurityHelper.isLoggedIn(data.getUser())) {
            return;
        }

        try {
            Object taskListFilterObject = data.getSessionAttribute("taskListFilter");

            if (taskListFilterObject == null) {
                TaskListFilter taskListFilter = new TaskListFilter();
                taskListFilter.setDefaultOptions();
                taskListFilter.setStatus("open");
                taskListFilter.setIsActive(true);
                taskListFilter.setSortField(TaskListFilter.SortField.PRIORITY);
                taskListFilter.setSortDirection(TaskListFilter.SortDirection.ASC);
                String user = SecurityHelper.getUserEmail(data.getUser());
                if (user != null&&!user.isEmpty()) {
                    TaskUser tu = TaskHelper.checkIfUserExists(user);
                    if (tu != null) {
                        String lastSelectedMilestone = tu.getLastms();
                        if (lastSelectedMilestone != null && !lastSelectedMilestone.isEmpty()) {
                            taskListFilter.setMilestone(lastSelectedMilestone);
                        }
                    }
                }
                taskListFilterObject = taskListFilter;
            }

            data.setSessionAttribute("taskListFilter", taskListFilterObject);
            data.setSessionAttribute("taskList", TaskHelper.getAllTasks((TaskListFilter) taskListFilterObject));
        } catch(Throwable t) {
            t.printStackTrace(System.err);
        }
    }

    @Override
    protected boolean checkViewData(IDataWrapper data) {

        if (!SecurityHelper.isLoggedIn(data.getUser()))
            return false;

        Object taskListFilterObject = data.getSessionAttribute("taskListFilter");

        if (taskListFilterObject != null) {
            data.setSessionAttribute("taskList", TaskHelper.getAllTasks((TaskListFilter) taskListFilterObject));
        }

        return true;
    }
}
