package org.nextprot.api.core.service.dbxref.resolver;

import org.nextprot.api.core.domain.CvDatabasePreferredLink;
import org.nextprot.api.core.domain.DbXref;

class EcoXrefURLResolver extends DefaultDbXrefURLResolver {

    @Override
    public String getTemplateURL(DbXref xref) {

        return CvDatabasePreferredLink.ECO.getLink();
    }

    @Override
    protected String getAccessionNumber(DbXref xref) {
        return xref.getAccession().replace(':', '_');
    }

    @Override
    public String getValidXrefURL(String xrefURL, String databaseName) {
        return "http://www.ontobee.org/ontology/ECO";
    }
}