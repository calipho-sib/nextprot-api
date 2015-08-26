package org.nextprot.api.commons.utils;


import org.junit.Assert;
import org.junit.Test;

/**
 * Created by fnikitin on 22/04/15.
 */
public class StringUtilsTest {

	
    
    @Test
    public void testCamelCase() {

        String cc = StringUtils.toCamelCase("nextprot-anatomy-cv", false);
        Assert.assertEquals("NextprotAnatomyCv", cc);
    }

    @Test
    public void testCamelCase2() {

        String cc = StringUtils.toCamelCase("nextprot-anatomy-cv", true);
        Assert.assertEquals("nextprotAnatomyCv", cc);
    }

    @Test
    public void testCamelCase3() {

        String cc = StringUtils.toCamelCase("nextprot_anatomy_cv", true);
        Assert.assertEquals("nextprotAnatomyCv", cc);
    }

    @Test
    public void testKebabCase() {

        String cc = StringUtils.camelToKebabCase("NextprotAnatomyCv");
        Assert.assertEquals("nextprot-anatomy-cv", cc);
    }

    @Test
    public void testKebabCaseIdempotent() {

        String cc = StringUtils.camelToKebabCase("nextprot-anatomy-cv");
        Assert.assertEquals("nextprot-anatomy-cv", cc);
    }

    @Test
    public void testSnakeCase() {

        String cc = StringUtils.camelToSnakeCase("NextprotAnatomyCv");
        Assert.assertEquals("nextprot_anatomy_cv", cc);
    }

    @Test
    public void testSnakeCaseIdempotent() {

        String cc = StringUtils.camelToSnakeCase("nextprot_anatomy_cv");
        Assert.assertEquals("nextprot_anatomy_cv", cc);
    }

    @Test
    public void testSlugReplaceWSPunctuations() {

        String cc = StringUtils.slug("nextprot: anatomy;cv");
        Assert.assertEquals("nextprot__anatomy_cv", cc);
    }

    @Test
    public void testSlugRemoveNonWordChars() {

        String cc = StringUtils.slug("hel@l#o.world");
        Assert.assertEquals("hello_world", cc);
    }

    @Test
    public void testWrappingRec() {

        String text ="MGDREQLLQRARLAEQAERYDDMASAMKAVTELNEPLSNEDRNLLSVAYKNVVGARRSSW" +
                "RVISSIEQKTMADGNEKKLEKVKAYREKIEKELETVCNDVLSLLDKFLIKNCNDFQYESK" +
                "VFYLKMKGDYYRYLAEVASGEKKNSVVEASEAAYKEAFEISKEQMQPTHPIRLGLALNFS" +
                "VFYYEIQNAPEQACLLAKQAFDDAIAELDTLNEDSYKDSTLIMQLLRDNLTLWTSDQQDE" +
                "EAGEGN";

        String expectedText = "MGDREQLLQRARLAEQAERYDDMASAMKAVTELNEPLSNEDRNLLSVAYKNVVGARRSSW\n" +
                "RVISSIEQKTMADGNEKKLEKVKAYREKIEKELETVCNDVLSLLDKFLIKNCNDFQYESK\n" +
                "VFYLKMKGDYYRYLAEVASGEKKNSVVEASEAAYKEAFEISKEQMQPTHPIRLGLALNFS\n" +
                "VFYYEIQNAPEQACLLAKQAFDDAIAELDTLNEDSYKDSTLIMQLLRDNLTLWTSDQQDE\n" +
                "EAGEGN";

        Assert.assertEquals(expectedText, StringUtils.wrapTextRec(text, 60, new StringBuilder()));
    }

    @Test
    public void testWrapping() {

        String text ="MGDREQLLQRARLAEQAERYDDMASAMKAVTELNEPLSNEDRNLLSVAYKNVVGARRSSW" +
                "RVISSIEQKTMADGNEKKLEKVKAYREKIEKELETVCNDVLSLLDKFLIKNCNDFQYESK" +
                "VFYLKMKGDYYRYLAEVASGEKKNSVVEASEAAYKEAFEISKEQMQPTHPIRLGLALNFS" +
                "VFYYEIQNAPEQACLLAKQAFDDAIAELDTLNEDSYKDSTLIMQLLRDNLTLWTSDQQDE" +
                "EAGEGN";

        String expectedText = "MGDREQLLQRARLAEQAERYDDMASAMKAVTELNEPLSNEDRNLLSVAYKNVVGARRSSW\n" +
                "RVISSIEQKTMADGNEKKLEKVKAYREKIEKELETVCNDVLSLLDKFLIKNCNDFQYESK\n" +
                "VFYLKMKGDYYRYLAEVASGEKKNSVVEASEAAYKEAFEISKEQMQPTHPIRLGLALNFS\n" +
                "VFYYEIQNAPEQACLLAKQAFDDAIAELDTLNEDSYKDSTLIMQLLRDNLTLWTSDQQDE\n" +
                "EAGEGN";

        Assert.assertEquals(expectedText, StringUtils.wrapText(text, 60));
    }
    
    @Test
    public void testHtmlTagsRemoval() {
        String cc = StringUtils.removeHtmlTags("<hello>world</hello>");
        Assert.assertEquals("world", cc);
    }

}