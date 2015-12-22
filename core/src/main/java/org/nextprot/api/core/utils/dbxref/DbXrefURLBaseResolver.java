package org.nextprot.api.core.utils.dbxref;

import com.google.common.base.Preconditions;
import org.nextprot.api.core.domain.DbXref;

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

        return resolveTemplateURL(template, getAccessionNumber(xref));
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

        if (templateURL.contains("%")) {

            throw new UnresolvedXrefURLException("unresolved stamps: could not resolve template URL '" + templateURL + "' with accession number '" + accession + "'");
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
