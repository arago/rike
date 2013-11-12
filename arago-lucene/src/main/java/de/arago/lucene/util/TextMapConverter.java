package de.arago.lucene.util;

import static de.arago.lucene.api.Converter.FIELD_CONTENT;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.ScoreDoc;

public class TextMapConverter extends BaseConverter<Map<String, String>> {

    StringBuilder all = new StringBuilder();

    @Override
    public Document toLuceneDocument(Map<String, String> cond) {

        String id = cond.get(FIELD_ID);
        if (id == null) {
            id = "" + cond.hashCode();
        }

        Document doc = new Document();
        doc.add(new Field(FIELD_ID, id, Field.Store.YES, Field.Index.NOT_ANALYZED));

        all.setLength(0);

        for (Map.Entry<String, String> o : cond.entrySet()) {
            if (o.getValue() == null) {
                continue;
            }

            if (!FIELD_ID.equals(o.getKey())) {
                doc.add(new Field(o.getKey(), o.getValue(), Field.Store.YES, Field.Index.ANALYZED));
                all.append(o.getValue());
                all.append(' ');
            }
        }

        doc.add(new Field(FIELD_CONTENT, all.toString(), Field.Store.NO, Field.Index.ANALYZED));
        return doc;
    }

    @Override
    public List<Map<String, String>> resultToList() {
        List<Map<String, String>> result = new ArrayList<Map<String, String>>(hits.length);

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
    public Map<String, String> getObject(int position) {
        try {
            return getObject(searcher.doc(hits[position].doc));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Map<String, String> getObject(Document doc) {
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
    public Term toLuceneID(Map<String, String> cond) {
        String id = cond.get(FIELD_ID);
        if (id == null)
            id = "" + cond.hashCode();
        return new Term(FIELD_ID, id);
    }
}
