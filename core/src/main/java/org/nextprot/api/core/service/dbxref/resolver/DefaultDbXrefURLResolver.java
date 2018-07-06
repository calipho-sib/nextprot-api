package org.nextprot.api.core.service.dbxref.resolver;

import com.google.common.base.Preconditions;
import org.nextprot.api.core.domain.CvDatabasePreferredLink;
import org.nextprot.api.core.domain.DbXref;
import org.nextprot.api.core.service.dbxref.DbXrefURLResolver;

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

    private final Map<String, Placeholder> placeholders;

    public interface PlaceholderFactory {

        Set<Placeholder> createPlaceholders();
    }

    public DefaultDbXrefURLResolver() {

        this(new DefaultPlaceholderFactory());
    }

    public DefaultDbXrefURLResolver(PlaceholderFactory factory) {

        placeholders = new HashMap<>();

        for (Placeholder placeholder : factory.createPlaceholders()) {

            placeholders.put(placeholder.getPlaceholderText(), placeholder);
        }
    }

    @Override
    public String resolve(DbXref xref) {

        Preconditions.checkNotNull(xref);

        String template = getTemplateURL(xref);

        xref.setLinkUrl(template);

        // resolve link with %u uniprot placeholder
        if (xref.getLinkUrl() != null && xref.getLinkUrl().contains("%u")) {

            Optional<DbXrefURLResolverSupplier> optionalSupplier = DbXrefURLResolverSupplier.fromDbName(xref.getDatabaseName());

            String templateURL = optionalSupplier.isPresent() ? optionalSupplier.get().getResolver().getTemplateURL(xref) : xref.getLinkUrl();

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

            String proteinAccession = xref.getProteinAccessionReferer();

            // replace %u by uniprot accession
            String resolved = templateURL.replaceFirst("%u", proteinAccession.startsWith("NX_") ? proteinAccession.substring(3) : proteinAccession);

            return resolved.replaceFirst("%s", xref.getAccession());
        }

        return resolveURL(template, getAccessionNumber(xref));
    }

    /**
     * Resolve url from template by replacing placeholders with accession value
     * @param templateURL the url to resolve
     * @param accession the accession to replace placeholder
     * @return a resolved url string
     */
    protected String resolveURL(String templateURL, String accession) {

        String resolvedUrl = templateURL.replaceAll("\"", "");

        for (Placeholder placeholder : placeholders.values()) {

            if (resolvedUrl.contains(placeholder.getPlaceholderText())) {
                resolvedUrl = placeholder.replacePlaceholderWithAccession(resolvedUrl, accession);
            }
            else {
                throw new UnresolvedXrefURLException("placeholder '"+placeholder.getPlaceholderText()+"' is missing: could not resolve template URL '" + resolvedUrl + "' with accession number '" + accession + "'");
            }
        }

        // GermOnline	http://www.germonline.org/%s/geneview?gene=%s
        // SOURCE	https://puma.princeton.edu/cgi-bin/source/sourceResult?criteria=%s&choice=Gene&option=Symbol&organism=%s
        // TODO: we should not have database link with multiple occurrence of %s that are either a stamp and a value !!!!
        // ChiTaRS db template: http://chitars.bioinfo.cnio.es/cgi-bin/search.pl?searchtype=gene_name&searchstr=%s&%s=1

        if (resolvedUrl.matches(UNRESOLVED_URL_REGEXP)) {
            handleNonResolvedUrl(resolvedUrl, accession);
        }

        return resolvedUrl;
    }

    private void handleNonResolvedUrl(String unresolvedUrl, String accession) {

        // the resolver should not throw an exception for URL-encoding character:
        //   ex: http://en.wikipedia.org/wiki/Thymosin_%CE%B11 -> http://en.wikipedia.org/wiki/Thymosin_α1
        // solution:
        //   decode URL-encoding character first as it match the following predicate
        try {
            String decoded = URLDecoder.decode(unresolvedUrl, "UTF-8");

            if (decoded.matches(UNRESOLVED_URL_REGEXP))
                throw new UnresolvedXrefURLException("unresolved stamps: could not resolve template URL '" + unresolvedUrl + "' with accession number '" + accession + "'");
        }
        // TODO: URLDecoder gives me no choice of catching RuntimeException, a URL matcher would have been great here
        catch (IllegalArgumentException e) {

            throw new UnresolvedXrefURLException("unresolved placeholder: could not resolve template URL '" + unresolvedUrl + "' with accession number '" + accession + "'", e);
        }
        catch (UnsupportedEncodingException e) {

            throw new UnresolvedXrefURLException("unsupported URL encoding: could not resolve template URL '" + unresolvedUrl + "' with accession number '" + accession + "'", e);
        }
    }

    protected String getAccessionNumber(DbXref xref) {

        return xref.getAccession();
    }

    @Override
    public String getTemplateURL(DbXref xref) {

        return getHttpPrefixURL(xref.getLinkUrl());
    }

    @Override
    public String getValidXrefURL(String xrefURL, String databaseName) {

        return getHttpPrefixURL(xrefURL);
    }

    private String getHttpPrefixURL(String url) {

        if (!url.startsWith("http")) {
            return "http://" + url;
        }

        return url;
    }

    public static class DefaultPlaceholderS extends Placeholder {

        public DefaultPlaceholderS() {
            super("s");
        }

        @Override
        public String replacePlaceholderWithAccession(String templateURL, String accession) {
            return templateURL.replaceAll(getPlaceholderText(), accession);
        }
    }

    private static class DefaultPlaceholderFactory implements PlaceholderFactory {

        @Override
        public Set<Placeholder> createPlaceholders() {

            Set<Placeholder> stampResolvers = new HashSet<>(1);

            stampResolvers.add(new DefaultPlaceholderS());

            return stampResolvers;
        }
    }
}
