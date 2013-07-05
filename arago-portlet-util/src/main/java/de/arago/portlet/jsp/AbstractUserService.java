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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import org.ocpsoft.prettytime.PrettyTime;

public abstract class AbstractUserService implements UserService {
    abstract public User getUser();

    @Override
    public TimeZone getTimeZone() {
        return getUser().getTimeZone();
    }

    @Override
    public Locale getLocale() {
        // TODO english for now
        //return getUser().getLocale();

        return Locale.ENGLISH;
    }

    @Override
    public String getEmail() {
        return getUser().getEmailAddress();
    }

    @Override
    public String getName() {
        try {
            return getUser().getLogin();
        } catch(Exception e) {
            throw new RuntimeException();
        }
    }

    @Override
    public String formatDate(Date date, String format) {
        SimpleDateFormat f = new SimpleDateFormat(format, getLocale());
        f.setTimeZone(getTimeZone());

        return f.format(date);
    }

    @Override
    public String formatDate(Date date) {
        return formatDate(date, "yyyy-MM-dd");
    }

    @Override
    public String formatHumanDate(Date date) {
        PrettyTime f = new PrettyTime(getLocale());

        return f.format(date);
    }
}
