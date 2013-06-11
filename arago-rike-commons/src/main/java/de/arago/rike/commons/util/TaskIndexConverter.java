package de.arago.rike.commons.util;

import static de.arago.lucene.api.Converter.FIELD_ID;
import de.arago.lucene.util.BaseConverter;
import de.arago.rike.commons.data.DataHelperRike;
import de.arago.rike.commons.data.Task;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.ScoreDoc;
import org.json.simple.JSONObject;


public class TaskIndexConverter extends BaseConverter<Task> {

    @Override
    public Document toLuceneDocument(Task task) {
        StringBuilder text = new StringBuilder(task.getId().toString()).append(" ");

        text.append(task.getTitle());
        String url = task.getUrl();
        // for wiki or mantis
        int index = url.indexOf("page=");
        if(index<0)
            index = url.indexOf("id=");
        if(index>0)
            url = url.substring(index);
        try {
            url = URLDecoder.decode(url,"UTF-8");
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
        text.append(" ");
        text.append(url);

        Document doc = new Document();

        JSONObject o = new JSONObject();
        o.put("name", task.getTitle());
        o.put("id", task.getId());
        o.put("status", task.getStatusEnum().toString());
        o.put("owner", task.getOwner());
        o.put("priority", ""+task.getPriority());

        doc.add(new Field(FIELD_ID, task.getId().toString(), Field.Store.YES, Field.Index.NOT_ANALYZED));
        doc.add(new Field(FIELD_JSON, o.toString(), Field.Store.YES, Field.Index.NOT_ANALYZED));
        doc.add(new Field(FIELD_CONTENT, text.toString(), Field.Store.YES, Field.Index.ANALYZED));

        return doc;
    }

    @Override
    public List<Task> resultToList() {
        DataHelperRike<Task> helper = new DataHelperRike<Task>(Task.class);

        List<Task> result = new ArrayList<Task>(hits.length);

        if (hits.length > 0) {
            for (ScoreDoc hit : hits) {
                try {
                    Document d = searcher.doc(hit.doc);
                    result.add(helper.find(d.get("id")));
                } catch(Exception e) {
                    throw new IllegalStateException(e);
                }
            }
        }

        return result;
    }

    @Override
    public Task getObject(int position) {
        return resultToList().get(position);
    }

    @Override
    public String toJSONString() {
        List<Object> result = new ArrayList<Object>();

        if (hits.length > 0) {
            for (ScoreDoc hit : hits) {
                try {
                    result.add(searcher.doc(hit.doc).get(FIELD_JSON));
                } catch(Exception e) {
                    throw new IllegalStateException(e);
                }
            }
        }

        return "[" + StringUtils.join(result, ",") + "]";
    }

    @Override
    public Term toLuceneID(Task task) {
        return new Term(FIELD_ID, task.getId().toString());
    }
}
