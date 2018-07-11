package org.nextprot.api.core.service.dbxref.resolver;

import org.nextprot.api.core.domain.CvDatabasePreferredLink;
import org.nextprot.api.core.domain.DbXref;

class PsiModXrefURLResolver extends DefaultDbXrefURLResolver {

    @Override
    protected String getAccessionNumber(DbXref xref) {

        String accession = xref.getAccession();

        if (accession.startsWith("MOD:")) {

            return xref.getAccession().substring(4);
        }

        return accession;
    }

    @Override
    public String getTemplateURL(DbXref xref) {

        return CvDatabasePreferredLink.PSIMOD.getLink();
    }
}