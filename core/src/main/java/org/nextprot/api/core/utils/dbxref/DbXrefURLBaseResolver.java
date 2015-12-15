package org.nextprot.api.core.utils.dbxref;

import com.google.common.base.Preconditions;
import org.apache.commons.lang.StringUtils;
import org.nextprot.api.core.domain.CvDatabasePreferredLink;
import org.nextprot.api.core.domain.DbXref;
import org.nextprot.api.core.domain.XRefDatabase;

/**
 * Base class resolving DbXref linked URLs.
 *
 * <h4>Warning</h4>
 * Each implementations should be stateless or synchronized as they are reusable and potentially multithreaded.
 */
abstract class DbXrefURLBaseResolver {

    public String resolve(DbXref xref) {

        Preconditions.checkNotNull(xref);

        if (StringUtils.isEmpty(xref.getLinkUrl()) && !CvDatabasePreferredLink.isDbHasPreferredLink(getXRefDatabase(xref))) {
            return "";
        }

        return resolveTemplateURL(getTemplateUrl(xref), getPrimaryId(xref));
    }

    protected String resolveTemplateURL(String templateURL, String primaryId) {

        if (templateURL.matches(".*%s\\b.*")) {

            return templateURL.replaceAll("\"", "").replaceAll("%s", primaryId);
        }

        throw new UnresolvedXrefURLException("placeholder '%s' is missing: could not resolve template URL '" + templateURL + "' with primary id '" + primaryId + "'");
    }

    protected String getPrimaryId(DbXref xref) {

        return xref.getAccession();
    }

    protected String getTemplateUrl(DbXref xref) {

        String templateURL = xref.getLinkUrl();

        if (!templateURL.startsWith("http")) {
            templateURL = "http://" + templateURL;
        }

        return templateURL;
    }

    protected XRefDatabase getXRefDatabase(DbXref xref) {
        return XRefDatabase.valueOfDbName(xref.getDatabaseName());
    }
}
