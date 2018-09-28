package org.nextprot.api.core.service.annotation.merge;

import com.google.common.collect.Sets;
import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.core.domain.StatementAnnotDescription;

import java.text.ParseException;

public class AnnotationDescriptionParserTest {

    @Test
    public void testParser() throws ParseException {

        AnnotationDescriptionParser parser = new AnnotationDescriptionParser("bobsponge");

        StatementAnnotDescription desc = parser.parse("phosphotyrosine; by ABL1");
        Assert.assertEquals("bobsponge", desc.getGeneName());
        Assert.assertEquals("Phosphotyrosine", desc.getPtm());
        Assert.assertEquals(Sets.newHashSet("ABL1"), desc.getEnzymeGeneNames());
        Assert.assertEquals("Phosphotyrosine; by ABL1", desc.format());
    }

    @Test
    public void testParserByAutocatalysis() throws ParseException {

        AnnotationDescriptionParser parser = new AnnotationDescriptionParser("ABL1");

        StatementAnnotDescription desc = parser.parse("phosphotyrosine; by ABL1");
        Assert.assertEquals("ABL1", desc.getGeneName());
        Assert.assertEquals("Phosphotyrosine", desc.getPtm());
        Assert.assertEquals(Sets.newHashSet("ABL1"), desc.getEnzymeGeneNames());
        Assert.assertEquals("Phosphotyrosine; by autocatalysis", desc.format());
    }

    @Test
    public void testParserByAutocatalysis2() throws ParseException {

        AnnotationDescriptionParser parser = new AnnotationDescriptionParser("ABL1");

        StatementAnnotDescription desc = parser.parse("phosphotyrosine; by autocatalysis");
        Assert.assertEquals("ABL1", desc.getGeneName());
        Assert.assertEquals("Phosphotyrosine", desc.getPtm());
        Assert.assertEquals(Sets.newHashSet("ABL1"), desc.getEnzymeGeneNames());
        Assert.assertEquals("Phosphotyrosine; by autocatalysis", desc.format());
    }
}