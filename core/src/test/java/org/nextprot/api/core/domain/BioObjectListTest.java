package org.nextprot.api.core.domain;

import org.junit.Assert;
import org.junit.Test;

public class BioObjectListTest {

    @Test
    public void testComplex() {

        BioObjectList bioComplex = new BioObjectList(BioObject.BioType.COMPLEX,
                BioObject.internal(BioObject.BioType.PROTEIN),
                BioObject.internal(BioObject.BioType.PROTEIN),
                BioObject.external(BioObject.BioType.CHEMICAL, "ChEBI")
        );

        Assert.assertEquals(3, bioComplex.size());
        Assert.assertEquals(BioObject.BioType.COMPLEX, bioComplex.getBioType());
        Assert.assertEquals(BioObject.ResourceType.MIXED, bioComplex.getResourceType());
        Assert.assertNull(bioComplex.getDatabase());
    }

    /*
<biological-object bio-type="complex">
	<biological-object-list>
		<biological-object bio-type="protein" accession="NX_Q3L8U1" database="neXtProt"/>
		<biological-object bio-type="protein-isoform" accession="NX_Q3L8U1-3" database="neXtProt"/>
		<biological-object bio-type="protein" resource-internal-ref="15642964"/>
		<biological-object bio-type="chemical" resource-internal-ref="15642965"/>
	</biological-object-list>
</biological-object>
...
<xref database="UniProt" category="Sequence databases" accession="Q81LD0" internal-id="15642964">
    <property-list>
        <property name="gene designation" value="hemL2"/>
    </property-list>
    <url>
    <![CDATA[ http://www.uniprot.org/uniprot/Q81LD0 ]]>
    </url>
</xref>

<xref database="ChEBI" category="Other" accession="CHEBI:29033" internal-id="39334227">
    <url>
        <![CDATA[http://www.ebi.ac.uk/chebi/searchId.do?chebiId=CHEBI:CHEBI:29033]]>
    </url>
</xref>
    */
    @Test
    public void testComplex2() {

        BioObject be = BioObject.internal(BioObject.BioType.PROTEIN);
        be.setAccession("NX_Q3L8U1");

        BioObject bi = BioObject.internal(BioObject.BioType.PROTEIN_ISOFORM);
        bi.setAccession("NX_Q3L8U1-3");

        BioObject be2 = BioObject.external(BioObject.BioType.PROTEIN, "UniProt");
        be.setAccession("Q81LD0");

        BioObject chemical = BioObject.external(BioObject.BioType.CHEMICAL, "ChEBI");
        be.setAccession("CHEBI:29033");

        BioObjectList bioComplex = new BioObjectList(BioObject.BioType.COMPLEX, be, bi, be2, chemical);

        Assert.assertEquals(4, bioComplex.size());
        Assert.assertEquals(BioObject.BioType.COMPLEX, bioComplex.getBioType());
        Assert.assertEquals(BioObject.ResourceType.MIXED, bioComplex.getResourceType());

        Assert.assertEquals(BioObject.ResourceType.INTERNAL, bioComplex.getContent().get(0).getResourceType());
        Assert.assertEquals(BioObject.BioType.PROTEIN, bioComplex.getContent().get(0).getBioType());

        Assert.assertEquals(BioObject.ResourceType.INTERNAL, bioComplex.getContent().get(1).getResourceType());
        Assert.assertEquals(BioObject.BioType.PROTEIN_ISOFORM, bioComplex.getContent().get(1).getBioType());

        Assert.assertEquals(BioObject.ResourceType.EXTERNAL, bioComplex.getContent().get(2).getResourceType());
        Assert.assertEquals(BioObject.BioType.PROTEIN, bioComplex.getContent().get(2).getBioType());

        Assert.assertEquals(BioObject.ResourceType.EXTERNAL, bioComplex.getContent().get(3).getResourceType());
        Assert.assertEquals(BioObject.BioType.CHEMICAL, bioComplex.getContent().get(3).getBioType());
    }
}