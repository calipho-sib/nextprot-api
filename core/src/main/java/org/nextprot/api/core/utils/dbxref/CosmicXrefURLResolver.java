package org.nextprot.api.core.utils.dbxref;

import org.nextprot.api.core.domain.CvDatabasePreferredLink;
import org.nextprot.api.core.domain.DbXref;

public class CosmicXrefURLResolver extends DbXrefURLBaseResolver {

    private enum Type {

        COSM, COSS, OTHER
    }

    private Type type;

    @Override
    protected void beforeResolution(DbXref xref) {

        String accession = xref.getAccession();

        if (accession.startsWith("COSM")) {
            type = Type.COSM;
        }
        else if (accession.startsWith("COSS")) {
            type = Type.COSS;
        }
        else {
            type = Type.OTHER;
        }
    }

    @Override
    protected String getTemplateUrl(DbXref xref) {

        if (type == Type.COSM)
            return CvDatabasePreferredLink.COSMIC_MUTATION.getLink();
        else if (type == Type.COSS)
            return CvDatabasePreferredLink.COSMIC_SAMPLE.getLink();
        return CvDatabasePreferredLink.COSMIC_GENE.getLink();
    }

    @Override
    protected String getPrimaryId(DbXref xref) {

        String accession = xref.getAccession();

        switch (type) {

            case COSM:
                accession = accession.replaceFirst("COSM", "");
                break;
            case COSS:
                accession = accession.replaceFirst("COSS", "");
        }

        return accession;
    }
}