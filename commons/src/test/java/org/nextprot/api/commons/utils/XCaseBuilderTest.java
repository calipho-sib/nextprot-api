package org.nextprot.api.commons.utils;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by fnikitin on 27/08/15.
 */
public class XCaseBuilderTest {

    @Test
    public void testCamelCase() {

        String cc = new XCaseBuilder("nextprot-anatomy-cv").camel(false).build();
        Assert.assertEquals("NextprotAnatomyCv", cc);
    }

    @Test
    public void testCamelCase2() {

        String cc = new XCaseBuilder("nextprot-anatomy-cv").camel(true).build();
        Assert.assertEquals("nextprotAnatomyCv", cc);
    }

    @Test
    public void testCamelCase3() {

        String cc = new XCaseBuilder("nextprot_anatomy_cv").camel(false).build();
        Assert.assertEquals("NextprotAnatomyCv", cc);
    }

    @Test
    public void testCamelCase4() {

        String cc = new XCaseBuilder("nextprot_anatomy_cv").camel(true).build();
        Assert.assertEquals("nextprotAnatomyCv", cc);
    }

    @Test
    public void testCamelCaseIdempotent() {

        String cc = new XCaseBuilder("nextprotAnatomyCv").camel(true).build();
        Assert.assertEquals("nextprotAnatomyCv", cc);
    }

    @Test
    public void testKebabCase() {

        String cc = new XCaseBuilder("NextprotAnatomyCv").kebab().build();
        Assert.assertEquals("nextprot-anatomy-cv", cc);
    }

    @Test
    public void testKebabCaseIdempotent() {

        String cc = new XCaseBuilder("nextprot-anatomy-cv").kebab().build();
        Assert.assertEquals("nextprot-anatomy-cv", cc);
    }

    @Test
    public void testSnakeCase() {

        String cc = new XCaseBuilder("NextprotAnatomyCv").snake().build();
        Assert.assertEquals("nextprot_anatomy_cv", cc);
    }

    @Test
    public void testSnakeCaseIdempotent() {

        String cc = new XCaseBuilder("nextprot_anatomy_cv").snake().build();
        Assert.assertEquals("nextprot_anatomy_cv", cc);
    }

    @Test
    public void testXCaseBuilder() {

        String string = new XCaseBuilder("PROTEIN_ENTRY").camel(true).kebab().build();
        Assert.assertEquals("protein-entry", string);
    }

    @Test
    public void testXCaseBuilder2() {

        String string = new XCaseBuilder("PROTEIN_ENTRY").camel(true).kebab().yelling().build();
        Assert.assertEquals("PROTEIN-ENTRY", string);
    }

    @Test
    public void testXCaseBuilder3() {

        String string = new XCaseBuilder("PROTEIN-ENTRY").camel(false).snake().whispering().build();
        Assert.assertEquals("protein_entry", string);
    }
}