package de.arago.lucene.util;

import java.util.ArrayList;
import java.util.List;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.ScoreDoc;

public class StringArrayConverter extends BaseConverter<String[]> {

    public static final int ID = 0;
    public static final int XML = 1;
    public static final String FIELD_XML = "xml";

    @Override
    public Document toLuceneDocument(String[] rule) {
        Document doc = new Document();

        doc.add(new Field(FIELD_ID, rule[ID], Field.Store.YES, Field.Index.NOT_ANALYZED));
        doc.add(new Field(FIELD_XML, rule[XML], Field.Store.YES, Field.Index.NOT_ANALYZED));
        doc.add(new Field(FIELD_CONTENT, rule[XML], Field.Store.YES, Field.Index.ANALYZED));

        return doc;
    }

    @Override
    public List<String[]> resultToList() {
        List<String[]> result = new ArrayList<String[]>(hits.length);

        if (hits.length > 0) {
            for (ScoreDoc hit : hits) {
                try {
                    Document doc = searcher.doc(hit.doc);
                    result.add(new String[] {doc.get(FIELD_ID), doc.get(FIELD_XML)});
                } catch (Exception e) {
                    throw new IllegalStateException(e);
                }
            }
        }

        return result;
    }

    @Override
    public String[] getObject(int position) {
        try {
            Document doc = searcher.doc(hits[position].doc);
            return new String[] {doc.get(FIELD_ID), doc.get(FIELD_XML)};
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Term toLuceneID(String[] rule) {
        return new Term(FIELD_ID, rule[ID]);
    }
}
