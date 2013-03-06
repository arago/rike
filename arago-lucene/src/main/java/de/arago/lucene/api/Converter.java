package de.arago.lucene.api;

import java.util.List;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.json.simple.JSONAware;

public interface Converter<T> extends Iterable<T>, JSONAware {
    public static final String FIELD_ID				= "id";
    public static final String FIELD_CONTENT	= "content";
    public static final String FIELD_JSON		  = "json";

    public void init(IndexSearcher searcher);
    public Document toLuceneDocument(T o);
    public void setResult(ScoreDoc[] hits);
    public T getObject(int position);
    public float getRating(int position);
    public int size();
    public Term  toLuceneID(T o);
    public List<T> resultToList();
}

