package org.nextprot.api.web.controller;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;
import net.sf.ehcache.constructs.blocking.LockTimeoutException;
import net.sf.ehcache.constructs.web.*;
import org.apache.commons.io.IOUtils;
import org.nextprot.api.web.utils.WebUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.ServletWrappingController;

import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;

/**
 * This controller is initalized in the XML.
 */
public class SparqlProxyController extends ServletWrappingController implements InitializingBean{
	
    @Value("${sparql.url}")
    private String sparqlEndpoint;

	@Autowired(required = false)
	private EhCacheCacheManager ehCacheManager;

	@Autowired
	private ServletContext servletContext;

	@Override
	public void afterPropertiesSet() throws Exception{
		this.setServletClass(org.mitre.dsmiley.httpproxy.ProxyServlet.class);
		this.setServletName("sparql-proxy");
		
		Properties props = new Properties();
		props.setProperty("targetUri", sparqlEndpoint);
		this.setInitParameters(props);
		
		super.afterPropertiesSet();
	}


	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {

		PageInfo pageInfo = buildPageInfo(request, response);
		if(pageInfo == null){ // If it is the sparql welcome page
			WebUtils.writeHtmlContent("welcome-sparql-page.html", response, servletContext);
		}else {

			// For dereferencing, check if the query string contains an "entity" parameter
			String queryString = request.getQueryString();
			if(queryString.contains("entity")) {
				String entityURI = queryString.substring(queryString.indexOf("="));
				String entity = entityURI.split("/")[1];
				String accession = entityURI.split("/")[2];
			}

			boolean requestAcceptsGzipEncoding = acceptsGzipEncoding(request);

			setStatus(response, pageInfo);
			setContentType(response, pageInfo);
			setCookies(pageInfo, response);
			// do headers last so that users can override with their own header sets
			setHeaders(pageInfo, requestAcceptsGzipEncoding, response);
			writeContent(request, response, pageInfo);

		}


		return null;


	}


	protected void writeContent(final HttpServletRequest request,
								final HttpServletResponse response, final PageInfo pageInfo)
			throws IOException, ResponseHeadersNotModifiableException {
		byte[] body;

		boolean shouldBodyBeZero = ResponseUtil.shouldBodyBeZero(request,
				pageInfo.getStatusCode());
		if (shouldBodyBeZero) {
			body = new byte[0];
		} else if (acceptsGzipEncoding(request)) {
			body = pageInfo.getGzippedBody();
			if (ResponseUtil.shouldGzippedBodyBeZero(body, request)) {
				body = new byte[0];
			} else {
				ResponseUtil.addGzipHeader(response);
			}

		} else {
			body = pageInfo.getUngzippedBody();
		}

		response.setContentLength(body.length);
		OutputStream out = new BufferedOutputStream(response.getOutputStream());
		out.write(body);
		out.flush();
	}



	/**
	 * Set the content type.
	 *
	 * @param response
	 * @param pageInfo
	 */
	protected void setContentType(final HttpServletResponse response,
								  final PageInfo pageInfo) {
		String contentType = pageInfo.getContentType();
		if (contentType != null && contentType.length() > 0) {
			response.setContentType(contentType);
		}
	}

	/**
	 * Set the serializableCookies
	 *
	 * @param pageInfo
	 * @param response
	 */
	protected void setCookies(final PageInfo pageInfo,
							  final HttpServletResponse response) {

		final Collection cookies = pageInfo.getSerializableCookies();
		for (Iterator iterator = cookies.iterator(); iterator.hasNext();) {
			final Cookie cookie = ((SerializableCookie) iterator.next())
					.toCookie();
			response.addCookie(cookie);
		}
	}

	/**
	 * Status code
	 *
	 * @param response
	 * @param pageInfo
	 */
	protected void setStatus(final HttpServletResponse response,
							 final PageInfo pageInfo) {
		response.setStatus(pageInfo.getStatusCode());
	}

	/**
	 * Set the headers in the response object, excluding the Gzip header
	 *
	 * @param pageInfo
	 * @param requestAcceptsGzipEncoding
	 * @param response
	 */
	protected void setHeaders(final PageInfo pageInfo,
							  boolean requestAcceptsGzipEncoding,
							  final HttpServletResponse response) {

		final Collection<Header<? extends Serializable>> headers = pageInfo
				.getHeaders();

		// Track which headers have been set so all headers of the same name
		// after the first are added
		final TreeSet<String> setHeaders = new TreeSet<String>(
				String.CASE_INSENSITIVE_ORDER);

		for (final Header<? extends Serializable> header : headers) {
			final String name = header.getName();

			switch (header.getType()) {
				case STRING:
					if (setHeaders.contains(name)) {
						response.addHeader(name, (String) header.getValue());
					} else {
						setHeaders.add(name);
						response.setHeader(name, (String) header.getValue());
					}
					break;
				case DATE:
					if (setHeaders.contains(name)) {
						response.addDateHeader(name, (Long) header.getValue());
					} else {
						setHeaders.add(name);
						response.setDateHeader(name, (Long) header.getValue());
					}
					break;
				case INT:
					if (setHeaders.contains(name)) {
						response.addIntHeader(name, (Integer) header.getValue());
					} else {
						setHeaders.add(name);
						response.setIntHeader(name, (Integer) header.getValue());
					}
					break;
				default:
					throw new IllegalArgumentException("No mapping for Header: "
							+ header);
			}
		}
	}


	private boolean acceptsGzipEncoding(HttpServletRequest request) {
		return acceptsEncoding(request, "gzip");
	}

	protected boolean acceptsEncoding(final HttpServletRequest request, final String name) {
		final boolean accepts = headerContains(request, "Accept-Encoding", name);
		return accepts;
	}

	private boolean headerContains(final HttpServletRequest request, final String header, final String value) {

		final Enumeration accepted = request.getHeaders(header);
		while (accepted.hasMoreElements()) {
			final String headerValue = (String) accepted.nextElement();
			if (headerValue.indexOf(value) != -1) {
				return true;
			}
		}
		return false;
	}


	protected PageInfo buildPageInfo(final HttpServletRequest request, final HttpServletResponse response) throws Exception {

		if(request.getMethod().equals("POST")){
			return buildPage(request, response);
		}
		else if(request.getMethod().equals("GET")){

			//Welcome SPARQL page
			if(checkIsSparqlWelcomePage(request))
				return null;

			// Look up the cached page
			final String key = calculateKey(request);

			PageInfo pageInfo;
			try {
				//TODO checkNoReentry(request);

				Cache blockingCache;
				if(ehCacheManager != null){
					blockingCache = ehCacheManager.getCacheManager().getCache("sparql-proxy-cache");
				}else {
					return buildPage(request, response);
				}

				Element element = blockingCache.get(key);
				if (element == null || element.getObjectValue() == null) {
					try {
						// Page is not cached - build the response, cache it, and
						// send to client
						pageInfo = buildPage(request, response);
						if (pageInfo.isOk()) {
							blockingCache.put(new Element(key, pageInfo));
						} else {
							blockingCache.put(new Element(key, null));
						}
					} catch (final Throwable throwable) {
						// Must unlock the cache if the above fails. Will be logged
						// at Filter
						blockingCache.put(new Element(key, null));
						throw new Exception(throwable);
					}
				} else {
					pageInfo = (PageInfo) element.getObjectValue();
				}
			} catch (LockTimeoutException e) {
				// do not release the lock, because you never acquired it
				throw e;
			} finally {
				// all done building page, reset the re-entrant flag
				//TODO visitLog.clear();
			}
			return pageInfo;

		}else {
			response.setStatus(400);
			response.setHeader("Content-Type", "text/plain");
			response.getWriter().write(request.getMethod() + " HTTP method not supported");
			return null;
		}
	}

	private static boolean checkIsSparqlWelcomePage(HttpServletRequest httpRequest) {
		return ((httpRequest.getMethod() == null || httpRequest.getMethod().equals("GET"))
			&& (httpRequest.getQueryString() == null) || (httpRequest.getQueryString().isEmpty()));
	}

	public static String calculateKey(HttpServletRequest httpRequest) {
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append(httpRequest.getMethod()).append(httpRequest.getRequestURI()).append(httpRequest.getQueryString());
		return stringBuffer.toString();
	}


	private PageInfo buildPage(final HttpServletRequest request, final HttpServletResponse response) throws Exception {

		// Invoke the next entity in the chain
		final ByteArrayOutputStream outstr = new ByteArrayOutputStream();
		final GenericResponseWrapper wrapper = new GenericResponseWrapper(response, outstr);

		//TODO THE ACTUAL CALL IS MADE HERE!!!!!!!!!!
		super.handleRequestInternal(request, wrapper);

		wrapper.flush();

		// Return the page info
		return new PageInfo(wrapper.getStatus(), wrapper.getContentType(),
				wrapper.getCookies(), outstr.toByteArray(), true,
				10000, wrapper.getAllHeaders());
	}

	// Generates the respective Sparql query
	private String generateQuery(String entity, String accession) {
		return "DESCRIBE <http://nextprot.org/rdf/" + entity + "/" + accession;
	}

}
