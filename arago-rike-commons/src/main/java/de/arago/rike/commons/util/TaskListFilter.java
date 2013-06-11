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

import java.util.LinkedList;
import org.apache.commons.lang.StringUtils;
import de.arago.portlet.MapOptionFilter;

public class TaskListFilter extends MapOptionFilter {

    public static enum SortDirection {

        ASC,
        DESC
    };

    public static enum SortField {

        TITLE, STATUS, ID, PRIORITY
    }

    @Override
    public void setDefaultOptions() {
        setSortField(SortField.STATUS);
        setSortDirection(SortDirection.ASC);
        setUser("");
        setStatus("");
        setPriority("");
        setMilestone("");
        setCreator("");
        setIsActive(false);
    }

    public void setSortField(SortField type) {
        set("sort", type);
    }

    public void setSortField(String type) {
        setSortField(SortField.valueOf(type));
    }

    public void setSortDirection(SortDirection sortDirection) {
        set("direction", sortDirection);
    }

    public void setSortDirection(String sortDirection) {
        setSortDirection(SortDirection.valueOf(sortDirection));
    }

    public SortField getSortField() {
        return (SortField) get("sort");
    }

    public SortDirection getSortDirection() {
        return (SortDirection) get("direction");
    }

    public String getUser() {
        return (String) getOr("user", "");
    }

    public void setUser(String what) {
        set("user", what);
    }

    public String getMilestone() {
        return (String) getOr("milestone", "");
    }

    public void setMilestone(String what) {
        set("milestone", what);
    }

    public String getArtifact() {
        return (String) getOr("artifact", "");
    }

    public void setArtifact(String what) {
        set("artifact", what);
    }

    public String getStatus() {
        return (String) getOr("status", "");
    }

    public void setStatus(String what) {
        set("status", what.toLowerCase());
    }

    public String getPriority() {
        return (String) getOr("priority", "");
    }

    public void setPriority(String what) {
        set("priority", what);
    }

    public void setIsActive(boolean b) {
        set("isActive", b);
    }

    public boolean isActive() {
        return (Boolean) getOr("isActive", false);
    }

    public void setCreator(String who) {
        set("creator", who);
    }

    public String getCreator() {
        return (String) getOr("creator", "");
    }

    public String getInfo() {
        LinkedList<String> parts = new LinkedList<String>();
        String info = "";
        if(!isActive())
            info = "inactive";
        else {
            if(getMilestone().length()>0) {
                String mid = getMilestone();
                for(String[] iter:ViewHelper.getAvailableMilestones())
                    if(mid.equals(iter[0])) {
                        parts.add(iter[1]);
                        break;
                    }
            }
            if(getStatus().length()>0)
                parts.add(ViewHelper.getStatus(getStatus()));
            info = StringUtils.join(parts,", ");
        }
        if(info.length()==0)
            info = "active";
        return info;
    }
}
