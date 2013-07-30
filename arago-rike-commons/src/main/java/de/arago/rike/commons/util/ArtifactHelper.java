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
package de.arago.rike.commons.util;

import de.arago.rike.commons.data.Artifact;
import de.arago.rike.commons.data.DataHelperRike;
import java.util.List;
import org.hibernate.criterion.Order;

/**
 *
 */
public final class ArtifactHelper {

    private ArtifactHelper() {
        //not called
    }

    public static List<Artifact> list() {
        DataHelperRike<Artifact> helper = new DataHelperRike<Artifact>(Artifact.class);

        return helper.list(helper.filter().addOrder(Order.asc("id")));
    }

    public static Artifact getArtifact(String id) {
        if (id == null || id.isEmpty()) return null;

        return new DataHelperRike<Artifact>(Artifact.class).find(id);
    }
}
