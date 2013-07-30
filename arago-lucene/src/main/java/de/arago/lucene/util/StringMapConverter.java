package de.arago.lucene.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.ScoreDoc;

public class StringMapConverter extends BaseConverter<Map<String,String>> {

    @Override
    public Document toLuceneDocument(Map<String,String> cond) {
        Document doc = new Document();

        Object id = cond.get("id");
        if (id == null) {
            id = "" + cond.hashCode();
        }

        doc.add(new Field(FIELD_ID, id.toString(), Field.Store.YES, Field.Index.NOT_ANALYZED));
        for (Map.Entry<String, String> e : cond.entrySet()) {
            if (e.getKey() == null || e.getValue() == null) continue;

            if (!"id".equals(e.getKey())) {
                doc.add(new Field(e.getKey().toString(), e.getValue().toString(), Field.Store.YES, Field.Index.ANALYZED));
            }
        }

        return doc;
    }

    @Override
    public List<Map<String,String>> resultToList() {
        List<Map<String,String>> result = new ArrayList<Map<String,String>>(hits.length);

        if (hits.length > 0) {
            for (ScoreDoc hit : hits) {
                try {
                    Document doc = searcher.doc(hit.doc);
                    result.add(getObject(doc));
                } catch (Exception e) {
                    throw new IllegalStateException(e);
                }
            }
        }

        return result;
    }

    @Override
    public Map<String,String> getObject(int position) {
        try {
            return getObject(searcher.doc(hits[position].doc));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Map<String,String> getObject(Document doc) {
        TreeMap<String, String> result = new TreeMap<String, String>();
        for (Object s : doc.getFields()) {
            if (s instanceof Field) {
                Field f = (Field) s;
                result.put(f.name(), f.stringValue());
            }
        }
        return result;
    }

    @Override
    public Term toLuceneID(Map<String,String> cond) {
        Object id = cond.get("id");

        if (id == null) {
            id = "" + cond.hashCode();
        }

        return new Term(FIELD_ID, id.toString());
    }
}
