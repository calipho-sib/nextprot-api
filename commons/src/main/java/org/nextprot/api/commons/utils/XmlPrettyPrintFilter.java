package org.nextprot.api.commons.utils;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

public class XmlPrettyPrintFilter implements Filter{


	protected FilterConfig config;

	  public void init(FilterConfig config) throws ServletException {
	    this.config = config;
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
	        text = PrettyPrinter.getPrettyXml(text);
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
