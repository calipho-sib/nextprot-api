package org.nextprot.api.core.utils.dbxref;

abstract class PlaceHoldersXrefURLResolver extends DbXrefURLBaseResolver {

    @Override
    protected String resolveTemplateURL(String templateURL, String primaryId) {

        if (templateURL.contains("%s1") && templateURL.contains("%s2")) {

            templateURL = templateURL.replaceFirst("%s1", getFirstPlaceHolderValue(primaryId));
            templateURL = templateURL.replaceFirst("%s2", getSecondPlaceHolderValue(primaryId));

            return templateURL;
        }

        throw new UnresolvedXrefURLException("could not resolve template URL '" + templateURL + "' with primary id '" + primaryId + "'");
    }

    protected abstract String getFirstPlaceHolderValue(String primaryId);
    protected abstract String getSecondPlaceHolderValue(String primaryId);
}