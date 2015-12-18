package org.nextprot.api.core.utils.dbxref;

import org.nextprot.api.core.domain.CvDatabasePreferredLink;
import org.nextprot.api.core.domain.DbXref;

class ConstantLinkXrefURLResolver extends DbXrefURLBaseResolver {

    private final CvDatabasePreferredLink link;

    ConstantLinkXrefURLResolver(CvDatabasePreferredLink link) {

        this.link = link;
    }

    @Override
    protected String getTemplateURL(DbXref xref) {

        return link.getLink();
    }
}