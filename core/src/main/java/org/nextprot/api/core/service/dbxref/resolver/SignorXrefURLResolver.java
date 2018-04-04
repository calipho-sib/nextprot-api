package org.nextprot.api.core.service.dbxref.resolver;

import org.nextprot.api.core.domain.CvDatabasePreferredLink;
import org.nextprot.api.core.domain.DbXref;

class SignorXrefURLResolver extends DefaultDbXrefURLResolver {

    private final CvDatabasePreferredLink link;

    SignorXrefURLResolver() {

        this.link = CvDatabasePreferredLink.SIGNOR;
    }

    @Override
    public String getTemplateURL(DbXref xref) {

        return link.getLink();
    }
}