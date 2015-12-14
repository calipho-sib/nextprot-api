package org.nextprot.api.core.utils.dbxref;

import org.nextprot.api.core.domain.DbXref;

class UnigeneXrefURLResolver extends DbXrefURLBaseResolver {

    @Override
    protected String getPrimaryId(DbXref xref) {

        return xref.getAccession().split("\\.")[1];
    }

    @Override
    protected String resolveTemplateURL(String templateURL, String primaryId) {

        if (templateURL.contains("%s1") && templateURL.contains("%s2")) {

            templateURL = templateURL.replaceFirst("%s1", "Hs");
            templateURL = templateURL.replaceFirst("%s2", primaryId);

            return templateURL;
        }

        throw new UnresolvedXrefURLException("could not resolve template URL '" + templateURL + "' with primary id '" + primaryId + "'");
    }
}