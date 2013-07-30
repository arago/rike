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
package de.arago.rike.activitylog.json;

import de.arago.portlet.JsonAction;
import de.arago.data.IDataWrapper;
import de.arago.rike.commons.data.DataHelperRike;
import de.arago.rike.commons.data.ActivityLog;

import java.util.List;
import java.util.Map;
import net.minidev.json.JSONObject;
import org.hibernate.criterion.Restrictions;

public class PollUpdates implements JsonAction {
    @Override
    public Map execute(IDataWrapper data) {
        JSONObject result = new JSONObject();

        result.put("count", 0);

        String lastId = data.getRequestAttribute("id");

        if (lastId != null && !lastId.isEmpty()) {
            final DataHelperRike<ActivityLog> helper = new DataHelperRike<ActivityLog>(ActivityLog.class);
            List<ActivityLog> list = helper.list(helper.filter().add(Restrictions.gt("id", Long.valueOf(lastId, 10))));

            result.put("count", list.size());
        }

        return result;
    }
}
