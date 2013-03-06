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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.arago.rike.util;

import com.liferay.portal.model.User;
import de.arago.portlet.util.SecurityHelper;


public final class PortraitHelper {
    private PortraitHelper() {}

    public static String getUserPortraitByEmail(String email) {
        try {
            User user = SecurityHelper.getUserByEmail(email);

            if (user == null) return "/image/user_male_portrait?img_id=0";

            return "/image/user_" + (user.isFemale() ? "female" : "male") + "_portrait?img_id=" + user.getPortraitId();
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }
}
