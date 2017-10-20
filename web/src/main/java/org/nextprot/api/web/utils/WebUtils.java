package org.nextprot.api.web.utils;

import org.apache.commons.io.IOUtils;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by dteixeir on 09.10.17.
 */
public class WebUtils {

    /**
     * Writes html content found in WEB-INF/html folder and sets response content type to text/html
     *
     * @param page The name of the file in WEB-INF/html folder
     * @param response The http resonse
     * @param sc The servlet context that should be injected
     * @throws IOException
     */
    public static void writeHtmlContent(String page, HttpServletResponse response, ServletContext sc) throws IOException {

        //This method was created for not having to override the default Media Type that is application/json to text/html

        byte[] body;
        body = IOUtils.toByteArray(sc.getResourceAsStream("/WEB-INF/html/" + page));
        response.setContentType("text/html");
        response.setContentLength(body.length);
        OutputStream out = new BufferedOutputStream(response.getOutputStream());
        out.write(body);
        out.flush();

    }

}
