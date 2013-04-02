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
package de.arago.portlet.util;

import java.util.Collections;
import java.util.List;

public class Paginator {

    private long items;
    private int itemsPerPage;
    private int currentPage = 1;
    private int pageOffset = 2;

    public Paginator(long itemCount, int itemsPerPage) {
        this.items = itemCount;
        this.itemsPerPage = itemsPerPage;
    }

    public Paginator(long itemCount, int itemsPerPage, int pageOffset) {
        this(itemCount, itemsPerPage);
        this.pageOffset = pageOffset;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public int lastPage() {
        return (int) Math.ceil(items / (double) itemsPerPage);
    }

    public int firstPage() {
        return 1;
    }

    public void go(int where) {
        currentPage = where < 0 ? 0 : (where > lastPage() ? lastPage() : where);
    }

    public int nextPage() {
        return currentPage + (currentPage >= lastPage() ? 0 : 1);
    }

    public int prevPage() {
        return currentPage - (currentPage > 0 ? 1 : 0);
    }

    public List<Object> slice(List<Object> entries) {
        if (entries == null)
            return Collections.EMPTY_LIST;

        return entries.subList(getFirstItem(), getLastItem());
    }

    private int getFirstItem() {
        return (currentPage - 1) * itemsPerPage;
    }

    private int getLastItem() {
        return (int) (currentPage == lastPage() || items < itemsPerPage ? items : (nextPage() - 1) * itemsPerPage);
    }

    public boolean hasSkipToFirst() {
        return currentPage > pageOffset + 1;
    }

    public boolean hasSkipToLast() {
        return currentPage < this.lastPage() - pageOffset;
    }

    public int firstOffset() {
        return currentPage - pageOffset <= 1 ? 1 : currentPage - pageOffset;
    }

    public int lastOffset() {
        return currentPage + pageOffset >= this.lastPage() ? this.lastPage() : currentPage + pageOffset;
    }
}
