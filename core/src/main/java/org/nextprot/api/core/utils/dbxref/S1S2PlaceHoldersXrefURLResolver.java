package org.nextprot.api.core.utils.dbxref;

abstract class S1S2PlaceHoldersXrefURLResolver extends DbXrefURLBaseResolver {

    @Override
    protected String resolveTemplateURL(String templateURL, String primaryId) {

        if (templateURL.contains("%s1") && templateURL.contains("%s2")) {

            templateURL = templateURL.replaceFirst("%s1", getS1PlaceHolderValue(primaryId));
            templateURL = templateURL.replaceFirst("%s2", getS2PlaceHolderValue(primaryId));

            return templateURL;
        }

        throw new UnresolvedXrefURLException("could not resolve template URL '" + templateURL + "' with primary id '" + primaryId + "'");
    }

    protected abstract String getS1PlaceHolderValue(String primaryId);
    protected abstract String getS2PlaceHolderValue(String primaryId);
}