package de.arago.lucene.util;

import de.arago.lucene.api.Converter;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import net.minidev.json.JSONArray;
import org.apache.commons.lang.NotImplementedException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;

public abstract class BaseConverter<T> implements Converter<T> {
    protected IndexSearcher searcher;
    protected ScoreDoc[] hits;

    @Override
    public float getRating(int position) {
        return hits[position].score;
    }

    @Override
    public int size() {
        return hits.length;
    }

    @Override
    public void setResult(ScoreDoc[] hits, IndexSearcher searcher) {
        this.searcher = searcher;
        this.hits = hits;
    }

    @Override
    public String toJSONString() {
        List<T> all = resultToList();
        return JSONArray.toJSONString(all);
    }

    @Override
    public Iterator<T> iterator() {
        return Collections.unmodifiableList(resultToList()).iterator();
    }

    @Override
    public List<T> resultToList() {
        throw new NotImplementedException();
    }
}
