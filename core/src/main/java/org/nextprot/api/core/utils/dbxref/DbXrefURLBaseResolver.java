package org.nextprot.api.core.utils.dbxref;

import com.google.common.base.Preconditions;
import org.nextprot.api.core.domain.DbXref;

/**
 * Base class resolving DbXref linked URLs.
 *
 * <h4>Warning</h4>
 * Each implementations should be stateless or synchronized as they are reusable and potentially multithreadable.
 *
 * TODO: Refactor this class:
 *
 * Linked urls of each db may contain different single occurrence of any stamps (%s, %u, %n, ...) that should be resolved separately.
 *
 *
 */
class DbXrefURLBaseResolver {

    public String resolve(DbXref xref) {

        Preconditions.checkNotNull(xref);

        String template = getTemplateURL(xref);

        xref.setLinkUrl(template);

        return resolveTemplateURL(template, getAccessionNumber(xref));
    }

    protected String resolveTemplateURL(String templateURL, String accession) {

        templateURL = templateURL.replaceAll("\"", "");

        if (templateURL.contains("%s")) {
            return resolveStampS(templateURL, accession);
        }

        throw new UnresolvedXrefURLException("placeholder '%s' is missing: could not resolve template URL '" + templateURL + "' with accession number '" + accession + "'");
    }

    protected String resolveStampS(String templateURL, String accession) {

        return templateURL.replaceFirst("%s", accession);
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
}
