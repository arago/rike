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

import de.arago.data.IDataWrapper;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The ActionDispatcher dispatches Actions
 *
 * based on the class namespace the dispatcher will look for action classes
 * in [namespace].action.ActionName,
 *
 * e.g.
 * class:  de.arago.portlet.some.ThePortlet
 * action: testAction
 *
 * will lead to loading the class
 * de.arago.portlet.some.action.TestAction
 * and execute it
 */

public class ActionDispatcher extends PortletDispatcher<Action> {
    private static final Logger logger = Logger.getLogger(ActionDispatcher.class.getName());

    public ActionDispatcher(Class<?> forWho, String type) {
        super(forWho, type);
    }

    public ActionDispatcher(Class<?> forWho) {
        super(forWho, "action");
    }

    /**
     * lookup and execute action,
     * if the action cannot be found (as a class or instance) nothing will be executed
     *
     * @param actionName the name of the action, name will be sanitized to [a-zA-Z0-9]
     * @param data the data passed to action
     */
    public void dispatch(String actionName, IDataWrapper data) {
        Action action = getDispatchable(actionName);

        try {
            if (action != null) action.execute(data);
        } catch(Throwable t) {
            logger.log(Level.SEVERE, "action "+actionName+" failed ", t);
        }
    }
}
