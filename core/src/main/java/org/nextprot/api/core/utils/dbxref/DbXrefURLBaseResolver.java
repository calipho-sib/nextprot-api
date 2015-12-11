package org.nextprot.api.core.utils.dbxref;

import com.google.common.base.Preconditions;
import org.apache.commons.lang.StringUtils;
import org.nextprot.api.core.domain.CvDatabasePreferredLink;
import org.nextprot.api.core.domain.DbXref;
import org.nextprot.api.core.domain.XRefDatabase;

/**
 * Base class that resolve DbXref URLs
 */
public abstract class DbXrefURLBaseResolver {

    public String resolve(DbXref xref) {

        Preconditions.checkNotNull(xref);

        if (StringUtils.isEmpty(getTemplateUrl(xref)) && !CvDatabasePreferredLink.isDbHasPreferredLink(getXRefDatabase(xref))) {
            return "";
        }

        return resolveTemplateURL(getTemplateUrl(xref), getPrimaryId(xref));
    }

    protected String resolveTemplateURL(String templateURL, String primaryId) {

        // general case
        if (templateURL.matches(".*%s\\b.*")) {
            return templateURL.replaceAll("\"", "").replaceAll("%s", primaryId);
        }

        throw new UnresolvedXrefURLException("could not resolve template URL '" + templateURL + "' with primary id '" + primaryId + "'");
    }

    protected String getPrimaryId(DbXref xref) {
        return xref.getAccession();
    }

    protected abstract String getTemplateUrl(DbXref xref);

    protected XRefDatabase getXRefDatabase(DbXref xref) {
        return XRefDatabase.valueOfDbName(xref.getDatabaseName());
    }
}
