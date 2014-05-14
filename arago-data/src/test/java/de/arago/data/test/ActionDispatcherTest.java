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
package de.arago.data.test;

import de.arago.data.IDataWrapper;
import de.arago.portlet.ActionDispatcher;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ActionDispatcherTest {

    private static class TestDataWrapper implements IDataWrapper {
        private Map<Object, Object> data = new HashMap<Object, Object>();

        public TestDataWrapper(Map<Object, Object> data) {
            this.data = data;
        }

        @Override
        public void setSessionAttribute(String key, Object value) {
            data.put(key, value);
        }

        @Override
        public void setRequestAttribute(String key, Object value) {


        }

        @Override
        public void setEvent(String key, HashMap<String, Object> event) {


        }

        @Override
        public void removeSessionAttribute(String key) {


        }

        @Override
        public Enumeration<String> getSessionAttributeNames() {

            return null;
        }

        @Override
        public Object getSessionAttribute(String key) {

            return null;
        }

        @Override
        public Enumeration<String> getRequestAttributeNames() {

            return null;
        }

        @Override
        public String getRequestAttribute(String key) {

            return null;
        }

        @Override
        public String getUser() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

    @Override
    public Object getRequestData(String name)
    {
      throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

        @Override
        public String getPersistentPreference(String key, String def) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void setPersistentPreference(String key, String value) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }

    @Test
    public void testLoad() {
        ActionDispatcher a = new ActionDispatcher(ActionDispatcherTest.class);
        assertEquals("de.arago.data.test.action.",a.getNamespace());
    }

    @Test
    public void testDispatchDoesNotExist() throws IllegalAccessException {
        ActionDispatcher a 					 = new ActionDispatcher(ActionDispatcherTest.class);
        HashMap<Object, Object> data = new HashMap<Object, Object>();

        a.dispatch("testActionDoesNotExist", new TestDataWrapper(data));

        if (data.containsKey("testActionExecuted")) throw new IllegalAccessException("action was executed");
    }

    @Test
    public void testDispatch() throws IllegalAccessException {
        ActionDispatcher a 					 = new ActionDispatcher(ActionDispatcherTest.class);
        HashMap<Object, Object> data = new HashMap<Object, Object>();

        a.dispatch("dispatcherTestAction", new TestDataWrapper(data));

        if (!data.containsKey("testActionExecuted")) throw new IllegalAccessException("action was not executed");
    }

}
