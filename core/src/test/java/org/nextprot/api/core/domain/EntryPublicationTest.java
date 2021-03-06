package org.nextprot.api.core.domain;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.core.domain.publication.EntryPublication;
import org.nextprot.api.core.domain.publication.PublicationDirectLink;
import org.nextprot.api.core.domain.publication.PublicationProperty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EntryPublicationTest {

    @Test
    public void testPublicationDirectLinkListOrder() {

        EntryPublication p = new EntryPublication("", 12);
        List<String> scopes = Arrays.asList(
                "VARIANT SCA34 PHE-168",
                "CLEAVAGE OF INITIATOR METHIONINE [LARGE SCALE ANALYSIS]",
                "INVOLVEMENT IN SCA34");
        List<String> comments = Arrays.asList(
                "[PDB:1JWU] [Structure]",
                "[iPTMnet:P04637] [PTM/processing]Phosphorylation",
                "[GeneRif:3303] S100A4 has opposite roles in Tag7 and Hsp70- mediated tumoricidal mechanisms",
                "[PRO:PR:000028557] [PTM/processing]P18848-1",
                "[GAD:125207] [Pathology & Biotech]Associated with CARDIOVASCULAR: pulmonary hypertension; thrombosis, deep vein; pulmonary thromboembolism; HLA-B");

        List<PublicationDirectLink> list = new ArrayList<>();
        scopes.forEach(scope -> list.add(new PublicationDirectLink(188, PublicationProperty.SCOPE, scope)));
        comments.forEach(comment -> list.add(new PublicationDirectLink(188, PublicationProperty.COMMENT, comment)));

        p.setDirectLinks(list);

        Assert.assertEquals(3, p.getDirectLinks(PublicationProperty.SCOPE).size());
        Assert.assertEquals(5, p.getDirectLinks(PublicationProperty.COMMENT).size());

        List<PublicationDirectLink> links = p.getDirectLinks();
        Assert.assertEquals(8, links.size());
        // should be datasource UniProt first, then order by database alpha insensitive, then by label alpha
        Assert.assertEquals(links.get(0).getLabel(), "CLEAVAGE OF INITIATOR METHIONINE [LARGE SCALE ANALYSIS]");
        Assert.assertEquals(links.get(1).getLabel(), "INVOLVEMENT IN SCA34");
        Assert.assertEquals(links.get(2).getLabel(), "VARIANT SCA34 PHE-168");
        Assert.assertEquals(links.get(3).getLabel(), "[Pathology & Biotech]Associated with CARDIOVASCULAR: pulmonary hypertension; thrombosis, deep vein; pulmonary thromboembolism; HLA-B");
        Assert.assertEquals(links.get(4).getLabel(), "S100A4 has opposite roles in Tag7 and Hsp70- mediated tumoricidal mechanisms");
        Assert.assertEquals(links.get(5).getLabel(), "[PTM/processing]Phosphorylation");
        Assert.assertEquals(links.get(6).getLabel(), "[Structure]");
        Assert.assertEquals(links.get(7).getLabel(), "[PTM/processing]P18848-1");

        Assert.assertEquals(links.get(0).getDatasource(), "Uniprot");
        Assert.assertEquals(links.get(1).getDatasource(), "Uniprot");
        Assert.assertEquals(links.get(2).getDatasource(), "Uniprot");
        Assert.assertEquals(links.get(3).getDatasource(), "PIR");
        Assert.assertEquals(links.get(4).getDatasource(), "PIR");
        Assert.assertEquals(links.get(5).getDatasource(), "PIR");
        Assert.assertEquals(links.get(6).getDatasource(), "PIR");
        Assert.assertEquals(links.get(7).getDatasource(), "PIR");
    }

    @Test
    public void testPublicationDirectLinkListEmpty() {

        EntryPublication p = new EntryPublication("", 12);
        Assert.assertTrue(p.getDirectLinks().isEmpty());
    }

    @Test
    public void testPublicationDirectLinkListScopeEmpty() {

        EntryPublication p = new EntryPublication("", 12);
        Assert.assertTrue(p.getDirectLinks(PublicationProperty.SCOPE).isEmpty());
    }

    @Test
    public void testPublicationDirectLinkListCommentEmpty() {

        EntryPublication p = new EntryPublication("", 12);
        Assert.assertTrue(p.getDirectLinks(PublicationProperty.SCOPE).isEmpty());
    }
}