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

import de.arago.data.IEventWrapper;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * The EventDispatcher dispatches Events
 *
 * based on the class namespace the dispatcher will look for event classes
 * in [namespace].event.Eventname,
 *
 * e.g.
 * class:  de.arago.portlet.some.ThePortlet
 * event: testEvent
 *
 * will lead to loading the class
 * de.arago.portlet.some.event.TestEvent
 * and execute it
 */

public class EventDispatcher extends PortletDispatcher<Event> {
    private static final Logger logger = Logger.getLogger(EventDispatcher.class.getName());

    public EventDispatcher(Class<?> who) {
        super(who, "event");
    }

    /**
     * lookup and execute event,
     * if the event cannot be found (as a class or instance) nothing will be executed
     *
     * @param data the data passed to event
     */
    public void dispatch(IEventWrapper data) {
        dispatch(data.getName(), data);
    }

    /**
     * lookup and execute event,
     * if the event cannot be found (as a class or instance) nothing will be executed
     *
     * @param name the name of the event, name will be sanitized to [a-zA-Z0-9]
     * @param data the data passed to event
     */
    public void dispatch(String name, IEventWrapper data) {
        Event event = getDispatchable(name);

        try {
            if (event != null) event.execute(data);
        } catch(Throwable t) {
            logger.log(Level.SEVERE, "event " + name + " failed ", t);
        }
    }
}
