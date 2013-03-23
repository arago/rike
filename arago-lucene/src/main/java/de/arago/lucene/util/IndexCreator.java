package de.arago.lucene.util;

import de.arago.lucene.api.Index;

abstract public class IndexCreator<T> {

    abstract public void fill(Index<T> index);
}
