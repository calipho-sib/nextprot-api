package org.nextprot.api.core.utils.dbxref.resolver;

import com.google.common.base.Preconditions;
import org.nextprot.api.core.domain.CvDatabasePreferredLink;
import org.nextprot.api.core.domain.DbXref;
import org.nextprot.api.core.utils.dbxref.DbXrefURLResolver;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;

/**
 * Base class resolving DbXref linked URLs.
 *
 * <h4>Warning</h4>
 * Each implementations should be stateless or synchronized as they are reusable and potentially multithreadable.
 *
 * <h4>Note</h4>
 * Linked urls of each db may contain different single occurrence of any stamps (%s, %u, %n, ...) that should be resolved separately.
 */
class DefaultDbXrefURLResolver implements DbXrefURLResolver {

    private static final String UNRESOLVED_URL_REGEXP = "^.+%[a-zA-Z].*$";

    private final Map<String, StampBaseResolver> stampResolvers;

    public interface StampResolverFactory {

        Set<StampBaseResolver> createStampResolvers();
    }

    public DefaultDbXrefURLResolver() {

        this(new DefaultStampResolverFactory());
    }

    public DefaultDbXrefURLResolver(StampResolverFactory factory) {

        stampResolvers = new HashMap<>();

        for (StampBaseResolver resolver : factory.createStampResolvers()) {

            stampResolvers.put(resolver.getStamp(), resolver);
        }
    }

    @Override
    public String resolve(DbXref xref) {

        Preconditions.checkNotNull(xref);

        String template = getTemplateURL(xref);

        xref.setLinkUrl(template);

        return resolveTemplateURL(template, getAccessionNumber(xref));
    }

    // TODO: this implementation is ugly and should be refactored
    @Override
    public String resolveWithAccession(DbXref xref, String accession) {

        if (xref != null && xref.getLinkUrl() != null && xref.getLinkUrl().contains("%u")) {

            Optional<XRefDatabase> db = XRefDatabase.valueOfName(xref.getDatabaseName());

            String templateURL = db.isPresent() ? db.get().getResolver().getTemplateURL(xref) : xref.getLinkUrl();

            if (!templateURL.startsWith("http")) {

                templateURL = "http://" + templateURL;
            }

            if ("brenda".equalsIgnoreCase(xref.getDatabaseName())) {

                if (xref.getAccession().startsWith("BTO")) {
                    templateURL = CvDatabasePreferredLink.BRENDA_BTO.getLink().replace("%s", xref.getAccession().replace(":", "_"));
                } else {
                    templateURL = templateURL.replaceFirst("%s1", xref.getAccession());

                    // organism always human: hardcoded as "247"
                    templateURL = templateURL.replaceFirst("%s2", "247");
                }
            }

            String resolved = templateURL.replaceFirst("%u", accession.startsWith("NX_") ? accession.substring(3) : accession);

            return resolved.replaceFirst("%s", xref.getAccession());
        }

        return resolve(xref);
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

        if (templateURL.matches(UNRESOLVED_URL_REGEXP)) {

            handleNonResolvedUrl(templateURL, accession);
        }

        return templateURL;
    }

    private void handleNonResolvedUrl(String templateURL, String accession) {

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

            throw new UnresolvedXrefURLException("unresolved stamps: could not resolve template URL '" + templateURL + "' with accession number '" + accession + "'", e);
        }
        catch (UnsupportedEncodingException e) {

            throw new UnresolvedXrefURLException("unsupported URL encoding: could not resolve template URL '" + templateURL + "' with accession number '" + accession + "'", e);
        }
    }

    protected String getAccessionNumber(DbXref xref) {

        return xref.getAccession();
    }

    @Override
    public String getTemplateURL(DbXref xref) {

        String templateURL = xref.getLinkUrl();

        if (!templateURL.startsWith("http")) {
            templateURL = "http://" + templateURL;
        }

        return templateURL;
    }

    public static class DefaultStampSResolver extends StampBaseResolver {

        public DefaultStampSResolver() {
            super("s");
        }

        @Override
        public String resolve(String templateURL, String accession) {
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
