package org.nextprot.api.core.utils;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.nextprot.api.commons.constants.AnnotationApiModel;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.Isoform;
import org.nextprot.api.core.domain.IsoformEntityName;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.domain.annotation.AnnotationIsoformSpecificity;
import org.nextprot.api.core.domain.annotation.AnnotationVariant;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author fnikitin
 */
public class NXVelocityUtilsTest {

    @Test
    public void testFormatIsoformId1() throws Exception {

        Isoform isoform = new Isoform();

        Assert.assertEquals("Iso 1", NXVelocityUtils.formatIsoformId(isoform));
    }

    @Test
    public void testFormatIsoformId2() throws Exception {

        Isoform isoform = new Isoform();

        IsoformEntityName isoformEntityName = Mockito.mock(IsoformEntityName.class);
        Mockito.when(isoformEntityName.getValue()).thenReturn("M");

        isoform.setMainEntityName(isoformEntityName);

        Assert.assertEquals("M", NXVelocityUtils.formatIsoformId(isoform));
    }

    @Test
    public void testFormatIsoformId3() throws Exception {

        Isoform isoform = new Isoform();

        IsoformEntityName isoformEntityName = Mockito.mock(IsoformEntityName.class);
        Mockito.when(isoformEntityName.getValue()).thenReturn("2");

        isoform.setMainEntityName(isoformEntityName);

        Assert.assertEquals("Iso 2", NXVelocityUtils.formatIsoformId(isoform));
    }

    @Test
    public void testVariation() throws Exception {

        NXVelocityUtils.Variation variation = new NXVelocityUtils.Variation("Q", 32, 32);

        Assert.assertEquals("(32|32|Q)", variation.asPeff());
    }

    @Test
    public void testGetVariantList() throws Exception {

        Annotation variant1 = newVariant("R", "Q",
                new Position("NX_P22694-1", 106, 106),
                new Position("NX_P22694-2", 153, 153),
                new Position("NX_P22694-3", 94, 94)
        );

        Annotation variant2 = newVariant("T", "M",
                new Position("NX_P22694-1", 300, 300),
                new Position("NX_P22694-2", 347, 347),
                new Position("NX_P22694-3", 288, 288)
        );

        Annotation variant3 = newVariant("A", "P",
                new Position("NX_P22694-1", 26, 26)
        );

        Entry entry = newEntry("NX_P22694", Arrays.asList(variant1, variant2, variant3));

        List<NXVelocityUtils.Variation> variations = NXVelocityUtils.getListVariant(entry, newIsoform("NX_P22694-1"));

        Assert.assertEquals("P", variations.get(0).getVariant());
        Assert.assertEquals(26, variations.get(0).getStart());
        Assert.assertEquals(26, variations.get(0).getEnd());

        Assert.assertEquals("Q", variations.get(1).getVariant());
        Assert.assertEquals(106, variations.get(1).getStart());
        Assert.assertEquals(106, variations.get(1).getEnd());

        Assert.assertEquals("M", variations.get(2).getVariant());
        Assert.assertEquals(300, variations.get(2).getStart());
        Assert.assertEquals(300, variations.get(2).getEnd());
    }

    @Test
    public void testGetVariantListAsPeff() throws Exception {

        Annotation variant1 = newVariant("R", "Q",
                new Position("NX_P22694-1", 106, 106),
                new Position("NX_P22694-2", 153, 153),
                new Position("NX_P22694-3", 94, 94)
        );

        Annotation variant2 = newVariant("T", "M",
                new Position("NX_P22694-1", 300, 300),
                new Position("NX_P22694-2", 347, 347),
                new Position("NX_P22694-3", 288, 288)
        );

        Annotation variant3 = newVariant("A", "P",
                new Position("NX_P22694-1", 26, 26)
        );

        Entry entry = newEntry("NX_P22694", Arrays.asList(variant1, variant2, variant3));

        Assert.assertEquals("(26|26|P)(106|106|Q)(300|300|M)", NXVelocityUtils.getVariantsAsPeffString(entry, newIsoform("NX_P22694-1")));
        Assert.assertEquals("(153|153|Q)(347|347|M)", NXVelocityUtils.getVariantsAsPeffString(entry, newIsoform("NX_P22694-2")));
        Assert.assertEquals("(94|94|Q)(288|288|M)", NXVelocityUtils.getVariantsAsPeffString(entry, newIsoform("NX_P22694-3")));
    }

    private static Isoform newIsoform(String id) {

        Isoform isoform = new Isoform();
        isoform.setUniqueName(id);

        return isoform;
    }

    private static Annotation newVariant(String ori, String var, Position... isoformPositions) {

        Annotation variant = new Annotation();
        variant.setCategory(AnnotationApiModel.VARIANT.getDbAnnotationTypeName());

        variant.setVariant(new AnnotationVariant(ori, var, ""));

        List<AnnotationIsoformSpecificity> specificityList = new ArrayList<>();

        for (Position position : isoformPositions) {

            AnnotationIsoformSpecificity spec = new AnnotationIsoformSpecificity();

            spec.setIsoformName(position.getIsoformId());
            spec.setFirstPosition(position.getStart());
            spec.setLastPosition(position.getEnd());

            specificityList.add(spec);
        }
        variant.setTargetingIsoforms(specificityList);

        return variant;
    }

    private static Entry newEntry(String id, List<Annotation> annotations) {

        Entry entry = new Entry(id);

        entry.setAnnotations(annotations);

        return entry;
    }

    private static class Position {

        private final String isoformId;
        private final int start;
        private final int end;

        Position(String isoformId, int start, int end) {

            this.isoformId = isoformId;
            this.start = start;
            this.end = end;
        }

        public String getIsoformId() {
            return isoformId;
        }

        public int getStart() {
            return start;
        }

        public int getEnd() {
            return end;
        }
    }
}