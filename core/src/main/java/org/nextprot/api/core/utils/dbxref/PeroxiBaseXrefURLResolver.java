package org.nextprot.api.core.utils.dbxref;

import org.nextprot.api.core.domain.DbXref;

class PeroxiBaseXrefURLResolver extends DbXrefURLBaseResolver {

    @Override
    protected String getTemplateURL(DbXref xref) {

        return "http://peroxibase.toulouse.inra.fr/browse/process/view_perox.php?id=%s";
    }
}