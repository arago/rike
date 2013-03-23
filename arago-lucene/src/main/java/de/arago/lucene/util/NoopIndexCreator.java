package de.arago.lucene.util;

import de.arago.lucene.api.Index;

public final class NoopIndexCreator extends IndexCreator {
    @Override
    public void fill(Index index) {
        // do nothing
        synchronized(index) {
            index.optimize();
            index.close();
        }
    }
}
