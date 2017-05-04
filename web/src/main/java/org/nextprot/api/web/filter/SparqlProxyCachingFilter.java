package org.nextprot.api.web.filter;

import net.sf.ehcache.constructs.web.AlreadyCommittedException;
import net.sf.ehcache.constructs.web.PageInfo;
import net.sf.ehcache.constructs.web.filter.SimplePageCachingFilter;
import org.apache.commons.io.IOUtils;
import org.nextprot.api.web.domain.MultiReadHttpServletRequest;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

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

        MultiReadHttpServletRequest multiReadRequest = new MultiReadHttpServletRequest(request);

        if (response.isCommitted()) { throw new AlreadyCommittedException("Response already committed before doing buildPage.");}
        logRequestHeaders(request);

        PageInfo pageInfo = buildPageInfo(multiReadRequest, response, chain);
        if (response.isCommitted()) {
            throw new AlreadyCommittedException(
                    "Response already committed after doing buildPage"
                            + " but before writing response from PageInfo.");
        }
        writeResponse(multiReadRequest, response, pageInfo);
    }

    @Override
    protected String calculateKey(HttpServletRequest httpRequest) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(httpRequest.getMethod()).append(httpRequest.getRequestURI()).append(httpRequest.getQueryString());
        try {
            stringBuffer.append(IOUtils.toString(httpRequest.getReader()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        String key = stringBuffer.toString();
        return key;
    }

}
