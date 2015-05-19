package org.nextprot.api.core.utils.peff;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.commons.constants.AnnotationApiModel;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.Isoform;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.domain.annotation.AnnotationIsoformSpecificity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by fnikitin on 05/05/15.
 */
public class IsoformPTMTest {

    @Test
    public void testGetModificationList() throws Exception {

        Annotation mod1 = newAnnotation("Phosphothreonine", "MOD:0001",
                AnnotationApiModel.MODIFIED_RESIDUE,
                new IsoformLocation("NX_P22694-1", 196, 196),
                new IsoformLocation("NX_P22694-2", 243, 243),
                new IsoformLocation("NX_P22694-3", 184, 184)
        );

        Annotation mod2 = newAnnotation("Phosphoserine", "MOD:0002",
                AnnotationApiModel.MODIFIED_RESIDUE,
                new IsoformLocation("NX_P22694-1", 339, 339),
                new IsoformLocation("NX_P22694-2", 386, 386),
                new IsoformLocation("NX_P22694-3", 327, 327)
        );

        Annotation mod3 = newAnnotation("Phosphothreonine", "MOD:0001",
                AnnotationApiModel.MODIFIED_RESIDUE,
                new IsoformLocation("NX_P22694-1", 198, 198),
                new IsoformLocation("NX_P22694-2", 245, 245),
                new IsoformLocation("NX_P22694-3", 186, 186)
        );

        Entry entry = newEntry("NX_P22694", Arrays.asList(mod1, mod2, mod3));

        List<IsoformPTM> mods = IsoformPTM.getListGenericPTM(entry, newIsoform("NX_P22694-1"));

        Assert.assertEquals("MOD:0001", mods.get(0).getModificationName());
        Assert.assertEquals(196, mods.get(0).getStart().getValue());
        Assert.assertEquals(196, mods.get(0).getEnd().getValue());

        Assert.assertEquals("MOD:0001", mods.get(1).getModificationName());
        Assert.assertEquals(198, mods.get(1).getStart().getValue());
        Assert.assertEquals(198, mods.get(1).getEnd().getValue());

        Assert.assertEquals("MOD:0002", mods.get(2).getModificationName());
        Assert.assertEquals(339, mods.get(2).getStart().getValue());
        Assert.assertEquals(339, mods.get(2).getEnd().getValue());
    }

    @Test
    public void testGetModifListAsPeff() throws Exception {

        Annotation mod1 = newAnnotation("Phosphothreonine", "MOD:0001",
                AnnotationApiModel.MODIFIED_RESIDUE,
                new IsoformLocation("NX_P22694-1", 196, 196),
                new IsoformLocation("NX_P22694-2", 243, 243),
                new IsoformLocation("NX_P22694-3", 184, 184)
        );

        Annotation mod2 = newAnnotation("Phosphoserine", "MOD:0002",
                AnnotationApiModel.MODIFIED_RESIDUE,
                new IsoformLocation("NX_P22694-1", 339, 339),
                new IsoformLocation("NX_P22694-2", 386, 386),
                new IsoformLocation("NX_P22694-3", 327, 327)
        );

        Annotation mod3 = newAnnotation("Phosphothreonine", "MOD:0001",
                AnnotationApiModel.MODIFIED_RESIDUE,
                new IsoformLocation("NX_P22694-1", 198, 198),
                new IsoformLocation("NX_P22694-2", 245, 245),
                new IsoformLocation("NX_P22694-3", 186, 186)
        );

        Entry entry = newEntry("NX_P22694", Arrays.asList(mod1, mod2, mod3));

        Assert.assertEquals("(196|MOD:0001)(198|MOD:0001)(339|MOD:0002)", IsoformPTM.getPsiPTMsAsPeffString(entry, newIsoform("NX_P22694-1")));
        Assert.assertEquals("(243|MOD:0001)(245|MOD:0001)(386|MOD:0002)", IsoformPTM.getPsiPTMsAsPeffString(entry, newIsoform("NX_P22694-2")));
        Assert.assertEquals("(184|MOD:0001)(186|MOD:0001)(327|MOD:0002)", IsoformPTM.getPsiPTMsAsPeffString(entry, newIsoform("NX_P22694-3")));
    }

    private static Isoform newIsoform(String id) {

        Isoform isoform = new Isoform();
        isoform.setUniqueName(id);

        return isoform;
    }

    private static Annotation newAnnotation(String cvterm, String cvtermCode, AnnotationApiModel type, IsoformLocation... isoformPositions) {

        Annotation annotation = new Annotation();
        annotation.setCategory(type.getDbAnnotationTypeName());

        annotation.setCvTermName(cvterm);
        annotation.setCvTermAccessionCode(cvtermCode);

        List<AnnotationIsoformSpecificity> specificityList = new ArrayList<>();

        for (IsoformLocation position : isoformPositions) {

            AnnotationIsoformSpecificity spec = new AnnotationIsoformSpecificity();

            spec.setIsoformName(position.getIsoformId());
            spec.setFirstPosition(position.getStart().getValue());
            spec.setLastPosition(position.getEnd().getValue());

            specificityList.add(spec);
        }
        annotation.setTargetingIsoforms(specificityList);

        return annotation;
    }

    private static Entry newEntry(String id, List<Annotation> annotations) {

        Entry entry = new Entry(id);

        entry.setAnnotations(annotations);

        return entry;
    }
}