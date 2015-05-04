package org.nextprot.api.core.utils;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.nextprot.api.core.domain.Isoform;
import org.nextprot.api.core.domain.IsoformEntityName;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.domain.annotation.AnnotationIsoformSpecificity;
import org.nextprot.api.core.domain.annotation.AnnotationVariant;

import java.util.ArrayList;
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
    public void testGetVariantAsPeff() throws Exception {

        Annotation variant = newVariant("R", "Q", new Position("NX_P22694-1", 32, 32));

        Assert.assertEquals("(32|32|Q)", NXVelocityUtils.getVariantAsPeff(newIsoform("NX_P22694-1"), variant));
    }

    @Test
    public void testGetVariantAsPeff2() throws Exception {

        Annotation variant = newVariant("R", "Q",
                new Position("NX_P22694-1", 300, 300),
                new Position("NX_P22694-2", 347, 347),
                new Position("NX_P22694-3", 288, 288)
        );

        Assert.assertEquals("(300|300|Q)", NXVelocityUtils.getVariantAsPeff(newIsoform("NX_P22694-1"), variant));
        Assert.assertEquals("(347|347|Q)", NXVelocityUtils.getVariantAsPeff(newIsoform("NX_P22694-2"), variant));
        Assert.assertEquals("(288|288|Q)", NXVelocityUtils.getVariantAsPeff(newIsoform("NX_P22694-3"), variant));
    }

    private static Isoform newIsoform(String id) {

        Isoform isoform = new Isoform();
        isoform.setUniqueName(id);

        return isoform;
    }

    private static Annotation newVariant(String ori, String var, Position... isoformPositions) {

        Annotation variant = new Annotation();

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