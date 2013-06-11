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

package de.arago.rike.zombie;

import de.arago.rike.commons.data.Milestone;
import de.arago.rike.commons.util.ViewHelper;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 *
 *
 */
public class OverdueMilestone {

    private final Milestone milestone;
    private final int hoursOpen;
    private final int hoursInProgress;
    private final int hoursDone;

    public OverdueMilestone(int hoursOpen, int hoursInProgress, int hoursDone, Milestone milestone) {
        this.milestone = milestone;
        this.hoursOpen = hoursOpen;
        this.hoursInProgress = hoursInProgress;
        this.hoursDone = hoursDone;
    }

    public Milestone getMilestone() {
        return milestone;
    }

    /**
     * get the work already done, the sum of all tasks with status = 'done'
     *
     * @return work done in hours
     */
    public int getWorkDoneInHours() {
        return hoursDone;
    }

    /**
     * get the work in progress, the sum of all tasks with status = 'in_progress'
     *
     * @return work in progress in hours
     */
    public int getWorkInProgressInHours() {
        return hoursInProgress;
    }

    /**
     * get the work left, the sum of all tasks with status != 'done'
     *
     * @return work left in hours
     */
    public int getWorkLeftInHours() {
        return hoursOpen;
    }

    /**
     * get the time left for the milestone based on the due date
     *
     * @return days left
     */
    public int getDaysLeft() {
        return ViewHelper.getDayDifference(milestone.getDueDate());
    }

    /**
     * get the work that still needs to be done based on due date and performance
     *
     * @return work done in days
     */
    public int getWorkDoneInDays() {
        if (milestone.getPerformance() <= 0) {
            return Integer.MAX_VALUE;
        }
        // (work left todo / work doable per week) * 7 => done in days
        return (hoursOpen * 7 + milestone.getPerformance() - 1) / milestone.getPerformance();
    }

    /**
     * get the estimated completion date based on time left and work todo
     *
     * @return the estimated completion date
     */
    public Date getEstimatedDoneDate() {
        if (milestone.getPerformance() <= 0) {
            return new Date(Long.MAX_VALUE);
        }
        GregorianCalendar c = new GregorianCalendar();
        c.add(Calendar.HOUR_OF_DAY, (hoursOpen * 7 * 24) / milestone.getPerformance());
        return c.getTime();
    }

    public int getLate() {
        return getWorkDoneInDays() - getDaysLeft();
    }
}
