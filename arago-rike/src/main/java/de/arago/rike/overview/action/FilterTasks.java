/**
 * Copyright (c) 2010 arago AG, http://www.arago.de/
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
/**
 *
 */
package de.arago.rike.overview.action;

import de.arago.data.IDataWrapper;
import de.arago.portlet.Action;
import de.arago.portlet.util.SecurityHelper;
import de.arago.rike.commons.data.DataHelperRike;
import de.arago.rike.commons.data.TaskUser;
import de.arago.rike.commons.util.TaskHelper;
import de.arago.rike.commons.util.TaskListFilter;
import java.util.HashMap;
import java.util.List;
import org.hibernate.criterion.Restrictions;

public class FilterTasks implements Action {

    @Override
    public void execute(IDataWrapper data) {

        TaskListFilter filter = (TaskListFilter) data.getSessionAttribute("taskListFilter");

        boolean have = false;

        String milestone = data.getRequestAttribute("milestone");
        String user = data.getRequestAttribute("user");
        String status = data.getRequestAttribute("status");
        String priority = data.getRequestAttribute("priority");
        String artifact = data.getRequestAttribute("artifact");
        String creator = data.getRequestAttribute("creator");

        if (milestone.length() > 0) {
            have = true;
        }
        if (user.length() > 0) {
            have = true;
        }
        if (status.length() > 0) {
            have = true;
        }
        if (priority.length() > 0) {
            have = true;
        }
        if (artifact.length() > 0) {
            have = true;
        }
        if (creator.length() > 0) {
            have = true;
        }

        filter.setMilestone(milestone);
        filter.setStatus(status);
        filter.setUser(user);
        filter.setArtifact(artifact);
        filter.setPriority(priority);
        filter.setIsActive(have);
        filter.setCreator(creator);

        data.setSessionAttribute("taskList", TaskHelper.getAllTasks(filter));

        HashMap<String, Object> eventData = new HashMap<String, Object>();
        eventData.put("milestone", milestone);

        data.setEvent("MilestoneChangeNotification", eventData);

        DataHelperRike<TaskUser> userHelper = new DataHelperRike<TaskUser>(TaskUser.class);
        String email = SecurityHelper.getUserEmail(data.getUser());
        List<TaskUser> userData = userHelper.list(userHelper.filter().add(Restrictions.eq("email", email)));
        if (userData.size() > 0) {
            userData.get(0).setLastms(milestone);
            userHelper.save(userData.get(0));
        }
    }
}
