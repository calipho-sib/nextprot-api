package org.nextprot.api.core.utils.dbxref.conv;

import org.nextprot.api.commons.constants.IdentifierOffset;
import org.nextprot.api.core.domain.CvDatabasePreferredLink;
import org.nextprot.api.core.domain.DbXref;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class RefSeqDbXrefConverter implements DbXrefPropertyToXrefConverter {

    @Override
    public List<DbXref> convert(DbXref xref) {

        for (DbXref.DbXrefProperty property : xref.getProperties()) {

            if ("nucleotide sequence ID".equals(property.getName())) {

                return Collections.singletonList(createRefSeqNucleotideDbXrefFromDbXrefProperty(xref, property));
            }
        }
        return new ArrayList<>();
    }

    private DbXref createRefSeqNucleotideDbXrefFromDbXrefProperty(DbXref xref, DbXref.DbXrefProperty property) {

        DbXref dbXRef = new DbXref();

        dbXRef.setDbXrefId(IdentifierOffset.XREF_PROPERTY_OFFSET +property.getPropertyId());
        dbXRef.setAccession(property.getValue());
        dbXRef.setDatabaseCategory("Sequence databases");
        dbXRef.setDatabaseName(CvDatabasePreferredLink.REFSEQ_NUCLEOTIDE.getDbName());
        dbXRef.setUrl(xref.getUrl());
        dbXRef.setLinkUrl(CvDatabasePreferredLink.REFSEQ_NUCLEOTIDE.getLink());
        dbXRef.setProperties(new ArrayList<DbXref.DbXrefProperty>());

        return dbXRef;
    }
}
