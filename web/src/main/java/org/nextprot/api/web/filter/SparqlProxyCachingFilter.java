package org.nextprot.api.web.filter;

import net.sf.ehcache.constructs.web.AlreadyCommittedException;
import net.sf.ehcache.constructs.web.PageInfo;
import net.sf.ehcache.constructs.web.filter.SimplePageCachingFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by dteixeir on 28.04.17.
 */
public class SparqlProxyCachingFilter extends SimplePageCachingFilter {


    @Override
    protected String getCacheName() {
        return "sparql-proxy-cache";
    }


    @Override
    protected void doFilter(final HttpServletRequest request,
                            final HttpServletResponse response, final FilterChain chain)
            throws Exception {

        if (response.isCommitted()) {
            throw new AlreadyCommittedException(
                    "Response already committed before doing buildPage.");
        }

        logRequestHeaders(request);
        PageInfo pageInfo = buildPageInfo(request, response, chain);

        if (response.isCommitted()) {
            throw new AlreadyCommittedException(
                    "Response already committed after doing buildPage"
                            + " but before writing response from PageInfo.");
        }
        writeResponse(request, response, pageInfo);
    }

    @Override
    protected String calculateKey(HttpServletRequest httpRequest) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(httpRequest.getMethod()).append(httpRequest.getRequestURI()).append(httpRequest.getQueryString());
        // TODO ADD POST DATA as part of the cache key
        String key = stringBuffer.toString();
        return key;
    }




}
