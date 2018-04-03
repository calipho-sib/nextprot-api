package org.nextprot.api.core.service.dbxref.resolver;

import org.nextprot.api.core.domain.CvDatabasePreferredLink;
import org.nextprot.api.core.domain.DbXref;

class RuleBaseXrefURLResolver extends DefaultDbXrefURLResolver {

    private final CvDatabasePreferredLink link;

    RuleBaseXrefURLResolver() {

        this.link = CvDatabasePreferredLink.RULEBASE;
    }

    @Override
    public String getTemplateURL(DbXref xref) {

        return link.getLink();
    }

    @Override
    public String getValidXrefURL(String xrefURL, String databaseName) {

        return "http://www.uniprot.org/unirule";
    }
}