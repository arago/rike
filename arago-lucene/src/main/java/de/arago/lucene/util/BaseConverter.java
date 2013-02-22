package de.arago.lucene.util;

import de.arago.lucene.api.Converter;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.lang.NotImplementedException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.json.simple.JSONArray;

public abstract class BaseConverter<T> implements Converter<T>{
	protected IndexSearcher searcher;
	protected ScoreDoc[] hits;

	public float getRating(int position) {
		return hits[position].score;
	}

	public int size() {
		return hits.length;
	}

	public void init(IndexSearcher s) {
		this.searcher = s;
	}

	public void setResult(ScoreDoc[] hits) {
		this.hits = hits;
	}
	
	public String toJSONString() {
		List<T> all = resultToList();
		return JSONArray.toJSONString(all);
	}

	public Iterator<T> iterator() {
		return Collections.unmodifiableList(resultToList()).iterator();
	}

	public List<T> resultToList() {
		throw new NotImplementedException();
	}
	
}
