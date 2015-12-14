package org.nextprot.api.core.utils.dbxref;

class UcscXrefURLResolver extends DbXrefURLBaseResolver {

    @Override
    protected String resolveTemplateURL(String templateURL, String primaryId) {

        if (templateURL.contains("%s1") && templateURL.contains("%s2")) {

            templateURL = templateURL.replaceFirst("%s1", primaryId);
            templateURL = templateURL.replaceFirst("%s2", "human");

            return templateURL;
        }

        throw new UnresolvedXrefURLException("could not resolve template URL '" + templateURL + "' with primary id '" + primaryId + "'");
    }
}