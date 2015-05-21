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
public class IsoformProcessingProductTest {

    @Test
    public void testSignalPeptideAsPeff() throws Exception {

        IsoformProcessingProduct mod = new IsoformProcessingProduct("spongebob", newMockAnnotation(1, 29, AnnotationApiModel.SIGNAL_PEPTIDE));

        Assert.assertEquals("(1|29|SIGNAL)", mod.asPeff());
    }

    @Test
    public void testMaturationPeptideAsPeff() throws Exception {

        IsoformProcessingProduct mod = new IsoformProcessingProduct("spongebob", newMockAnnotation(20, 213, AnnotationApiModel.MATURATION_PEPTIDE));

        Assert.assertEquals("(20|213|PROPEP)", mod.asPeff());
    }

    @Test
    public void testMatureProteinAsPeff() throws Exception {

        IsoformProcessingProduct mod = new IsoformProcessingProduct("spongebob", newMockAnnotation(17, 609, AnnotationApiModel.MATURE_PROTEIN));

        Assert.assertEquals("(17|609|CHAIN)", mod.asPeff());
    }

    @Test
    public void testMitochondrialTransitPeptideAsPeff() throws Exception {

        IsoformProcessingProduct mod = new IsoformProcessingProduct("spongebob", newMockAnnotation(1, 19, AnnotationApiModel.MITOCHONDRIAL_TRANSIT_PEPTIDE));

        Assert.assertEquals("(1|19|TRANSIT)", mod.asPeff());
    }

    @Test
    public void testPeroxisomeTransitPeptideAsPeff() throws Exception {

        IsoformProcessingProduct mod = new IsoformProcessingProduct("spongebob", newMockAnnotation(1, 19, AnnotationApiModel.PEROXISOME_TRANSIT_PEPTIDE));

        Assert.assertEquals("(1|19|TRANSIT)", mod.asPeff());
    }

    private static Annotation newMockAnnotation(int from, int to, AnnotationApiModel model) {

        Annotation annotation = Mockito.mock(Annotation.class);

        when(annotation.getStartPositionForIsoform(Mockito.anyString())).thenReturn(from);
        when(annotation.getEndPositionForIsoform(Mockito.anyString())).thenReturn(to);
        when(annotation.getAPICategory()).thenReturn(model);

        return annotation;
    }
}