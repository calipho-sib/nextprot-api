package org.nextprot.api.core.utils.dbxref;

class TkgXrefURLResolver extends DbXrefURLBaseResolver {

    @Override
    protected String resolveTemplateURL(String templateURL, String primaryId) {

        //	Db_URL: http://www2.idac.tohoku.ac.jp/dep/ccr/TKGdate/TKGvo10%n/%s.html
        //	  Note: n% is the second digit of the cell line AC and %s is the cell line AC without the 'TKG'
        //	  Example: for "DR   TKG; TKG 0377": n%=3 s%=0377
        if (templateURL.contains("%n")) {

            return super.resolveTemplateURL(templateURL.replaceAll("%n", String.valueOf(primaryId.charAt(1))), primaryId);
        }

        throw new UnresolvedXrefURLException("placeholder '%n' is missing: could not resolve template URL '" + templateURL + "' with primary id '" + primaryId + "'");
    }
}