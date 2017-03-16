package org.nextprot.api.core.utils.dbxref.resolver;

import org.nextprot.api.core.domain.CvDatabasePreferredLink;
import org.nextprot.api.core.domain.DbXref;

class ConstantLinkXrefURLResolver extends DefaultDbXrefURLResolver {

    private final CvDatabasePreferredLink link;

    ConstantLinkXrefURLResolver(CvDatabasePreferredLink link) {

        this.link = link;
    }

    @Override
    public String getTemplateURL(DbXref xref) {

        return link.getLink();
    }
}