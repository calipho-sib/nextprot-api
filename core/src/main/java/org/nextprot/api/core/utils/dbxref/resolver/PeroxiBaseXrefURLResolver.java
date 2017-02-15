package org.nextprot.api.core.utils.dbxref.resolver;

import org.nextprot.api.core.domain.DbXref;

class PeroxiBaseXrefURLResolver extends DefaultDbXrefURLResolver {

    @Override
    public String getTemplateURL(DbXref xref) {

        return "http://peroxibase.toulouse.inra.fr/browse/process/view_perox.php?id=%s";
    }
}