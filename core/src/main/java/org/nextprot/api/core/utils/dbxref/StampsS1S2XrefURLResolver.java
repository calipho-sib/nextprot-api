package org.nextprot.api.core.utils.dbxref;

abstract class StampsS1S2XrefURLResolver extends DbXrefURLBaseResolver {

    @Override
    protected String resolveTemplateURL(String templateURL, String primaryId) {

        if (templateURL.contains("%s1") && templateURL.contains("%s2")) {

            templateURL = templateURL.replaceFirst("%s1", getS1StampValue(primaryId));
            templateURL = templateURL.replaceFirst("%s2", getS2StampValue(primaryId));

            return templateURL;
        }

        throw new UnresolvedXrefURLException("could not resolve template URL '" + templateURL + "' with primary id '" + primaryId + "'");
    }

    protected abstract String getS1StampValue(String primaryId);
    protected abstract String getS2StampValue(String primaryId);
}