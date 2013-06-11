package de.arago.rike.commons.util;

import de.arago.lucene.api.Index;
import de.arago.lucene.util.IndexCreator;
import de.arago.rike.commons.data.DataHelperRike;
import de.arago.rike.commons.data.Task;


public class TaskIndexCreator extends IndexCreator<Task> {

    @Override
    public void fill(Index<Task> index) {
        synchronized (index) {
            index.delete();

            DataHelperRike<Task> helper = new DataHelperRike<Task>(Task.class);

            for (Task task: helper.list())
                index.update(task);

            index.close();
        }
    }
}
