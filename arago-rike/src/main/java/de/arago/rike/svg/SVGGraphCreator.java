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
package de.arago.rike.svg;

import de.arago.rike.commons.data.Artifact;
import de.arago.rike.commons.data.DataHelperRike;
import de.arago.rike.commons.data.Dependency;
import de.arago.rike.commons.data.Milestone;
import de.arago.rike.commons.data.Task;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

public class SVGGraphCreator {

    private static final ConcurrentHashMap<String, SVGContainer> cache = new ConcurrentHashMap<String, SVGContainer>();
    private static final long ONE_MINUTE = 0L;

    private static final class SVGContainer {

        private final String data;
        private final Date created;

        public SVGContainer(String data) {
            this.data = data;
            created = new Date();
        }

        public String getData() {
            return data;
        }

        public boolean isExpired() {
            return created.getTime() < (System.currentTimeMillis() - ONE_MINUTE);
        }
    }

    public static void clearCache() {
        cache.clear();
    }

    /**
     * Create an svg graph for the node with its dependent node in parent and
     * child directions
     *
     * @param id
     * @return SVG content
     */
    public static String getGraph(SvgFilter filter) throws IOException, InterruptedException {

        SVGContainer container = cache.get(filter.getId());

        if (container == null || container.isExpired()) {
            StringBuilder content = createDotNotation(filter);

//			container = new SVGContainer(content.toString());
            container = new SVGContainer(transformDotToSVG(content));

            cache.put(filter.getId(), container);
        }

        return container.getData();
    }

    static StringBuilder createDotNotation(SvgFilter filter) {
        DataHelperRike<Task> helper = new DataHelperRike<Task>(Task.class);

        TreeSet<String> dates = new TreeSet<String>();
        for (Task task : helper.list(makeCriteria(filter, helper))) {
            if (task.getStart() != null) {
                dates.add(String.format("%1$tY-%1$tm-%1$td", task.getStart()));
            }
        }

        StringBuilder content = new StringBuilder();
        content.append("digraph G {\n    rankdir=LR;\n");

        TreeSet<Long> tasks = new TreeSet<Long>();
        String artifact_id = "";
        for (Task task : helper.list(makeCriteria(filter, helper))) {
            if (task.getArtifact() != null && !artifact_id.equals("" + task.getArtifact().getId())) {
                if (artifact_id.length() > 0) {
                    content.append("}\n\n");
                }
                artifact_id = "" + task.getArtifact().getId();
                content.append(String.format("subgraph cluster_%1$s{\n", artifact_id));
                content.append("  labeljust=r\n");
                content.append(String.format("  label=\"%1$s\"\n\n", task.getArtifact().getName()));

                for (String date : dates) {
                    content.append(String.format("  \"%1$s.%2$s\" [label=\"%2$s\",style=filled,fillcolor=beige,shape=invhouse]\n", artifact_id, date));
                }
                content.append(String.format("  \"%1$s.NEXT\" [label=\"Next\",style=filled,fillcolor=beige,shape=invhouse]\n\n", artifact_id));
                for (String date : dates) {
                    content.append(String.format("\"%1$s.%2$s\"->", artifact_id, date));
                }
                content.append(String.format("\"%1$s.NEXT\" [weight=8]\n\n", artifact_id));
            }

            String color = "dodgerblue";
            if ("open".equals(task.getStatus())) {
                color = "red";
            } else if ("done".equals(task.getStatus())) {
                color = "green";
            } else if ("in_progress".equals(task.getStatus())) {
                color = "yellow";
            }

            String name = formatName(task.getTitle());

            content.append(String.format("  %1$d [label=\"%2$s\",target=_parent,URL=\"%3$s\",style=filled,shape=note,fillcolor=%4$s]\n",
                                         task.getId(), name, "javascript:top.openRikeTask(" + task.getId() + ")", color));
            String start = "NEXT";
            if (task.getStart() != null) {
                start = String.format("%1$tY-%1$tm-%1$td", task.getStart());
            }
            content.append(String.format("  \"%1$s.%2$s\"->%3$d [color=invis]\n", artifact_id, start, task.getId()));

            tasks.add(task.getId());
        }

        if (artifact_id.length() > 0) {
            content.append("}\n\n");
        }

        DataHelperRike<Dependency> deps = new DataHelperRike<Dependency>(Dependency.class);
        for (Dependency d : deps.list()) {
            if (tasks.contains(d.getPremise()) && tasks.contains(d.getSequel())) {
                content.append(String.format("%1$d->%2$d\n", d.getPremise(), d.getSequel()));
            }
        }

        content.append("}\n");
        return content;
    }

    private static String formatName(String rawName) {
        String nameFixed = rawName.replace("\"", "\\\"").replaceAll("\\s", " ");
        String name = "";
        while(nameFixed.length()>30) {
            int pos = nameFixed.indexOf(" ",25);
            if (pos>0) {
                name += nameFixed.substring(0,pos)+"\\n";
                nameFixed = nameFixed.substring(pos+1);
            } else
                break;
        }
        name += nameFixed;
        return name;
    }

    private static Criteria makeCriteria(SvgFilter filter,
                                         DataHelperRike<Task> helper) {
        Criteria crit = helper.filter();

        if (filter.getUser() != null && filter.getUser().length() > 0) {
            crit.add(Restrictions.eq("owner", filter.getUser()));
        }

        if (filter.getArtifact() != null && filter.getArtifact() >= 0) {
            Artifact a = new DataHelperRike<Artifact>(Artifact.class).find(filter.getArtifact());
            crit.add(Restrictions.eq("artifact", a));
        }

        if (filter.getMilestone() != null && !filter.getMilestone().isEmpty()) {

            String tmp = filter.getMilestone();

            if (tmp.startsWith("milestone_")) {
                Milestone m = new DataHelperRike<Milestone>(Milestone.class).find(filter.getMilestone().substring(10));

                crit.add(Restrictions.eq("milestone", m));
            } else if (tmp.startsWith("release_")) {
                DataHelperRike<Milestone> milestoneHelper = new DataHelperRike<Milestone>(Milestone.class);

                List<Milestone> list = milestoneHelper.list(milestoneHelper.filter().add(Restrictions.eq("release", filter.getMilestone().substring(8))));

                crit.add(Restrictions.in("milestone", list));
            }

        }

        crit.addOrder(Order.asc("artifact"));
        return crit;
    }

    private static String transformDotToSVG(StringBuilder content) throws IOException, InterruptedException {
        File file = File.createTempFile("pattern", ".dot");

        new FileWriter(file).append(content.toString()).close();

        Process p = Runtime.getRuntime().exec(new String[] {
                                                  "dot",
                                                  "-Tsvg",
                                                  "-Gsplines=true",
                                                  "-Goverlap=false",
                                                  "-Grankdir=LR",
                                                  "-o" + file.getAbsolutePath() + ".svg",
                                                  file.getAbsolutePath()
                                              });

        p.waitFor();

        file.delete();

        StringBuilder b = new StringBuilder();
        BufferedReader reader = new BufferedReader(new FileReader(file.getAbsolutePath() + ".svg"));

        String part;
        while ((part = reader.readLine()) != null) {
            b.append(part);
        }

        reader.close();

        new File(file.getAbsolutePath() + ".svg").delete();

        return b.toString();
    }
}
