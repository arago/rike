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

import de.arago.data.IEventWrapper;
import de.arago.portlet.EventDispatcher;
import java.util.HashMap;
import java.util.Map;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

public class EventDispatcherTest {

    private static class TestDataWrapper implements IEventWrapper {

        private Map<Object,Object> data;

        public TestDataWrapper(Map<Object,Object> data) {
            this.data = data;
        }

        @Override
        public String getName() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void setName(String name) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Object getEventAttribute(String key) {
            return data.get(key);
        }

        @Override
        public void setEventAttribute(String key, Object value) {
            data.put(key, value);
        }

        @Override
        public Object getSessionAttribute(String key) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void setSessionAttribute(String key, Object value) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void removeSessionAttribute(String key) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public String getUser() {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }

    public EventDispatcherTest() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testLoad() {
        EventDispatcher a = new EventDispatcher(EventDispatcherTest.class);
        assertEquals("de.arago.data.test.event.", a.getNamespace());
    }

    @Test
    public void testDispatchDoesNotExist() {
        EventDispatcher a = new EventDispatcher(EventDispatcherTest.class);
        Map<Object,Object> data = new HashMap<Object,Object>();
        a.dispatch("testEventDoesNotExist", new TestDataWrapper(data));
    }

    @Test
    public void testDispatch() {
        EventDispatcher a = new EventDispatcher(EventDispatcherTest.class);
        Map<Object,Object> data = new HashMap<Object,Object>();
        a.dispatch("DispatcherTestEvent", new TestDataWrapper(data));
        assertEquals("event executed", data.get("ok"));
    }
}
