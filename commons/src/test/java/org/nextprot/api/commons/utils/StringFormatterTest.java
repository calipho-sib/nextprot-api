package org.nextprot.api.commons.utils;

import org.junit.Assert;
import org.junit.Test;

public class StringFormatterTest {

    @Test
    public void testCamelCase() {

        String formattedString = new StringFormatter("nextprot-anatomy-cv").camelFirstWordLetterLowerCase(false).format();
        Assert.assertEquals("NextprotAnatomyCv", formattedString);
    }

    @Test
    public void testCamelCase2() {

        String formattedString = new StringFormatter("nextprot-anatomy-cv").camel().format();
        Assert.assertEquals("nextprotAnatomyCv", formattedString);
    }

    @Test
    public void testCamelCase2bis() {

        String formattedString = new StringFormatter("nextprot-anatomy-cv").camel().format();
        Assert.assertEquals("nextprotAnatomyCv", formattedString);
    }

    @Test
    public void testCamelCase3() {

        String formattedString = new StringFormatter("nextprot_anatomy_cv").camelFirstWordLetterLowerCase(false).format();
        Assert.assertEquals("NextprotAnatomyCv", formattedString);
    }

    @Test
    public void testCamelCase4() {

        String formattedString = new StringFormatter("nextprot_anatomy_cv").camel().format();
        Assert.assertEquals("nextprotAnatomyCv", formattedString);
    }

    @Test
    public void testCamelCase5() {

        String formattedString = new StringFormatter("full name").camel().format();
        Assert.assertEquals("fullName", formattedString);
    }

    @Test
    public void testCamelCaseIdempotent() {

        String formattedString = new StringFormatter("nextprotAnatomyCv").camel().format();
        Assert.assertEquals("nextprotAnatomyCv", formattedString);
    }

    @Test
    public void testCamelCaseIdempotent2() {

        String formattedString = new StringFormatter("fullName").camel().format();
        Assert.assertEquals("fullName", formattedString);
    }

    @Test
    public void testKebabCase() {

        String formattedString = new StringFormatter("NextprotAnatomyCv").kebab().format();
        Assert.assertEquals("nextprot-anatomy-cv", formattedString);
    }

    @Test
    public void testKebabCaseIdempotent() {

        String formattedString = new StringFormatter("nextprot-anatomy-cv").kebab().format();
        Assert.assertEquals("nextprot-anatomy-cv", formattedString);
    }

    @Test
    public void testSnakeCase() {

        String formattedString = new StringFormatter("NextprotAnatomyCv").snake().format();
        Assert.assertEquals("nextprot_anatomy_cv", formattedString);
    }

    @Test
    public void testSnakeCaseIdempotent() {

        String formattedString = new StringFormatter("nextprot_anatomy_cv").snake().format();
        Assert.assertEquals("nextprot_anatomy_cv", formattedString);
    }

    @Test
    public void testXCaseBuilder() {

        String formattedString = new StringFormatter("PROTEIN_ENTRY").camel().kebab().format();
        Assert.assertEquals("protein-entry", formattedString);
    }

    @Test
    public void testXCaseBuilder2() {

        String formattedString = new StringFormatter("PROTEIN_ENTRY").camel().kebab().yelling().format();
        Assert.assertEquals("PROTEIN-ENTRY", formattedString);
    }

    @Test
    public void testXCaseBuilder3() {

        String formattedString = new StringFormatter("PROTEIN-ENTRY").camelFirstWordLetterLowerCase(false).snake().whispering().format();
        Assert.assertEquals("protein_entry", formattedString);
    }

    @Test
    public void testKebabCase2() {

        String formattedString = new StringFormatter("positional-annotation;secondary structure").camel().kebab().format();
        Assert.assertEquals("positional-annotation;secondary-structure", formattedString);
    }
}