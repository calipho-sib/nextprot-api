package org.nextprot.api.core.utils.dbxref;

import com.google.common.base.Preconditions;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nextprot.api.core.domain.DbXref;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Base class resolving DbXref linked URLs.
 *
 * <h4>Warning</h4>
 * Each implementations should be stateless or synchronized as they are reusable and potentially multithreadable.
 *
 * <h4>Note</h4>
 * Linked urls of each db may contain different single occurrence of any stamps (%s, %u, %n, ...) that should be resolved separately.
 */
class DbXrefURLBaseResolver {

    private static final Log LOGGER = LogFactory.getLog(DbXrefURLBaseResolver.class);

    private static final String UNRESOLVED_URL_REGEXP = "^.+%[a-zA-Z].*$";

    private final Map<String, StampBaseResolver> stampResolvers;

    public interface StampResolverFactory {

        Set<StampBaseResolver> createStampResolvers();
    }

    public DbXrefURLBaseResolver() {

        this(new DefaultStampResolverFactory());
    }

    public DbXrefURLBaseResolver(StampResolverFactory factory) {

        stampResolvers = new HashMap<>();

        for (StampBaseResolver resolver : factory.createStampResolvers()) {

            stampResolvers.put(resolver.getStamp(), resolver);
        }
    }

    public String resolve(DbXref xref) {

        Preconditions.checkNotNull(xref);

        String template = getTemplateURL(xref);

        xref.setLinkUrl(template);

        try {
            return resolveTemplateURL(template, getAccessionNumber(xref));
        } catch (Exception ex) {

            LOGGER.warn(xref.getAccession()+" - " + ex.getLocalizedMessage());
            return "None";
        }
    }

    private String resolveTemplateURL(String templateURL, String accession) {

        templateURL = templateURL.replaceAll("\"", "");

        for (Map.Entry<String, StampBaseResolver> entry : stampResolvers.entrySet()) {

            String stamp = entry.getKey();
            StampBaseResolver stampResolver = entry.getValue();

            if (templateURL.contains(stamp)) {

                templateURL = stampResolver.resolve(templateURL, accession);
            }
            else {

                throw new UnresolvedXrefURLException("stamp '"+stamp+"' is missing: could not resolve template URL '" + templateURL + "' with accession number '" + accession + "'");
            }
        }

        // GermOnline	http://www.germonline.org/%s/geneview?gene=%s
        // SOURCE	https://puma.princeton.edu/cgi-bin/source/sourceResult?criteria=%s&choice=Gene&option=Symbol&organism=%s
        // TODO: we should not have database link with multiple occurrence of %s that are either a stamp and a value !!!!
        // ChiTaRS db template: http://chitars.bioinfo.cnio.es/cgi-bin/search.pl?searchtype=gene_name&searchstr=%s&%s=1

        try {

            if (templateURL.matches(UNRESOLVED_URL_REGEXP)) {

                // the resolver should not throw an exception for URL-encoding character:
                //   ex: http://en.wikipedia.org/wiki/Thymosin_%CE%B11 -> http://en.wikipedia.org/wiki/Thymosin_α1
                // solution:
                //   decode URL-encoding character first as it match the following predicate
                try {
                    String decoded = URLDecoder.decode(templateURL, "UTF-8");

                    if (decoded.matches(UNRESOLVED_URL_REGEXP))
                        throw new UnresolvedXrefURLException("unresolved stamps: could not resolve template URL '" + templateURL + "' with accession number '" + accession + "'");
                }
                // TODO: URLDecoder gives me no choice of catching RuntimeException, a URL matcher would have been great here
                catch (IllegalArgumentException e) {

                    throw new UnresolvedXrefURLException("unresolved stamps: could not resolve template URL '" + templateURL + "' with accession number '" + accession + "'");
                }
            }
        } catch (UnsupportedEncodingException e) {

            throw new UnresolvedXrefURLException("unsupported URL encoding: could not resolve template URL '" + templateURL + "' with accession number '" + accession + "'");
        }

        return templateURL;
    }

    protected String getAccessionNumber(DbXref xref) {

        return xref.getAccession();
    }

    protected String getTemplateURL(DbXref xref) {

        String templateURL = xref.getLinkUrl();

        if (!templateURL.startsWith("http")) {
            templateURL = "http://" + templateURL;
        }

        return templateURL;
    }

    static class DefaultStampSResolver extends StampBaseResolver {

        public DefaultStampSResolver() {
            super("s");
        }

        protected String resolve(String templateURL, String accession) {
            return templateURL.replaceAll(getStamp(), accession);
        }
    }

    private static class DefaultStampResolverFactory implements StampResolverFactory {

        @Override
        public Set<StampBaseResolver> createStampResolvers() {

            Set<StampBaseResolver> stampResolvers = new HashSet<>(1);

            stampResolvers.add(new DefaultStampSResolver());

            return stampResolvers;
        }
    }
}
