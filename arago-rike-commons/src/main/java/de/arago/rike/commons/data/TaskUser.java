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


public class TaskUser {

    private long id;
    private String email;
    private String last_ms;
    private long account;
    private long yesterday;
    private int isDeleted;
    private String flags;
    private int[] ended_tasks;

    public String getLast_ms() {
        return last_ms;
    }

    public void setLast_ms(String lastMs) {
        last_ms = lastMs;
    }

    public void setId(Long what) {
        id = what;
    }

    public Long getId() {
        return id;
    }

    public void setEmail(String what) {
        email = what;
    }

    public String getEmail() {
        return email;
    }

    public Long getAccount() {
        return account;
    }

    public void setAccount(Long account) {
        this.account = account;
    }

    public Long getYesterday() {
        return yesterday;
    }

    public void setYesterday(Long yesterday) {
        this.yesterday = yesterday;
    }

    public int getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(int isDeleted) {
        this.isDeleted = isDeleted;
    }

    public boolean isDeleted() {
        return isDeleted == 1;
    }

    public int[] getEnded_tasks() {
        return ended_tasks;
    }

    public void setEnded_tasks(int[] ended_tasks) {
        this.ended_tasks = ended_tasks;
    }

    public String getFlags() {
        return flags;
    }

    public void setFlags(String flags) {
        this.flags = flags;
    }

    public String getAlias() {
        return email.replaceAll("\\@.*$", "");
    }

}
