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
/**
 *
 */
package de.arago.rike.task.action;

import de.arago.portlet.Action;
import de.arago.portlet.util.SecurityHelper;

import de.arago.data.IDataWrapper;
import de.arago.rike.commons.data.DataHelperRike;
import de.arago.rike.commons.data.Milestone;
import de.arago.rike.commons.util.ActivityLogHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.commons.lang.StringEscapeUtils;

public class SaveMilestone implements Action {

    @Override
    public void execute(IDataWrapper data) {
        
        DataHelperRike<Milestone> helper = new DataHelperRike<Milestone>(Milestone.class);
        Milestone milestone = null;
        boolean newMilestoneCreated = false;

        if (data.getRequestAttribute("id") != null && !data.getRequestAttribute("id").isEmpty()) {
            milestone = helper.find(data.getRequestAttribute("id"));
        }

        if (milestone == null) {
            newMilestoneCreated = true;
            milestone = new Milestone();
        }

        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            milestone.setDueDate(format.parse(data.getRequestAttribute("due_date")));
        } catch(Exception ignored) {
        }

        milestone.setTitle(data.getRequestAttribute("title"));
        milestone.setUrl(data.getRequestAttribute("url"));
        milestone.setCreated(new Date());
        milestone.setCreator(SecurityHelper.getUser(data.getUser()).getEmailAddress());

        milestone.setPerformance(0);
        
        try {
            milestone.setPerformance(Integer.valueOf(data.getRequestAttribute("performance"), 10));
        } catch(Exception ignored) {}

        milestone.setRelease("");

        String release = data.getRequestAttribute("release");

        if (release != null && release.equals("_new_")) {
            milestone.setRelease(data.getRequestAttribute("new_release"));
        } else if (release != null) {
            milestone.setRelease(release);
        }

        helper.save(milestone);

        data.setSessionAttribute("milestone", milestone);
        data.setSessionAttribute("targetView", "viewMilestone");

        String message = " changed Milestone #";
        if (newMilestoneCreated) {
            message = " created Milestone #";
        }


        ActivityLogHelper.log(message + milestone.getId() + " <a href=\"/web/guest/rike/-/show/milestone/" + milestone.getId() + "\">" + StringEscapeUtils.escapeHtml(milestone.getTitle()) + "</a>", "", milestone.getCreator(), data, milestone.toMap());

    }
}
