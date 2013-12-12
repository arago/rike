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

    @Override
    public String toString() {
        return "UserContainer{" + "user=" + user + ", timestamp=" + timestamp + '}';
    }

    /**
     * @return the user
     */
    public User getUser() {
        return user;
    }
}

public final class SecurityHelper {

    private static final ConcurrentMap<String, UserContainer> cache      = new ConcurrentHashMap<String, UserContainer>();
    private static final ConcurrentMap<String, UserContainer> emailCache = new ConcurrentHashMap<String, UserContainer>();

    /**
     * enable this if your frontend proxy does basic authentication
     */
    private static final boolean trustReverseProxyAuth = "true".equals(System.getProperty("de.arago.security.trustReverseProxy"));

    /**
     * trust siteminder auth
     */
    private static final boolean trustSiteMinder = "true".equals(System.getProperty("de.arago.security.trustSiteMinder"));

    /**
     * siteminder auth header
     */
    private static final String siteMinderHeader = System.getProperty("de.arago.security.siteMinderHeader", "HTTP_MAIL");

    /**
     * enable debug logging
     */
    private static final boolean debug           = "true".equals(System.getProperty("de.arago.security.debug")) || true;

    private static final Logger LOG = Logger.getLogger(SecurityHelper.class.getName());

    static {
        if (debug) {
            log("trustReverseProxyAuth=" + trustReverseProxyAuth);
            log("trustSiteMinder=" + trustSiteMinder);
            log("siteMinderHeader=" + siteMinderHeader);
        }
    }


    private static void log(String message, Object ... parameters) {
        LOG.log(Level.INFO, message, parameters);
    }

    private SecurityHelper() {
        //not called
    }

    private static void refreshUsers() throws SystemException {

        if (debug) {
            log("refreshing users");
        }

        for (User user: UserLocalServiceUtil.getUsers(0, QueryUtil.ALL_POS)) {
            if (debug) {
                log("have user " + user.getScreenName() + ", " + user.getEmailAddress());
            }
            cache.put(user.getScreenName(), new UserContainer(user));
            emailCache.put(user.getEmailAddress(), new UserContainer(user));
        }
    }

    private static User getUserFromCookies(HttpServletRequest request) throws SystemException, PortalException {
        // https://www.everit.biz/web/guest/everit-blog/-/blogs/getting-current-liferay-user-in-a-standalone-webapp
        final Cookie[] cookies	= request.getCookies() == null?new Cookie[0]:request.getCookies();
        String userId			= null;
        String password		= null;
        String companyId	= null;

        if (debug) {
            log("getting user from cookies");
        }

        for (Cookie c : cookies) {
            if ("COMPANY_ID".equals(c.getName())) {
                companyId = c.getValue();
            } else if ("ID".equals(c.getName())) {
                userId		= hexStringToStringByAscii(c.getValue());
            } else if ("PASSWORD".equals(c.getName())) {
                password	= hexStringToStringByAscii(c.getValue());
            }
        }

        if (debug) {
            log("have cookies {0} {1} {2}", userId, password, companyId);
        }


        if (userId != null && password != null && companyId != null) {
            final KeyValuePair kvp = UserLocalServiceUtil.decryptUserId(Long.parseLong(companyId), userId, password);
            return getUser(kvp.getKey());
        }

        if (debug) {
            log("no cookies found");
        }

        return null;
    }

    private static User lookupUserFromCache(String name, String pass, boolean mayRefreshCache) throws SystemException {
        if (debug) {
            log("looking up user from cache {0} (refresh: {1})", pass, mayRefreshCache);
        }
        UserContainer container = name.contains("@")?emailCache.get(name):cache.get(name);

        if (container != null && !container.isExpired()) {
            if (debug) {
                log("found container " + container);
            }

            if (trustReverseProxyAuth) {
                if (debug) {
                    log("trusting reverse proxy");
                }
            } else {
                if (!container.getUser().getDigest().equals(container.getUser().getDigest(pass))) {
                    if (debug) {
                        log("password digests do not match");
                    }
                    return null;
                }
            }


            return container.getUser();
        }

        if (debug) {
            log("did not find a user");
        }


        if (mayRefreshCache) {
            refreshUsers();

            return lookupUserFromCache(name, pass, false);
        }

        return null;
    }

    private static User getUserFromAuthHeader(HttpServletRequest request) throws SystemException {
        if (debug) {
            log("getting user from auth header");
        }

        String auth					 = request.getHeader("Authorization");
        if (auth == null) {
            if (debug) {
                log("did not find an auth header");
            }
            return null;
        }

        auth = new String(Base64.decodeBase64(auth.replaceAll("^Basic\\ +", "")));

        if (debug) {
            log("have auth header: " + auth.substring(0, auth.indexOf(':')));
        }

        return lookupUserFromCache(auth.substring(0, auth.indexOf(':')), auth.substring(auth.indexOf(':') + 1), true);
    }

    private static User getUserFromSiteMinder(HttpServletRequest request) {
        if (debug) {
            log("getting user from siteminder");
        }

        if (!trustSiteMinder) {
            if(debug) {
                log("siteminder disabled");
            }
            return null;
        }

        final String header = request.getHeader(siteMinderHeader);

        if (debug) {
            log("have siteminder " + header);
        }

        if (header == null || header.isEmpty()) {
            return null;
        }


        return getUserByEmail(header);
    }

    public static User getUserFromRequest(HttpServletRequest request) {
        try {
            User user = getUserFromSiteMinder(request);

            if (user == null) {
                user = getUserFromLiferay(request);
            }

            if (user == null) {
                user = getUserFromCookies(request);
            }

            if (user == null) {
                user = getUserFromAuthHeader(request);
            }

            if (debug) {
                log("found user " + user.getEmailAddress());
            }

            return user;
        } catch (Throwable t) {
            t.printStackTrace();
            Logger.getLogger(SecurityHelper.class.getName()).log(Level.SEVERE, null, t);
        }

        if (debug) {
            log("did not find a user in the request");
        }

        return null;
    }


    public static User getUserByEmail(String email) {
        if(debug) {
            log("getting user by email " + email);
        }

        try {
            return lookupUserFromEmailCache(email, true);
        } catch(Exception e) {
            return null;
        }
    }

    private static User lookupUserFromEmailCache(String email, boolean mayRefreshCache) throws SystemException {
        if (debug) {
            log("looking up user from email cache " + email);
        }

        UserContainer container = emailCache.get(email);

        if (container != null && !container.isExpired()) {
            if (debug) {
                log("found user " + container);
            }
            return container.getUser();
        }

        if (debug) {
            log("did not find a user");
        }

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
        if (debug) {
            log("getting user from liferay " + user);
        }

        try {
            return UserLocalServiceUtil.getUserById(Long.valueOf(user,10));
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static String getUserEmail(String user) {
        if (debug) {
            log("getting user email from liferay " + user);
        }

        try {
            User u = getUser(user);
            if(u!=null) {
                if (debug) {
                    log("have user " + u.getEmailAddress());
                }
                return u.getEmailAddress();
            }
        } catch(Exception e) {}
        return "";
    }

    public static String getUserScreenName(String user) {
        if (debug) {
            log("getting user screen name " + user);
        }

        try {
            User u = getUser(user);
            if(u!=null) {
                if (debug) {
                    log("have user screenname " + u.getScreenName());
                }
                return u.getScreenName();
            }
        } catch(Exception e) {}
        return "";
    }

    public static boolean isLoggedIn(String user) {
        if (debug) {
            log("isloggedin " + user);
        }

        return user != null && user.length() > 0;
    }

    public static String[] getUserGroups(String userId) {
        if (debug) {
            log("getting user groups " + userId);
        }
        if (userId == null || userId.isEmpty()) {
            if (debug) {
                log("returning empty user groups");
            }
            return new String[0];
        }
        ArrayList<String> result = new ArrayList<String>();

        try {
            List<UserGroup> groups = UserGroupLocalServiceUtil.getUserUserGroups(Long.valueOf(userId,10));
            for(UserGroup g: groups) {
                result.add(g.getName());
            }
        } catch (Exception ex) {
            Logger.getLogger(SecurityHelper.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (debug) {
            log("returning user groups " + result);
        }

        return result.toArray(new String[0]);
    }

    public static String[] getUserRoles(String userId) {

        if (debug) {
            log("getting user roles " + userId);
        }

        if (userId == null || userId.isEmpty()) {
            if (debug) {
                log("returning empty roles");
            }
            return new String[0];
        }
        ArrayList<String> result = new ArrayList<String>();

        try {
            List<Role> roles = RoleLocalServiceUtil.getUserRoles(Long.valueOf(userId,10));
            for(Role g: roles) {
                result.add(g.getName());
            }
        } catch (Exception ex) {
            Logger.getLogger(SecurityHelper.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (debug) {
            log("returning roles " + result);
        }
        return result.toArray(new String[0]);
    }

    public static List<String[]> getUserGroupsWithNames(String user) {
        if (debug) {
            log("getting user groups with names " + user);
        }
        if (user == null || user.isEmpty()) {
            if (debug) {
                log("returning empty groups");
            }

            return Collections.EMPTY_LIST;
        }

        ArrayList<String[]> result = new ArrayList<String[]>();

        try {
            List<UserGroup> groups = UserGroupLocalServiceUtil.getUserUserGroups(Long.valueOf(user,10));
            for(UserGroup g: groups) {
                result.add(new String[] {g.getName(),g.getDescription()});
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        if (debug) {
            log("returning groups with names " + result);
        }

        return result;
    }

    private static User getUserFromLiferay(HttpServletRequest request) {
        if (debug) {
            log("getting user from request");
        }

        try {
            if (debug) {
                log("found liferay user " + PortalUtil.getUser(request).getEmailAddress());
            }
            return PortalUtil.getUser(request);
        } catch (Throwable ex) {
            if (debug) {
                log("no user in liferay found");
            }
            return null;
        }
    }
}
