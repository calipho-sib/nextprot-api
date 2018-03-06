package org.nextprot.api.core.utils.dbxref.conv;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.commons.constants.IdentifierOffset;
import org.nextprot.api.core.domain.DbXref;
import org.nextprot.api.core.utils.dbxref.XrefDatabase;
import org.nextprot.api.core.utils.dbxref.resolver.DbXrefURLResolverSupplier;

import java.util.Collections;
import java.util.List;

import static org.nextprot.api.core.utils.dbxref.conv.DbXrefConverterTest.createDbXref;
import static org.nextprot.api.core.utils.dbxref.conv.DbXrefConverterTest.createDbXrefProperty;

public class RefSeqDbXrefConverterTest {

    @Test
    public void testConvert() {

        DbXrefPropertyToXrefConverter converter = new RefSeqDbXrefConverter();

        DbXref xref = createDbXref("NP_000198.1", DbXrefURLResolverSupplier.REF_SEQ.getXrefDatabase().getName(), "http://www.ncbi.nlm.nih.gov/protein/%s", "http://www.ncbi.nlm.nih.gov/refseq/");
        xref.setProperties(Collections.singletonList(createDbXrefProperty(5309676, "nucleotide sequence ID", "NM_000207.2")));

        List<DbXref> xrefs = converter.convert(xref);
        Assert.assertEquals(1, xrefs.size());

        DbXref ref = xrefs.get(0);
        Assert.assertEquals(IdentifierOffset.XREF_PROPERTY_OFFSET + 5309676L, ref.getDbXrefId().longValue());
        Assert.assertEquals("NM_000207.2", ref.getAccession());
        Assert.assertEquals("http://www.ncbi.nlm.nih.gov/nuccore/%s", ref.getLinkUrl());
        Assert.assertEquals("http://www.ncbi.nlm.nih.gov/refseq/", ref.getUrl());
        Assert.assertEquals("http://www.ncbi.nlm.nih.gov/nuccore/NM_000207.2", ref.getResolvedUrl());
        Assert.assertEquals(XrefDatabase.REF_SEQ.getName(), ref.getDatabaseName());
        Assert.assertEquals("Sequence databases", ref.getDatabaseCategory());
        Assert.assertTrue(ref.getProperties().isEmpty());
    }
}