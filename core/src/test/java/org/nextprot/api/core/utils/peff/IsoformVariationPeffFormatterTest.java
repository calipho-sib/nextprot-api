package org.nextprot.api.core.utils.peff;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.commons.constants.AnnotationApiModel;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.Isoform;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.domain.annotation.AnnotationIsoformSpecificity;
import org.nextprot.api.core.domain.annotation.AnnotationVariant;
import org.nextprot.api.core.utils.NXVelocityUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by fnikitin on 05/05/15.
 */
public class IsoformVariationPeffFormatterTest {

    @Test
    public void testGetVariantList() throws Exception {

        Annotation variant1 = newVariant("R", "Q",
                new IsoformLocation("NX_P22694-1", 106, 106),
                new IsoformLocation("NX_P22694-2", 153, 153),
                new IsoformLocation("NX_P22694-3", 94, 94)
        );

        Annotation variant2 = newVariant("T", "M",
                new IsoformLocation("NX_P22694-1", 300, 300),
                new IsoformLocation("NX_P22694-2", 347, 347),
                new IsoformLocation("NX_P22694-3", 288, 288)
        );

        Annotation variant3 = newVariant("A", "P",
                new IsoformLocation("NX_P22694-1", 26, 26)
        );

        Entry entry = newEntry("NX_P22694", Arrays.asList(variant1, variant2, variant3));

        List<IsoformVariationPeffFormatter> isoformVariations = IsoformVariationPeffFormatter.getListVariant(entry, newIsoform("NX_P22694-1"));

        Assert.assertEquals("P", isoformVariations.get(0).getVariant());
        Assert.assertEquals(26, isoformVariations.get(0).getStart().getValue());
        Assert.assertEquals(26, isoformVariations.get(0).getEnd().getValue());

        Assert.assertEquals("Q", isoformVariations.get(1).getVariant());
        Assert.assertEquals(106, isoformVariations.get(1).getStart().getValue());
        Assert.assertEquals(106, isoformVariations.get(1).getEnd().getValue());

        Assert.assertEquals("M", isoformVariations.get(2).getVariant());
        Assert.assertEquals(300, isoformVariations.get(2).getStart().getValue());
        Assert.assertEquals(300, isoformVariations.get(2).getEnd().getValue());
    }

    @Test
    public void testGetVariantListAsPeff() throws Exception {

        Annotation variant1 = newVariant("R", "Q",
                new IsoformLocation("NX_P22694-1", 106, 106),
                new IsoformLocation("NX_P22694-2", 153, 153),
                new IsoformLocation("NX_P22694-3", 94, 94)
        );

        Annotation variant2 = newVariant("T", "M",
                new IsoformLocation("NX_P22694-1", 300, 300),
                new IsoformLocation("NX_P22694-2", 347, 347),
                new IsoformLocation("NX_P22694-3", 288, 288)
        );

        Annotation variant3 = newVariant("A", "P",
                new IsoformLocation("NX_P22694-1", 26, 26)
        );

        Annotation variant4 = newVariant("D", "",
                new IsoformLocation("NX_P22694-3", 6, 6)
        );

        Entry entry = newEntry("NX_P22694", Arrays.asList(variant1, variant2, variant3, variant4));

        Assert.assertEquals("(26|26|P)(106|106|Q)(300|300|M)", NXVelocityUtils.getVariantsAsPeffString(entry, newIsoform("NX_P22694-1")));
        Assert.assertEquals("(153|153|Q)(347|347|M)", NXVelocityUtils.getVariantsAsPeffString(entry, newIsoform("NX_P22694-2")));
        Assert.assertEquals("(6|6|)(94|94|Q)(288|288|M)", NXVelocityUtils.getVariantsAsPeffString(entry, newIsoform("NX_P22694-3")));
    }

    private static Annotation newVariant(String ori, String var, IsoformLocation... isoformPositions) {

        Annotation variant = new Annotation();
        variant.setCategory(AnnotationApiModel.VARIANT.getDbAnnotationTypeName());

        variant.setVariant(new AnnotationVariant(ori, var, ""));

        List<AnnotationIsoformSpecificity> specificityList = new ArrayList<>();

        for (IsoformLocation position : isoformPositions) {

            AnnotationIsoformSpecificity spec = new AnnotationIsoformSpecificity();

            spec.setIsoformName(position.getIsoformId());
            spec.setFirstPosition(position.getStart().getValue());
            spec.setLastPosition(position.getEnd().getValue());

            specificityList.add(spec);
        }
        variant.setTargetingIsoforms(specificityList);

        return variant;
    }

    private static Isoform newIsoform(String id) {

        Isoform isoform = new Isoform();
        isoform.setUniqueName(id);

        return isoform;
    }

    private static Entry newEntry(String id, List<Annotation> annotations) {

        Entry entry = new Entry(id);

        entry.setAnnotations(annotations);

        return entry;
    }
}