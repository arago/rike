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
package de.arago.portlet;

import java.util.Enumeration;

import org.junit.Test;
import java.util.Map;
import java.util.HashMap;

import de.arago.data.IDataWrapper;
import static org.junit.Assert.*;

public class ActionDispatcherTest {

    private static class testClass {
    }

    private static class TestDataWrapper implements IDataWrapper {
        private Map<Object, Object> data = new HashMap<Object, Object>();

        public TestDataWrapper(Map<Object, Object> data) {
            this.data = data;
        }

        public void setSessionAttribute(String key, Object value) {
            data.put(key, value);
        }

        public void setRequestAttribute(String key, Object value) {


        }

        public void setEvent(String key, HashMap<String, Object> event) {


        }

        public void removeSessionAttribute(String key) {


        }

        public Enumeration<String> getSessionAttributeNames() {

            return null;
        }

        public Object getSessionAttribute(String key) {

            return null;
        }

        public Enumeration<String> getRequestAttributeNames() {

            return null;
        }

        public String getRequestAttribute(String key) {

            return null;
        }

        public String getUser() {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }

    @Test
    public void testLoad() {
        ActionDispatcher a = new ActionDispatcher(testClass.class);
        assertEquals(a.getNamespace(), "de.arago.portlet.action.");
    }

    @Test
    public void testDispatchDoesNotExist() throws IllegalAccessException {
        ActionDispatcher a 					 = new ActionDispatcher(testClass.class);
        HashMap<Object, Object> data = new HashMap<Object, Object>();

        a.dispatch("testActionDoesNotExist", new TestDataWrapper(data));

        if (data.containsKey("testActionExecuted")) throw new IllegalAccessException("action was executed");
    }

    @Test
    public void testDispatch() throws IllegalAccessException {
        ActionDispatcher a 					 = new ActionDispatcher(testClass.class);
        HashMap<Object, Object> data = new HashMap<Object, Object>();

        a.dispatch("dispatcherTestAction", new TestDataWrapper(data));

        if (!data.containsKey("testActionExecuted")) throw new IllegalAccessException("action was not executed");
    }

}
