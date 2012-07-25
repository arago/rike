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

import java.util.Map;
import de.arago.data.IEventWrapper;
import java.util.HashMap;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class EventDispatcherTest {

	private static class testClass {
	}

	private static class TestDataWrapper implements IEventWrapper {

		private Map<Object,Object> data;

		public TestDataWrapper(Map<Object,Object> data) {
			this.data = data;
		}

		public String getName() {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		public void setName(String name) {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		public Object getEventAttribute(String key) {
			return data.get(key);
		}

		public void setEventAttribute(String key, Object value) {
			data.put(key, value);
		}

		public Object getSessionAttribute(String key) {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		public void setSessionAttribute(String key, Object value) {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		public void removeSessionAttribute(String key) {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		public String getUser() {
			throw new UnsupportedOperationException("Not supported yet.");
		}
	}

	public EventDispatcherTest() {
	}

	@BeforeClass
	public static void setUpClass() throws Exception {
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
	}

	@Before
	public void setUp() {
	}

	@After
	public void tearDown() {
	}

	@Test
	public void testLoad() {
		EventDispatcher a = new EventDispatcher(testClass.class);
		assertEquals(a.getNamespace(), "de.arago.portlet.event.");
	}

	@Test
	public void testDispatchDoesNotExist() throws Exception {
		EventDispatcher a = new EventDispatcher(testClass.class);
		Map<Object,Object> data = new HashMap<Object,Object>();
		a.dispatch("testEventDoesNotExist", new TestDataWrapper(data));
	}

	@Test
	public void testDispatch() throws Exception {
		EventDispatcher a = new EventDispatcher(testClass.class);
		Map<Object,Object> data = new HashMap<Object,Object>();

		a.dispatch("DispatcherTestEvent", new TestDataWrapper(data));


		assertEquals("event executed", data.get("ok"));

	}
}
