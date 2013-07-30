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
package de.arago.debug.performance;

import java.util.logging.Level;
import java.util.logging.Logger;


public final class Performance {
    private static final Logger log = Logger.getLogger(Performance.class.getName());

    private static final boolean enabled;
    private static final String prefix;
    private static final String configPrefix = "de.arago.debug.perf.";

    private static final StatsdClient client;

    private Performance() {
        //not called
    }

    static {
        final String host = System.getProperty(configPrefix + "host", "");

        if (host != null && !host.isEmpty()) {
            prefix  = System.getProperty(configPrefix + "prefix", "");
            int port = 8125;

            try {
                port = Integer.valueOf(System.getProperty(configPrefix + "port", "8125"), 10);
            } catch(Exception e) {
                // blank
            }

            StatsdClient tmpClient = null;

            try {
                tmpClient = new StatsdClient(host, port);
            } catch (Exception ex) {
                log.log(Level.SEVERE, null, ex);
            }


            client  = tmpClient;
            enabled = client != null;

            if (enabled) log.log(Level.INFO, "{Performance} statistics will be gathered on " + host + ":" + port);
        } else {
            client  = null;
            enabled = false;
            prefix  = null;
        }
    }

    /**
     * increment a key
     * @param key
     */
    public static void increment(String key) {
        if (enabled) client.increment(prefix + key);
    }


    /**
     * decrement a key
     * @param key
     */
    public static void decrement(String key) {
        if (enabled) client.decrement(prefix + key);
    }

    /**
     * time some operation
     *
     * @param key
     * @param howLong
     */
    public static void timing(String key, long howLong) {
        if (!enabled) return;

        int t;

        if (howLong < Integer.MIN_VALUE) {
            t = Integer.MIN_VALUE;
        } else if (howLong > Integer.MAX_VALUE) {
            t = Integer.MAX_VALUE;
        } else {
            t = (int) howLong;
        }

        client.timing(prefix + key, t);
    }

    public static boolean isEnabled() {
        return enabled;
    }
}
