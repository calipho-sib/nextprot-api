package org.nextprot.api.core.utils.dbxref.resolver;

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
    public String resolve(DbXref xref, String accession) {

        // the template url no longer need accession (no %u)
        return resolve(xref);
    }

    @Override
    public String getValidXrefURL(String xrefURL, String databaseName) {

        return "http://www.uniprot.org/unirule";
    }
}