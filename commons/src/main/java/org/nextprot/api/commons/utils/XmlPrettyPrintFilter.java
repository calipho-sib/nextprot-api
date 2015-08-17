package org.nextprot.api.commons.utils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class XmlPrettyPrintFilter implements Filter {

    protected FilterConfig config;
    private XMLPrettyPrinter XMLPrettyPrinter;

    public void init(FilterConfig config) throws ServletException {
        this.config = config;
        try {
            XMLPrettyPrinter = new XMLPrettyPrinter();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
            throw new ServletException(e);
        }
    }

    public void destroy() {
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        ServletResponse newResponse = response;

        if (request instanceof HttpServletRequest) {
            newResponse = new CharResponseWrapper((HttpServletResponse) response);
        }

        chain.doFilter(request, newResponse);

        if (newResponse instanceof CharResponseWrapper) {
            String text = newResponse.toString();
            if (text != null) {
                try {
                    text = XMLPrettyPrinter.prettify(text);
                } catch (TransformerException e) {
                    e.printStackTrace();
                    throw new IOException(e);
                }
                response.getWriter().write(text);
            }
        }
    }
}

class CharResponseWrapper extends HttpServletResponseWrapper {
    protected CharArrayWriter charWriter;

    protected PrintWriter writer;

    protected boolean getOutputStreamCalled;

    protected boolean getWriterCalled;

    public CharResponseWrapper(HttpServletResponse response) {
        super(response);

        charWriter = new CharArrayWriter();
    }

    public ServletOutputStream getOutputStream() throws IOException {
        if (getWriterCalled) {
            throw new IllegalStateException("getWriter already called");
        }

        getOutputStreamCalled = true;
        return super.getOutputStream();
    }

    public PrintWriter getWriter() throws IOException {
        if (writer != null) {
            return writer;
        }
        if (getOutputStreamCalled) {
            throw new IllegalStateException("getOutputStream already called");
        }
        getWriterCalled = true;
        writer = new PrintWriter(charWriter);
        return writer;
    }

    public String toString() {
        String s = null;

        if (writer != null) {
            s = charWriter.toString();
        }
        return s;
    }

}
