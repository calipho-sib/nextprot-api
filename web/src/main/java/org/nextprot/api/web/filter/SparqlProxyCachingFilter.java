package org.nextprot.api.web.filter;

import net.sf.ehcache.constructs.web.filter.SimplePageCachingFilter;

/**
 * Created by dteixeir on 28.04.17.
 */
public class SparqlProxyCachingFilter extends SimplePageCachingFilter {


    @Override
    protected String getCacheName() {
        return "sparql-proxy-cache";
    }


}
