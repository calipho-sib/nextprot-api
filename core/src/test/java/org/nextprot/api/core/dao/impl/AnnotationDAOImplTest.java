package org.nextprot.api.core.dao.impl;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.core.domain.annotation.AnnotationProperty;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by fnikitin on 08/07/15.
 */
public class AnnotationDAOImplTest {

    @Test
    public void testSetPropertyNameValue() throws Exception {

        AnnotationProperty property = new AnnotationProperty();

        AnnotationDAOImpl.setPropertyNameValue(property, "mutation", "blablabla");

        Assert.assertEquals("blablabla", property.getValue());
    }

    @Test
    public void testSetMutationPropertyNameValue() throws Exception {

        AnnotationProperty property = new AnnotationProperty();

        AnnotationDAOImpl.setPropertyNameValue(property, "mutation AA", "p.R54C");

        Assert.assertNotEquals("p.R54C", property.getValue());
    }

    @Test
    public void testAsHGVMutationFormats() throws Exception {

        Map<String, String> hgvFormats = new HashMap<>();

        hgvFormats.put("p.R54C", "p.Arg54Cys");
        hgvFormats.put("p.L11L", "p.Leu11Leu");
        hgvFormats.put("p.E3815*", "p.Glu3815Ter");
        hgvFormats.put("p.I6616del", "p.Ile6616del");
        hgvFormats.put("p.K487_L498del12", "p.Lys487_Leu498del");
        hgvFormats.put("p.P564_L567delPRAL", "p.Pro564_Leu567del");
        hgvFormats.put("p.M682fs*1", "p.Met682fsTer1");
        hgvFormats.put("p.S1476fs*>9", "p.Ser1476fsTer>9");

        for (Map.Entry<String, String> hgvFormat : hgvFormats.entrySet()) {
            Assert.assertEquals(hgvFormat.getValue(), AnnotationDAOImpl.asHGVMutationFormat(hgvFormat.getKey()));
        }
    }
}