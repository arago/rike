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
package de.arago.rike.leaderboard;

import de.arago.rike.commons.data.GlobalConfig;
import de.arago.rike.svg.SVGDataServlet;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

/**
 * this servlet serves svg graphs and status for mars nodes
 */
public class LeaderBoardImagesServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("image/png");

        try {
            SVGDataServlet.addExpires(response);

            int pos = request.getRequestURL().lastIndexOf("/");
            String name = request.getRequestURL().substring(pos+1);
            File tmp = new File(System.getProperty("java.io.tmpdir"),name);
            if(!tmp.exists()) {
                String uri = GlobalConfig.get(GlobalConfig.PATH_TO_PERSONAL_PICS);
                URL resource = (new URI(uri+name)).toURL();
                FileUtils.copyURLToFile(resource, tmp);
            }
            FileUtils.copyFile(tmp, response.getOutputStream());
        } catch (Exception ex) {
            IOUtils.copy(LeaderBoardImagesServlet.class.getResourceAsStream("/unknown.png"), response.getOutputStream());
        }
    }
}
