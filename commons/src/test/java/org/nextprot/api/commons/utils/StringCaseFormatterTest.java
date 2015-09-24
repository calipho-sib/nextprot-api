package org.nextprot.api.commons.utils;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by fnikitin on 27/08/15.
 */
public class StringCaseFormatterTest {

    @Test
    public void testCamelCase() {

        String cc = new StringCaseFormatter("nextprot-anatomy-cv").camel(false).format();
        Assert.assertEquals("NextprotAnatomyCv", cc);
    }

    @Test
    public void testCamelCase2() {

        String cc = new StringCaseFormatter("nextprot-anatomy-cv").camel(true).format();
        Assert.assertEquals("nextprotAnatomyCv", cc);
    }

    @Test
    public void testCamelCase2bis() {

        String cc = new StringCaseFormatter("nextprot-anatomy-cv").camel().format();
        Assert.assertEquals("nextprotAnatomyCv", cc);
    }

    @Test
    public void testCamelCase3() {

        String cc = new StringCaseFormatter("nextprot_anatomy_cv").camel(false).format();
        Assert.assertEquals("NextprotAnatomyCv", cc);
    }

    @Test
    public void testCamelCase4() {

        String cc = new StringCaseFormatter("nextprot_anatomy_cv").camel(true).format();
        Assert.assertEquals("nextprotAnatomyCv", cc);
    }

    @Test
    public void testCamelCase5() {

        String cc = new StringCaseFormatter("full name").camel(true).format();
        Assert.assertEquals("fullName", cc);
    }

    @Test
    public void testCamelCaseIdempotent() {

        String cc = new StringCaseFormatter("nextprotAnatomyCv").camel(true).format();
        Assert.assertEquals("nextprotAnatomyCv", cc);
    }

    @Test
    public void testCamelCaseIdempotent2() {

        String cc = new StringCaseFormatter("fullName").camel(true).format();
        Assert.assertEquals("fullName", cc);
    }

    @Test
    public void testKebabCase() {

        String cc = new StringCaseFormatter("NextprotAnatomyCv").kebab().format();
        Assert.assertEquals("nextprot-anatomy-cv", cc);
    }

    @Test
    public void testKebabCaseIdempotent() {

        String cc = new StringCaseFormatter("nextprot-anatomy-cv").kebab().format();
        Assert.assertEquals("nextprot-anatomy-cv", cc);
    }

    @Test
    public void testSnakeCase() {

        String cc = new StringCaseFormatter("NextprotAnatomyCv").snake().format();
        Assert.assertEquals("nextprot_anatomy_cv", cc);
    }

    @Test
    public void testSnakeCaseIdempotent() {

        String cc = new StringCaseFormatter("nextprot_anatomy_cv").snake().format();
        Assert.assertEquals("nextprot_anatomy_cv", cc);
    }

    @Test
    public void testXCaseBuilder() {

        String string = new StringCaseFormatter("PROTEIN_ENTRY").camel(true).kebab().format();
        Assert.assertEquals("protein-entry", string);
    }

    @Test
    public void testXCaseBuilder2() {

        String string = new StringCaseFormatter("PROTEIN_ENTRY").camel(true).kebab().yelling().format();
        Assert.assertEquals("PROTEIN-ENTRY", string);
    }

    @Test
    public void testXCaseBuilder3() {

        String string = new StringCaseFormatter("PROTEIN-ENTRY").camel(false).snake().whispering().format();
        Assert.assertEquals("protein_entry", string);
    }
}