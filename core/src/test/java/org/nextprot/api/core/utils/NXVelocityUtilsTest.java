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
import org.nextprot.api.core.utils.peff.Modification;
import org.nextprot.api.core.utils.peff.Variation;

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
    public void testGetVariantList() throws Exception {

        Annotation variant1 = newVariant("R", "Q",
                new SimplePosition("NX_P22694-1", 106, 106),
                new SimplePosition("NX_P22694-2", 153, 153),
                new SimplePosition("NX_P22694-3", 94, 94)
        );

        Annotation variant2 = newVariant("T", "M",
                new SimplePosition("NX_P22694-1", 300, 300),
                new SimplePosition("NX_P22694-2", 347, 347),
                new SimplePosition("NX_P22694-3", 288, 288)
        );

        Annotation variant3 = newVariant("A", "P",
                new SimplePosition("NX_P22694-1", 26, 26)
        );

        Entry entry = newEntry("NX_P22694", Arrays.asList(variant1, variant2, variant3));

        List<Variation> variations = NXVelocityUtils.getListVariant(entry, newIsoform("NX_P22694-1"));

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
                new SimplePosition("NX_P22694-1", 106, 106),
                new SimplePosition("NX_P22694-2", 153, 153),
                new SimplePosition("NX_P22694-3", 94, 94)
        );

        Annotation variant2 = newVariant("T", "M",
                new SimplePosition("NX_P22694-1", 300, 300),
                new SimplePosition("NX_P22694-2", 347, 347),
                new SimplePosition("NX_P22694-3", 288, 288)
        );

        Annotation variant3 = newVariant("A", "P",
                new SimplePosition("NX_P22694-1", 26, 26)
        );

        Entry entry = newEntry("NX_P22694", Arrays.asList(variant1, variant2, variant3));

        Assert.assertEquals("(26|26|P)(106|106|Q)(300|300|M)", NXVelocityUtils.getVariantsAsPeffString(entry, newIsoform("NX_P22694-1")));
        Assert.assertEquals("(153|153|Q)(347|347|M)", NXVelocityUtils.getVariantsAsPeffString(entry, newIsoform("NX_P22694-2")));
        Assert.assertEquals("(94|94|Q)(288|288|M)", NXVelocityUtils.getVariantsAsPeffString(entry, newIsoform("NX_P22694-3")));
    }

    @Test
    public void testGetModificationList() throws Exception {

        Annotation mod1 = newModification("Phosphothreonine",
                AnnotationApiModel.MODIFIED_RESIDUE,
                new SimplePosition("NX_P22694-1", 196, 196),
                new SimplePosition("NX_P22694-2", 243, 243),
                new SimplePosition("NX_P22694-3", 184, 184)
        );

        Annotation mod2 = newModification("Phosphoserine",
                AnnotationApiModel.MODIFIED_RESIDUE,
                new SimplePosition("NX_P22694-1", 339, 339),
                new SimplePosition("NX_P22694-2", 386, 386),
                new SimplePosition("NX_P22694-3", 327, 327)
        );

        Annotation mod3 = newModification("Phosphothreonine",
                AnnotationApiModel.MODIFIED_RESIDUE,
                new SimplePosition("NX_P22694-1", 198, 198),
                new SimplePosition("NX_P22694-2", 245, 245),
                new SimplePosition("NX_P22694-3", 186, 186)
        );

        Entry entry = newEntry("NX_P22694", Arrays.asList(mod1, mod2, mod3));

        List<Modification> mods = NXVelocityUtils.getListGenericPTM(entry, newIsoform("NX_P22694-1"));

        Assert.assertEquals("Phosphothreonine", mods.get(0).getModificationName());
        Assert.assertEquals(196, mods.get(0).getStart());
        Assert.assertEquals(196, mods.get(0).getEnd());

        Assert.assertEquals("Phosphothreonine", mods.get(1).getModificationName());
        Assert.assertEquals(198, mods.get(1).getStart());
        Assert.assertEquals(198, mods.get(1).getEnd());

        Assert.assertEquals("Phosphoserine", mods.get(2).getModificationName());
        Assert.assertEquals(339, mods.get(2).getStart());
        Assert.assertEquals(339, mods.get(2).getEnd());
    }

    @Test
    public void testGetModifListAsPeff() throws Exception {

        Annotation mod1 = newModification("Phosphothreonine",
                AnnotationApiModel.MODIFIED_RESIDUE,
                new SimplePosition("NX_P22694-1", 196, 196),
                new SimplePosition("NX_P22694-2", 243, 243),
                new SimplePosition("NX_P22694-3", 184, 184)
        );

        Annotation mod2 = newModification("Phosphoserine",
                AnnotationApiModel.MODIFIED_RESIDUE,
                new SimplePosition("NX_P22694-1", 339, 339),
                new SimplePosition("NX_P22694-2", 386, 386),
                new SimplePosition("NX_P22694-3", 327, 327)
        );

        Annotation mod3 = newModification("Phosphothreonine",
                AnnotationApiModel.MODIFIED_RESIDUE,
                new SimplePosition("NX_P22694-1", 198, 198),
                new SimplePosition("NX_P22694-2", 245, 245),
                new SimplePosition("NX_P22694-3", 186, 186)
        );

        Entry entry = newEntry("NX_P22694", Arrays.asList(mod1, mod2, mod3));

        Assert.assertEquals("(196|Phosphothreonine)(198|Phosphothreonine)(339|Phosphoserine)", NXVelocityUtils.getGenericPTMsAsPeffString(entry, newIsoform("NX_P22694-1")));
        Assert.assertEquals("(243|Phosphothreonine)(245|Phosphothreonine)(386|Phosphoserine)", NXVelocityUtils.getGenericPTMsAsPeffString(entry, newIsoform("NX_P22694-2")));
        Assert.assertEquals("(184|Phosphothreonine)(186|Phosphothreonine)(327|Phosphoserine)", NXVelocityUtils.getGenericPTMsAsPeffString(entry, newIsoform("NX_P22694-3")));
    }

    private static Isoform newIsoform(String id) {

        Isoform isoform = new Isoform();
        isoform.setUniqueName(id);

        return isoform;
    }

    private static Annotation newVariant(String ori, String var, SimplePosition... isoformPositions) {

        Annotation variant = new Annotation();
        variant.setCategory(AnnotationApiModel.VARIANT.getDbAnnotationTypeName());

        variant.setVariant(new AnnotationVariant(ori, var, ""));

        List<AnnotationIsoformSpecificity> specificityList = new ArrayList<>();

        for (SimplePosition position : isoformPositions) {

            AnnotationIsoformSpecificity spec = new AnnotationIsoformSpecificity();

            spec.setIsoformName(position.getIsoformId());
            spec.setFirstPosition(position.getStart());
            spec.setLastPosition(position.getEnd());

            specificityList.add(spec);
        }
        variant.setTargetingIsoforms(specificityList);

        return variant;
    }

    private static Annotation newModification(String modName, AnnotationApiModel type, SimplePosition... isoformPositions) {

        Annotation modification = new Annotation();
        modification.setCategory(type.getDbAnnotationTypeName());

        modification.setCvTermName(modName);

        List<AnnotationIsoformSpecificity> specificityList = new ArrayList<>();

        for (SimplePosition position : isoformPositions) {

            AnnotationIsoformSpecificity spec = new AnnotationIsoformSpecificity();

            spec.setIsoformName(position.getIsoformId());
            spec.setFirstPosition(position.getStart());
            spec.setLastPosition(position.getEnd());

            specificityList.add(spec);
        }
        modification.setTargetingIsoforms(specificityList);

        return modification;
    }

    private static Entry newEntry(String id, List<Annotation> annotations) {

        Entry entry = new Entry(id);

        entry.setAnnotations(annotations);

        return entry;
    }

    private static class SimplePosition {

        private final String isoformId;
        private final int start;
        private final int end;

        SimplePosition(String isoformId, int start, int end) {

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