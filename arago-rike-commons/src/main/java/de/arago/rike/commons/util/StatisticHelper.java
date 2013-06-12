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

import org.hibernate.SQLQuery;

public final class StatisticHelper implements Runnable {
    private static final String[] queries = new String[] {
        "UPDATE tasks SET size_adjusted=size_estimated ",
        "DELETE FROM task_stat WHERE DATEDIFF(CURDATE( ),moment)>360 OR CURDATE( )=moment;",
        "INSERT INTO task_stat (summe_size,count_id,milestone_id,artifact_id,task_status,moment) SELECT sum(size_adjusted),count(id),milestone_id,artifact_id,task_status,CURDATE() FROM tasks WHERE milestone_id IS NOT NULL GROUP BY milestone_id,artifact_id,task_status;",

        "DELETE FROM releases_archive;",
        "INSERT INTO releases_archive (name,estimated_size,hours_spent,task_count,finished) SELECT milestones.release_name, sum(size_adjusted), sum(hours_spent),count(tasks.id), max(end) FROM tasks,milestones WHERE milestone_id=milestones.id and task_status='done' GROUP BY milestones.release_name;"
    };

    public StatisticHelper() {}

    public static void update() {
        new Thread(new StatisticHelper()).start();
    }

    @Override
    public synchronized void run() {
        try {
            final DataHelperRike<Object> helper = new DataHelperRike<Object>(Object.class);

            for (final String query: queries) {
                SQLQuery q = null;

                try {
                    q = helper.createSQLQuery(query);
                    helper.execute(q);
                } finally {
                    helper.finish(q);
                }
            }
        } catch(Throwable t) {
            t.printStackTrace(System.err);
        }
    }
}
