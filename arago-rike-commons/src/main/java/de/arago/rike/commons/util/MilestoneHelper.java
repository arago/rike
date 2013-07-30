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
package de.arago.rike.commons.util;

import de.arago.rike.commons.data.DataHelperRike;
import de.arago.rike.commons.data.Milestone;
import java.util.Date;
import java.util.List;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

/**
 *
 */
public final class MilestoneHelper {

    private MilestoneHelper() {
        //not called
    }

    public static List<Milestone> list() {
        DataHelperRike<Milestone> helper = new DataHelperRike<Milestone>(Milestone.class);

        return helper.list(helper.filter().addOrder(Order.desc("dueDate")).addOrder(Order.asc("title")));
    }

    public static List<Milestone> listNotExpired() {
        DataHelperRike<Milestone> helper = new DataHelperRike<Milestone>(Milestone.class);

        return helper.list(helper.filter().add(Restrictions.or(Restrictions.gt("dueDate", new Date()), Restrictions.isNull("dueDate"))).addOrder(Order.asc("dueDate")).addOrder(Order.asc("title")));
    }

    public static Milestone getMilestone(String id) {
        if (id == null || id.isEmpty()) return null;

        return new DataHelperRike<Milestone>(Milestone.class).find(id);
    }

    public static boolean isMilestoneDone(Milestone milestone) {
        DataHelperRike<Milestone> helper = new DataHelperRike<Milestone>(Milestone.class);

        String query = "select count(*) as items_left from tasks where milestone_id = "+milestone.getId()+" and task_status != 'done'";

        for (final Object o: helper.list(helper.createSQLQuery(query))) {
            if (o != null && ViewHelper.asInt(o) == 0) return true;
        }

        return false;
    }
}
