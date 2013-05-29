package de.arago.rike.task.json;

import de.arago.data.IDataWrapper;
import de.arago.portlet.JsonAction;
import de.arago.rike.util.TaskFinder;
import org.json.simple.JSONObject;

public class FindTask implements JsonAction {

    @Override
    public JSONObject execute(IDataWrapper data) throws Exception {
        JSONObject result = new JSONObject();
        result.put("items", TaskFinder.findTasksWithFreetextQuery(data.getRequestAttribute("q"), 10));

        return result;

    }
}
