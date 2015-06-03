package org.nextprot.api.core.utils.peff;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.nextprot.api.commons.constants.AnnotationApiModel;
import org.nextprot.api.core.domain.annotation.Annotation;

import static org.mockito.Mockito.when;

/**
 * Created by fnikitin on 07/05/15.
 */
public class IsoformPTMNoPsiPeffFormatterTest {

    @Test
    public void testDisulfideBondAsPeff() throws Exception {

        DisulfideBondPeffFormatter mod = new DisulfideBondPeffFormatter("spongebob", newMockAnnotation(31, 96, AnnotationApiModel.DISULFIDE_BOND, ""));

        Assert.assertEquals("(31|Disulfide)(96|Disulfide)", mod.asPeff());
    }

    @Test
    public void testGlycoAsPeff() throws Exception {

        IsoformPTMNoPsiPeffFormatter mod = new IsoformPTMNoPsiPeffFormatter("spongebob", newMockAnnotation(31, 96, AnnotationApiModel.GLYCOSYLATION_SITE, "N-linked (GlcNAc...)"));

        Assert.assertEquals("(31|N-linked (GlcNAc...))", mod.asPeff());
    }

    private static Annotation newMockAnnotation(int from, int to, AnnotationApiModel model, String cvterm) {

        Annotation annotation = Mockito.mock(Annotation.class);

        when(annotation.getStartPositionForIsoform(Mockito.anyString())).thenReturn(from);
        when(annotation.getEndPositionForIsoform(Mockito.anyString())).thenReturn(to);
        when(annotation.getCvTermName()).thenReturn(cvterm);
        when(annotation.getAPICategory()).thenReturn(model);

        return annotation;
    }
}