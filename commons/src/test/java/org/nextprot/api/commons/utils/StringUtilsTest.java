package org.nextprot.api.commons.utils;


import org.junit.Assert;
import org.junit.Test;

/**
 * Created by fnikitin on 22/04/15.
 */
public class StringUtilsTest {

    @Test
    public void testCamelCase() {

        String cc = StringUtils.createXCaseBuilder("nextprot-anatomy-cv").camel(false).build();
        Assert.assertEquals("NextprotAnatomyCv", cc);
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
}