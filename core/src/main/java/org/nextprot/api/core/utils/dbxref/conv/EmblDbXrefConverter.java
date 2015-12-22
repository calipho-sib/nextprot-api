package org.nextprot.api.core.utils.dbxref.conv;

import org.nextprot.api.commons.constants.IdentifierOffset;
import org.nextprot.api.core.domain.CvDatabasePreferredLink;
import org.nextprot.api.core.domain.DbXref;
import org.nextprot.api.core.domain.XRefDatabase;

import java.util.ArrayList;
import java.util.List;

class EmblDbXrefConverter implements DbXrefPropertyToXrefConverter {

    @Override
    public List<DbXref> convert(DbXref xref) {

        List<DbXref> list = new ArrayList<>(3);

        for (DbXref.DbXrefProperty property : xref.getProperties()) {

            if ("molecule type".equals(property.getName()) && "Unassigned_DNA".equals(property.getValue())) {

                list.add(createEmblDbXrefUnassignedDNAFromDbXrefProperty(property));
            }
            else if ("genomic sequence ID".equals(property.getName())) {

                list.add(createEmblDbXrefGenSeqFromDbXrefProperty(property));
            }
            else if ("protein sequence ID".equals(property.getName())) {

                list.add(createEmblDbXrefProtSeqFromDbXrefProperty(property));
            }
        }

        return list;
    }

    private DbXref createEmblDbXrefUnassignedDNAFromDbXrefProperty(DbXref.DbXrefProperty property) {

        DbXref dbXRef = new DbXref();

        return dbXRef;
    }

    private DbXref createEmblDbXrefGenSeqFromDbXrefProperty(DbXref.DbXrefProperty property) {

        DbXref dbXRef = new DbXref();

        dbXRef.setDbXrefId(IdentifierOffset.XREF_PROPERTY_OFFSET +property.getPropertyId());
        dbXRef.setAccession(property.getValue());
        dbXRef.setDatabaseCategory("Sequence databases");
        dbXRef.setDatabaseName(CvDatabasePreferredLink.EMBL_GENE.getDbName());
        dbXRef.setUrl(XRefDatabase.EMBL.getUrl());
        dbXRef.setLinkUrl(CvDatabasePreferredLink.EMBL_GENE.getLink());
        dbXRef.setProperties(new ArrayList<DbXref.DbXrefProperty>());

        return dbXRef;
    }

    private DbXref createEmblDbXrefProtSeqFromDbXrefProperty(DbXref.DbXrefProperty property) {

        DbXref dbXRef = new DbXref();

        dbXRef.setDbXrefId(IdentifierOffset.XREF_PROPERTY_OFFSET +property.getPropertyId());
        dbXRef.setAccession(property.getValue());
        dbXRef.setDatabaseCategory("Sequence databases");
        dbXRef.setDatabaseName(CvDatabasePreferredLink.EMBL_PROTEIN.getDbName());
        dbXRef.setUrl(XRefDatabase.EMBL.getUrl());
        dbXRef.setLinkUrl(CvDatabasePreferredLink.EMBL_PROTEIN.getLink());
        dbXRef.setProperties(new ArrayList<DbXref.DbXrefProperty>());

        return dbXRef;
    }
}
