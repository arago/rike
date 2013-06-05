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

import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.KeyValuePair;
import com.liferay.portal.model.Role;
import com.liferay.portal.model.User;
import com.liferay.portal.model.UserGroup;
import com.liferay.portal.service.RoleLocalServiceUtil;
import com.liferay.portal.service.UserGroupLocalServiceUtil;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.util.PortalUtil;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.codec.binary.Base64;



final class UserContainer {
    private final User user;
    private final long timestamp;

    public UserContainer(User user) {
        this.user      = user;
        this.timestamp = System.currentTimeMillis();
    }

    public boolean isExpired() {
        return (timestamp + (5 * 60 * 1000)) < System.currentTimeMillis();
    }

    /**
     * @return the user
     */
    public User getUser() {
        return user;
    }
}
public class SecurityHelper {

    private static final ConcurrentMap<String, UserContainer> cache      = new ConcurrentHashMap<String, UserContainer>();
    private static final ConcurrentMap<String, UserContainer> emailCache = new ConcurrentHashMap<String, UserContainer>();

    /**
     * enable this if your frontend proxy does basic authentication
     */
    private static final boolean basicAuthEnabled = "true".equals(System.getProperty("de.arago.security.basicauth"));


    private static void refreshUsers() throws Exception {
        for (User user: UserLocalServiceUtil.getUsers(0, QueryUtil.ALL_POS)) {
            cache.put(user.getScreenName(), new UserContainer(user));
            emailCache.put(user.getEmailAddress(), new UserContainer(user));
        }
    }



    private static User getUserFromCookies(HttpServletRequest request) throws Exception {
        // https://www.everit.biz/web/guest/everit-blog/-/blogs/getting-current-liferay-user-in-a-standalone-webapp
        final Cookie[] cookies	= request.getCookies() == null?new Cookie[0]:request.getCookies();
        String userId			= null;
        String password		= null;
        String companyId	= null;

        for (Cookie c : cookies) {
            if ("COMPANY_ID".equals(c.getName())) {
                companyId = c.getValue();
            } else if ("ID".equals(c.getName())) {
                userId		= hexStringToStringByAscii(c.getValue());
            } else if ("PASSWORD".equals(c.getName())) {
                password	= hexStringToStringByAscii(c.getValue());
            }
        }

        if (userId != null && password != null && companyId != null) {
            final KeyValuePair kvp = UserLocalServiceUtil.decryptUserId(Long.parseLong(companyId), userId, password);
            return getUser(kvp.getKey());
        }

        return null;
    }

    private static User lookupUserFromCache(String name, String pass, boolean mayRefreshCache) throws Exception {
        UserContainer container = cache.get(name);

        if (container != null && !container.isExpired()) return container.getUser();

        if (mayRefreshCache) {
            refreshUsers();

            return lookupUserFromCache(name, pass, false);
        }

        return null;
    }

    private static User getUserFromAuthHeader(HttpServletRequest request) throws Exception {
        if (!basicAuthEnabled) return null;

        String auth					 = request.getHeader("Authorization");
        if (auth == null) return null;

        auth = new String(Base64.decodeBase64(auth.replaceAll("^Basic\\ +", "")));

        final String[] parts = auth.split(":");
        if (parts == null || parts.length == 0 || parts[0] == null || parts[0].isEmpty()) return null;

        return lookupUserFromCache(parts[0], parts[1], true);
    }

    public static User getUserFromRequest(HttpServletRequest request) {
        try {
            User user = getUserFromLiferay(request);

            if (user == null) user = getUserFromAuthHeader(request);
            if (user == null) user = getUserFromCookies(request);

            return user;
        } catch (Exception e) {
            Logger.getLogger(SecurityHelper.class.getName()).log(Level.SEVERE, null, e);
        }

        return null;
    }


    public static User getUserByEmail(String email) {
        try {
            return lookupUserFromEmailCache(email, true);
        } catch(Exception e) {
            return null;
        }
    }

    private static User lookupUserFromEmailCache(String email, boolean mayRefreshCache) throws Exception {
        UserContainer container = emailCache.get(email);

        if (container != null && !container.isExpired()) return container.getUser();

        if (mayRefreshCache) {
            refreshUsers();

            return lookupUserFromEmailCache(email, false);
        }

        return null;
    }

    private static String hexStringToStringByAscii(String hexString) {
        byte[] bytes = new byte[hexString.length()/2];
        for (int i = 0; i < hexString.length() / 2; i++) {
            String oneHexa = hexString.substring(i * 2, i * 2 + 2);
            bytes[i] = Byte.parseByte(oneHexa, 16);
        }
        try {
            return new String(bytes, "ASCII");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }


    public static User getUser(String user) {
        try {
            return UserLocalServiceUtil.getUserById(Long.valueOf(user,10));
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static String getUserEmail(String user) {
        try {
            User u = getUser(user);
            if(u!=null)
                return u.getEmailAddress();
        } catch(Exception e) {}
        return "";
    }

    public static boolean isLoggedIn(String user) {
        return user != null && user.length() > 0;
    }

    public static String[] getUserGroups(String userId) {
        if (userId == null || userId.isEmpty()) return new String[0];
        ArrayList<String> result = new ArrayList<String>();

        try {
            List<UserGroup> groups = UserGroupLocalServiceUtil.getUserUserGroups(Long.valueOf(userId,10));
            for(UserGroup g: groups) {
                result.add(g.getName());
            }
        } catch (Exception ex) {
            Logger.getLogger(SecurityHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result.toArray(new String[0]);
    }
    
    public static String[] getUserRoles(String userId) {
        if (userId == null || userId.isEmpty()) return new String[0];
        ArrayList<String> result = new ArrayList<String>();

        try {
            List<Role> roles = RoleLocalServiceUtil.getUserRoles(Long.valueOf(userId,10));
            for(Role g: roles) {
                result.add(g.getName());
            }
        } catch (Exception ex) {
            Logger.getLogger(SecurityHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result.toArray(new String[0]);
    }

    public static List<String[]> getUserGroupsWithNames(String user) {
        if (user == null || user.isEmpty()) return Collections.EMPTY_LIST;

        ArrayList<String[]> result = new ArrayList<String[]>();

        try {
            List<UserGroup> groups = UserGroupLocalServiceUtil.getUserUserGroups(Long.valueOf(user,10));
            for(UserGroup g: groups) {
                result.add(new String[] {g.getName(),g.getDescription()});
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        return result;
    }

    private static User getUserFromLiferay(HttpServletRequest request) {
        try {
            return PortalUtil.getUser(request);
        } catch (PortalException ex) {
            return null;
        } catch (SystemException ex) {
            return null;
        }
    }
}
