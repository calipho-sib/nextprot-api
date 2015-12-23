package org.nextprot.api.core.utils.dbxref.conv;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.commons.constants.IdentifierOffset;
import org.nextprot.api.core.domain.DbXref;

import java.util.List;

public class EnsemblXrefPropertyConverterTest {

    @Test
    public void testConvert() {

        EnsemblXrefPropertyConverter converter = new EnsemblXrefPropertyConverter();

        DbXref.EnsemblInfos infos = new DbXref.EnsemblInfos(620964, "ENSG00000254647", 1L, "ENSP00000408400", 2L);

        List<DbXref.DbXrefProperty> props = converter.convert(infos);
        Assert.assertEquals(2, props.size());

        assertProducedXrefPropertyListContains(props, 620964, IdentifierOffset.XREF_ENSEMBL_GENE_PROPERTY_OFFSET + 1, EnsemblXrefPropertyConverter.MAPPED_GENE_NAME, "ENSG00000254647");
        assertProducedXrefPropertyListContains(props, 620964, IdentifierOffset.XREF_ENSEMBL_PROTEIN_PROPERTY_OFFSET + 2, EnsemblXrefPropertyConverter.MAPPED_PROTEIN_NAME, "ENSP00000408400");
    }

    private void assertProducedXrefPropertyListContains(List<DbXref.DbXrefProperty> producedXrefProperties, long expectedRefId, long expectedPropId, String expectedName, String expectedValue) {

        DbXref.DbXrefProperty xrefPropToCheck = null;

        for (DbXref.DbXrefProperty producedProperty : producedXrefProperties) {

            if (producedProperty.getPropertyId() == expectedPropId) {

                xrefPropToCheck = producedProperty;
                break;
            }
        }

        Assert.assertNotNull(xrefPropToCheck);

        Assert.assertEquals(expectedRefId, xrefPropToCheck.getDbXrefId().longValue());
        Assert.assertEquals(expectedName, xrefPropToCheck.getName());
        Assert.assertEquals(expectedValue, xrefPropToCheck.getValue());
    }
}