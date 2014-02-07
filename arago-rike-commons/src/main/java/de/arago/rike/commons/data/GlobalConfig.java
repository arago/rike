/**
 * Copyright (c) 2010 arago AG, http://www.arago.de/
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package de.arago.rike.commons.data;

import java.util.HashMap;
import java.util.Map;

public class GlobalConfig {

    public static final String PRIORITY_MAXIMAL_NUMBER      = "PRIORITY_MAXIMAL_NUMBER";
    public static final String PRIORITY_NORMAL              = "PRIORITY_NORMAL";
    public static final String WORKFLOW_TYPE                = "WORKFLOW_TYPE";
    public static final String WORKFLOW_DAYS_TO_FINISH_TASK = "WORKFLOW_DAYS_TO_FINISH_TASK";
    public static final String WORKFLOW_DAYS_TOP_PRIO_TASK  = "WORKFLOW_DAYS_TOP_PRIO_TASK";
    public static final String WORKFLOW_WIP_LIMIT           = "WORKFLOW_WIP_LIMIT";
    public static final String WORKFLOW_TIME_OFFSET         = "WORKFLOW_TIME_OFFSET";
    public static final String CHECK_PERIOD_SECONDS         = "CHECK_PERIOD_SECONDS";
    public static final String PATH_TO_PERSONAL_PICS        = "PATH_TO_PERSONAL_PICS";

    private static Map keyValues = new HashMap();
    private long id;
    private String key;
    private String value;

    public static void fetchFromDatabase() {
        keyValues.clear();

        DataHelperRike<GlobalConfig> helper = new DataHelperRike<GlobalConfig>(GlobalConfig.class);

        for (GlobalConfig param : helper.list()) {
            keyValues.put(param.getKey(), param.getValue());
        }

        checkDefaults(keyValues);
    }

    private static void checkDefaults(Map map) {
        if (map.get(PRIORITY_MAXIMAL_NUMBER) == null) {
            map.put(PRIORITY_MAXIMAL_NUMBER, "3");
        }

        if (map.get(PRIORITY_NORMAL) == null) {
            map.put(PRIORITY_NORMAL, "2");
        }

        if (map.get(WORKFLOW_TYPE) == null) {
            map.put(WORKFLOW_TYPE, "arago Technologies");
        }

        if (map.get(WORKFLOW_DAYS_TO_FINISH_TASK) == null) {
            map.put(WORKFLOW_DAYS_TO_FINISH_TASK, "5");
        }

        if (map.get(WORKFLOW_DAYS_TOP_PRIO_TASK) == null) {
            map.put(WORKFLOW_DAYS_TOP_PRIO_TASK, "7");
        }

        if (map.get(WORKFLOW_WIP_LIMIT) == null) {
            map.put(WORKFLOW_WIP_LIMIT, "3");
        }

        if (map.get(WORKFLOW_TIME_OFFSET) == null) {
            map.put(WORKFLOW_TIME_OFFSET, "24");
        }

        if (map.get(CHECK_PERIOD_SECONDS) == null) {
            map.put(CHECK_PERIOD_SECONDS, "60");
        }
    }

    public static String get(final String key) {
        return (String) keyValues.get(key);
    }
    
    public static void set(final String key, final String value) {
        keyValues.put(key, value);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
