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
package de.arago.rike.commons.data;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static de.arago.rike.commons.data.GlobalConfig.PRIORITY_MAXIMAL_NUMBER;


public class Task implements Serializable {

    private long id;
    private String title;
    private String url;
    private String owner;
    private Date start;
    private Date end;
    private String creator;
    private Date created;
    private Integer sizeEstimated;
    private Integer hoursSpent;
    private Milestone milestone;
    private Artifact artifact;
    private Integer priority;
    private String status;
    private Date rated;
    private String ratedBy;
    private Date dueDate;
    private String description;

    public Task() {
    }

    public static enum Status {

        UNKNOWN, OPEN, IN_PROGRESS, DONE
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getOwner() {
        return owner;
    }

    public String getStatus() {
        return status;
    }

    public Status getStatusEnum() {
        return Status.valueOf(status.toUpperCase());
    }

    public void setStatus(String what) {
        setStatus(Status.valueOf(what.toUpperCase()));
    }

    public void setStatus(Status what) {
        status = what.toString().toLowerCase();
    }

    public void setStart(Date start) {
      this.start = start;
    }

    public Date getStart() {
        return start;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public Date getEnd() {
        return end;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getCreator() {
        return creator;
    }

    public String getRatedBy() {
        return ratedBy;
    }

    public void setRatedBy(String user) {
        this.ratedBy = user;
    }

    public void setCreated(Date created) {
      this.created = created; 
    }

    public void setRated(Date rated) {
      this.rated = rated;
    }

    public Date getCreated() {
        return created;
    }

    public Date getRated() {
        return rated;
    }

    public Integer getSizeEstimated() {
        return sizeEstimated;
    }

    public void setSizeEstimated(Integer sizeEstimated) {
        this.sizeEstimated = sizeEstimated;
    }

    public Integer getHoursSpent() {
        return hoursSpent == null?0:hoursSpent;
    }

    public void setHoursSpent(Integer spent) {
        hoursSpent = spent;
    }

    public Milestone getMilestone() {
        return milestone;
    }

    public void setMilestone(Milestone milestone) {
        this.milestone = milestone;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        if(priority<=0)
            priority = 1;
        if(priority>Integer.parseInt(GlobalConfig.get(PRIORITY_MAXIMAL_NUMBER)))
            priority = Integer.parseInt(GlobalConfig.get(PRIORITY_MAXIMAL_NUMBER));
        this.priority = priority;
    }


    public void setArtifact(Artifact what) {
        artifact = what;
    }

    public Artifact getArtifact() {
        return artifact;
    }

    public void setDueDate(Date when) {
      this.dueDate = when;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public String getDescription() {
        return description == null?"":description;
    }

    public void setDescription(String description) {
        this.description = description == null?"":description;
    }

    public Map toMap() {
        Map map = new HashMap();

        map.put("type", "Task");
        map.put("id", getId().toString());
        map.put("title", title);
        map.put("url", url);
        map.put("owner", owner);
        if (start != null) {
            map.put("start", start.getTime() + "");
        }
        if (end != null) {
            map.put("end", end.getTime() + "");
        }
        map.put("creator", creator);
        if (created != null) {
            map.put("created", created.getTime() + "");
        }

        map.put("sizeEstimated", sizeEstimated);
        map.put("hoursSpent", hoursSpent);

        if (milestone != null) {
            map.put("milestoneId", milestone.getId());
        }
        if (artifact != null) {
            map.put("artifact", artifact.getId().toString());
        }

        map.put("priority", priority);
        map.put("status", status);

        if (rated != null) {
            map.put("rated", rated.getTime() + "");
        }

        map.put("ratedBy", ratedBy);
        if (dueDate != null) {
            map.put("dueDate", dueDate.getTime() + "");
        }
        map.put("description", description);

        return map;
    }
}
