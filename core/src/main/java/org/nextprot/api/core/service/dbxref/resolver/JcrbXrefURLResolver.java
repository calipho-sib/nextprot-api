package org.nextprot.api.core.service.dbxref.resolver;

import org.nextprot.api.core.domain.CvDatabasePreferredLink;
import org.nextprot.api.core.domain.DbXref;

import java.util.Optional;

class JcrbXrefURLResolver extends DefaultDbXrefURLResolver {

    @Override
    protected String getAccessionNumber(DbXref xref) {

        return xref.getAccession().toLowerCase();
    }

    @Override
    public String getTemplateURL(DbXref xref) {

        Optional<DbXrefURLResolverSupplier> optResolverSupplier = DbXrefURLResolverSupplier.fromDbName(xref.getDatabaseName());

        if (optResolverSupplier.isPresent()) {

            if (optResolverSupplier.get() == DbXrefURLResolverSupplier.IFO)
                return CvDatabasePreferredLink.IFO.getLink();
            else if (optResolverSupplier.get() == DbXrefURLResolverSupplier.JCRB) {
                return CvDatabasePreferredLink.JCRB.getLink();
            }
            throw new UnresolvedXrefURLException("'"+xref.getDatabaseName()+"' is not a JCRB db");
        }

        throw new UnresolvedXrefURLException("missing db name");
    }
}