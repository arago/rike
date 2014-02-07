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
package de.arago.rike.commons.data;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Milestone {

    private long id;
    private String title;
    private Date dueDate;
    private String release;
    private String url;
    private String creator;
    private Date created;
    private Integer performance;
    
    public Milestone() {
        
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

    public void setDueDate(Date what) {
      dueDate = what;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public String getRelease() {
        return release;
    }

    public void setRelease(String release) {
        this.release = release;
    }

    public boolean hasRelease() {
        return !getRelease().isEmpty();
    }

    public void setUrl(String what) {
        url = what;
    }

    public String getUrl() {
        return url;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreated(Date created) {
      this.created = created;
    }

    public Date getCreated() {
        return created;
    }

    public void setPerformance(Integer what) {
        this.performance = what;
    }

    public Integer getPerformance() {
        if(performance<=0)
            return 42;
        return performance;
    }

    public long getDays() {
        long d = 0;
        if(getDueDate()!=null)
            d = getDueDate().getTime();
        return (d-System.currentTimeMillis())/(1000*60*60);
    }

    public Map toMap() {
        Map map = new HashMap();

        map.put("type", "Milestone");
        map.put("id", getId().toString());
        map.put("title", title);

        if (dueDate != null) {
            map.put("dueDate", dueDate.getTime() + "");
        }

        map.put("release", release);
        map.put("url", url);
        map.put("creator", creator);

        if (created != null) {
            map.put("created", created.getTime() + "");
        }

        map.put("performance", performance);

        return map;
    }

}
