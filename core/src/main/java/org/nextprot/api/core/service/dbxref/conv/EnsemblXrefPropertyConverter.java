package org.nextprot.api.core.service.dbxref.conv;

import org.nextprot.api.commons.constants.IdentifierOffset;
import org.nextprot.api.core.domain.DbXref;

import java.util.ArrayList;
import java.util.List;

/**
 * Convert DbXref.EnsemblInfos to DbXrefProperties
 *
 * Created by fnikitin on 23/12/15.
 */
public class EnsemblXrefPropertyConverter implements Converter<DbXref.EnsemblInfos, DbXref.DbXrefProperty> {

    public static final String MAPPED_GENE_NAME = "nxmapped gene ID";
    public static final String MAPPED_PROTEIN_NAME = "nxmapped protein sequence ID";

    public static EnsemblXrefPropertyConverter getInstance() {
        return Loader.INSTANCE;
    }

    /**
     * Does a thread-safe lazy-initialization of the instance without explicit synchronization
     * @see <a href="http://stackoverflow.com/questions/11165852/java-singleton-and-synchronization">java-singleton-and-synchronization</a>
     */
    private static class Loader {

        private static EnsemblXrefPropertyConverter INSTANCE = new EnsemblXrefPropertyConverter();
    }

    @Override
    public List<DbXref.DbXrefProperty> convert(DbXref.EnsemblInfos ensemblInfos) {

        List<DbXref.DbXrefProperty> list = new ArrayList<>();

        DbXref.DbXrefProperty geneProperty = new DbXref.DbXrefProperty();
        geneProperty.setDbXrefId(ensemblInfos.getTranscriptXrefId());
        geneProperty.setName(MAPPED_GENE_NAME);
        geneProperty.setPropertyId(IdentifierOffset.XREF_ENSEMBL_GENE_PROPERTY_OFFSET+ensemblInfos.getGenePropertyId());
        geneProperty.setValue(ensemblInfos.getGeneAc());

        DbXref.DbXrefProperty proteinProperty = new DbXref.DbXrefProperty();
        proteinProperty.setDbXrefId(ensemblInfos.getTranscriptXrefId());
        proteinProperty.setName(MAPPED_PROTEIN_NAME);
        proteinProperty.setPropertyId(IdentifierOffset.XREF_ENSEMBL_PROTEIN_PROPERTY_OFFSET+ensemblInfos.getProteinPropertyId());
        proteinProperty.setValue(ensemblInfos.getProteinAc());

        list.add(geneProperty);
        list.add(proteinProperty);

        return list;
    }
}
