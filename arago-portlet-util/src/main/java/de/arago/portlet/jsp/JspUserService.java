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
package de.arago.portlet.jsp;

import com.liferay.portal.model.User;
import de.arago.portlet.util.SecurityHelper;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.servlet.http.HttpServletRequest;

public class JspUserService extends AbstractUserService {
    private final static String USER_IN_SESSION = "de.arago.user.loggedin";
    private final RenderRequest request;
    private final PortletSession session;
    private final HttpServletRequest servletRequest;


    public JspUserService(HttpServletRequest r) {
        servletRequest = r;
        request = null;
        session = null;
    }

    public JspUserService(RenderRequest request, PortletSession session) {
        this.request = request;
        this.session = session;
        servletRequest = null;
    }

    @Override
    public User getUser() {
        if (request != null && session != null) {
            User user = (User) session.getAttribute(USER_IN_SESSION);
            if (user != null) return user;

            user = SecurityHelper.getUser(request.getRemoteUser());
            session.setAttribute(USER_IN_SESSION, user);

            return user;
        } else if (servletRequest != null) {
            return SecurityHelper.getUserFromRequest(servletRequest);
        } else {
            return null;
        }
    }

}
