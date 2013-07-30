/**
 * Copyright (c) 2010 arago AG, http://www.arago.de/
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the
 * Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package de.arago.data.util;

import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

final class ConfigHelper {

    private static final String PREFIX = "de.arago.data.util.datasource.";
    private static final Pattern userPattern = Pattern.compile("user=([^&]+)");
    private static final Pattern passPattern = Pattern.compile("password=([^&]+)");

    private ConfigHelper() {
        //not called
    }

    static SessionFactory makeFactory(String datasource, Properties p) {
        final Configuration configuration = new Configuration();
        configuration.configure();

        String jdbcUrl = System.getProperty(PREFIX + datasource);

        if (jdbcUrl == null || jdbcUrl.isEmpty()) {
            jdbcUrl = "jdbc:mysql://127.0.0.1/rike?user=rike&password=rike&useUnicode=true&characterEncoding=UTF-8";
        }

        configuration.setProperty("hibernate.connection.url", jdbcUrl);
        setCredentials(configuration, jdbcUrl);

        setDebug(configuration);
        setAdditionalProperties(configuration, p);

        return configuration.buildSessionFactory();
    }

    private static void setCredentials(Configuration configuration, String jdbcUrl) {
        Matcher user = userPattern.matcher(jdbcUrl);
        if (user.find()) {
            configuration.setProperty("hibernate.connection.username", user.group(1));
        }

        Matcher pass = passPattern.matcher(jdbcUrl);
        if (pass.find()) {
            configuration.setProperty("hibernate.connection.password", pass.group(1));
        }
    }

    private static void setAdditionalProperties(Configuration configuration, Properties additional) {
        if (additional == null) {
            return;
        }

        for (String key : additional.stringPropertyNames()) {
            configuration.setProperty(key, additional.getProperty(key));
        }
    }

    private static void setDebug(Configuration configuration) {
        configuration.setProperty("hibernate.show_sql", System.getProperty("hibernate.show_sql", "false"));
    }
}
