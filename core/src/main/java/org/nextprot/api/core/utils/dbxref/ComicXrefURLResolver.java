package org.nextprot.api.core.utils.dbxref;

import org.nextprot.api.core.domain.CvDatabasePreferredLink;
import org.nextprot.api.core.domain.DbXref;

public class ComicXrefURLResolver extends DbXrefURLBaseResolver {

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

        String templateURL = xref.getLinkUrl();

        switch (type) {

            case COSM:
                templateURL = CvDatabasePreferredLink.COSMIC_MUTATION.getLink();
                break;
            case COSS:
                templateURL = CvDatabasePreferredLink.COSMIC_SAMPLE.getLink();
                break;
            case OTHER:
                templateURL = CvDatabasePreferredLink.COSMIC_GENE.getLink();
        }

        return templateURL;
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