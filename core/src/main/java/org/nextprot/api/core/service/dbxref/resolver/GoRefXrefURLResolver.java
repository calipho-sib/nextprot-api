package org.nextprot.api.core.service.dbxref.resolver;

import org.nextprot.api.core.domain.CvDatabasePreferredLink;
import org.nextprot.api.core.domain.DbXref;

class GoRefXrefURLResolver extends DefaultDbXrefURLResolver {

    @Override
    public String getTemplateURL(DbXref xref) {

        return CvDatabasePreferredLink.GO_REF.getLink();
    }

    @Override
    protected String getAccessionNumber(DbXref xref) {
        return xref.getAccession().substring(7);
    }

    @Override
    public String getValidXrefURL(String xrefURL, String databaseName) {
        return "https://github.com/geneontology/go-site/blob/master/metadata/gorefs/README.md";
    }
}