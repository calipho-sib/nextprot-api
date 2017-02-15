package org.nextprot.api.core.utils.dbxref.resolver;

import org.nextprot.api.core.domain.CvDatabasePreferredLink;
import org.nextprot.api.core.domain.DbXref;
import org.nextprot.api.core.utils.dbxref.DbXrefURLBaseResolver;

class CosmicXrefURLResolver extends DbXrefURLBaseResolver {

    @Override
    protected String getTemplateURL(DbXref xref) {

        String accession = xref.getAccession();

        if (accession.startsWith("COSM"))
            return CvDatabasePreferredLink.COSMIC_MUTATION.getLink();
        else if (accession.startsWith("COSS"))
            return CvDatabasePreferredLink.COSMIC_SAMPLE.getLink();
        return CvDatabasePreferredLink.COSMIC_GENE.getLink();
    }

    @Override
    protected String getAccessionNumber(DbXref xref) {

        String accession = xref.getAccession();

        if (accession.startsWith("COSM"))
            return accession.replaceFirst("COSM", "");
        else if (accession.startsWith("COSS"))
            return accession.replaceFirst("COSS", "");

        return accession;
    }
}