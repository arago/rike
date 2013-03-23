package de.arago.lucene.util;

import de.arago.lucene.api.Index;

abstract public class IndexUpdater<T> {
    /**
     * update one item in the index
     * @param index
     * @param item
     */
    abstract public void update(Index<T> index, Object item);
}
