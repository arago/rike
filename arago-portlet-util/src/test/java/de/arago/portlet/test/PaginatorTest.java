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
package de.arago.portlet.test;

import de.arago.portlet.util.Paginator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import junit.framework.TestCase;


public class PaginatorTest extends TestCase {

    public PaginatorTest(String testName) {
        super(testName);
    }

    public void testNormal() {
        List<Object> page1 = Arrays.asList(new Object[] {1, 2, 3});
        List<Object> page2 = Arrays.asList(new Object[] {4, 5, 6});
        List<Object> page3 = Arrays.asList(new Object[] {7, 8, 9});
        List<Object> page4 = Arrays.asList(new Object[] {10, 11, 12});
        List<Object> page5 = Arrays.asList(new Object[] {13});

        List<Object> items = Arrays.asList(new Object[] {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13});

        Paginator pager = new Paginator(items.size(), 3);

        assertEquals(1, pager.firstPage());
        assertEquals(5, pager.lastPage());

        pager.go(1);
        assertEquals(1, pager.getCurrentPage());
        assertEquals(page1, pager.slice(items));

        pager.go(2);
        assertEquals(2, pager.getCurrentPage());
        assertEquals(page2, pager.slice(items));

        pager.go(3);
        assertEquals(3, pager.getCurrentPage());
        assertEquals(page3, pager.slice(items));

        pager.go(4);
        assertEquals(4, pager.getCurrentPage());
        assertEquals(page4, pager.slice(items));

        pager.go(5);
        assertEquals(5, pager.getCurrentPage());
        assertEquals(page5, pager.slice(items));

        // pager may not overrun boundaries
        pager.go(6);
        assertEquals(5, pager.getCurrentPage());
        assertEquals(page5, pager.slice(items));

    }


    public void testSmallerThanItemsPerPage() {
        List<Object> items = Arrays.asList(new Object[] {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13});

        Paginator pager = new Paginator(items.size(), 20);

        assertEquals(1, pager.firstPage());
        assertEquals(1, pager.lastPage());

        assertEquals(items, pager.slice(items));

    }


    public void testEven() {
        List<Object> items = Arrays.asList(new Object[] {1, 2, 3, 4});

        Paginator pager = new Paginator(items.size(), 2);

        assertEquals(1, pager.firstPage());
        assertEquals(2, pager.lastPage());
    }

    public void testUnEven() {
        List<Object> items = Arrays.asList(new Object[] {1, 2, 3});

        Paginator pager = new Paginator(items.size(), 2);

        assertEquals(1, pager.firstPage());
        assertEquals(2, pager.lastPage());
    }

    public void testSmallerThanItemsPerPageBug() {
        List<Object> items = Arrays.asList(new Object[] {1, 2, 3, 4});

        Paginator pager = new Paginator(items.size(), 20);

        pager.go(2);
        assertEquals(1, pager.firstPage());
        assertEquals(1, pager.lastPage());

        assertEquals(items, pager.slice(items));

    }

    public void testNullList() {
        Paginator pager = new Paginator(4, 4);

        pager.go(2);
        assertEquals(1, pager.firstPage());
        assertEquals(1, pager.lastPage());

        assertEquals(Collections.EMPTY_LIST, pager.slice(null));

    }

    public void testSmaller() {
        List<Object> items = Arrays.asList(new Object[] {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14});

        Paginator pager = new Paginator(items.size(), 5);

        assertEquals(1, pager.firstPage());
        assertEquals(3, pager.lastPage());
    }

    public void testBug() {
        // 20 and 34
        List<Object> items = new ArrayList<Object>();

        for (int i = 0; i < 34; ++i ) items.add(i);

        Paginator pager = new Paginator(items.size(), 20);

        assertEquals(1, pager.firstPage());
        assertEquals(2, pager.lastPage());
    }

}
