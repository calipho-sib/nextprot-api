package org.nextprot.api.core.utils.dbxref.resolver;

import org.nextprot.api.core.domain.CvDatabasePreferredLink;
import org.nextprot.api.core.domain.DbXref;
import org.nextprot.api.core.utils.dbxref.DbXrefURLBaseResolver;

import java.util.Optional;

class JcrbXrefURLResolver extends DbXrefURLBaseResolver {

    @Override
    protected String getAccessionNumber(DbXref xref) {

        return xref.getAccession().toLowerCase();
    }

    @Override
    protected String getTemplateURL(DbXref xref) {

        Optional<XRefDatabase> db = XRefDatabase.optionalValueOfDbName(xref.getDatabaseName());

        if (db.isPresent()) {

            if (db.get() == XRefDatabase.IFO)
                return CvDatabasePreferredLink.IFO.getLink();
            else if (db.get() == XRefDatabase.JCRB) {
                return CvDatabasePreferredLink.JCRB.getLink();
            }
            throw new UnresolvedXrefURLException("'"+xref.getDatabaseName()+"' is not a JCRB db");
        }

        throw new UnresolvedXrefURLException("missing db name");
    }
}