package de.arago.lucene.util;

import de.arago.lucene.api.Converter;
import de.arago.lucene.api.IndexFactory;
import java.util.List;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONValue;
import org.apache.lucene.queryparser.classic.QueryParser;


public class NodeSearcher {

    private NodeSearcher() {
        //not called
    }

    /**
     * find nodes in a freetext index
     *
     * @param query
     * @param maxItems
     * @return found ids, it will never return null, but an empty List instead
     */
    public static List findNodesWithFreetextQuery(String query, int maxItems) {
        Converter<?> result = IndexFactory.getIndex("mars-node").query(prepareQuery(query), maxItems);

        return (JSONArray) JSONValue.parse(result.toJSONString());
    }

    private static boolean isJustANumber(String what) {
        return what.trim().matches("^\\d+$");
    }

    private static String prepareQuery(String query) {
        StringBuilder q = new StringBuilder();

        for (String part : query.trim().replaceAll("[\\:\\.]", " ").replaceAll("\\ \\ +", " ").split("\\ ")) {
            if (part == null || part.trim().length() == 0) {
                continue;
            }

            q.append("+content:(");
            q.append(escapeAndAllowWildcards(part.trim()));

            // we do not wildcard numbers, due to lucene throwing exceptions
            q.append(isJustANumber(part) ? "" : "*");
            q.append(") AND ");
        }

        query = q.toString();

        // cut off the last AND
        return query.substring(0, query.length() - 5);
    }

    public static String escapeAndAllowWildcards(String what) {
        return QueryParser.escape(what).replaceAll("\\\\\\*", "*").replaceAll("\\\\\\?", "?");
    }
}