package org.nextprot.api.commons.utils;


import org.junit.Assert;
import org.junit.Test;

/**
 * Created by fnikitin on 22/04/15.
 */
public class StringUtilsTest {

    @Test
    public void testXCaseBuilderCamelCase() {

        String cc = StringUtils.createXCaseBuilder("nextprot-anatomy-cv").camel(false).format();
        Assert.assertEquals("NextprotAnatomyCv", cc);
    }

    @Test
    public void testSnakeCase() {

        String cc = StringUtils.camelToSnakeCase("NextprotAnatomyCv");
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
    public void testFormatForenameInitials() {

        Assert.assertEquals("J.-C.", StringUtils.formatForenameInitials("Jean-Christophe"));
    }
}