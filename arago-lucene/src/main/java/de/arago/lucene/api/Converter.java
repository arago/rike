package de.arago.lucene.api;

import java.util.List;
import net.minidev.json.JSONAware;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;

public interface Converter<T> extends Iterable<T>, JSONAware {
    public static final String FIELD_ID				= "id";
    public static final String FIELD_CONTENT	= "content";
    public static final String FIELD_JSON		  = "json";

    public void setResult(ScoreDoc[] hits, IndexSearcher searcher);

    public Document toLuceneDocument(T o);

    public T getObject(int position);
    public float getRating(int position);
    public int size();
    public Term  toLuceneID(T o);
    public List<T> resultToList();
}

