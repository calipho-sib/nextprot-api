package org.nextprot.api.core.utils.peff;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.nextprot.api.core.domain.annotation.Annotation;

import static org.mockito.Mockito.when;

/**
 * Created by fnikitin on 05/05/15.
 */
public class DisulfideTest {

    @Test
    public void testAsPeff() throws Exception {

        Disulfide mod = new Disulfide("spongebob", newMockAnnotation(31, 96));

        Assert.assertEquals("(31|Disulfide)(96|Disulfide)", mod.asPeff());
    }

    private static Annotation newMockAnnotation(int from, int to) {

        Annotation annotation = Mockito.mock(Annotation.class);

        when(annotation.getStartPositionForIsoform(Mockito.anyString())).thenReturn(from);
        when(annotation.getEndPositionForIsoform(Mockito.anyString())).thenReturn(to);
        when(annotation.getCvTermName()).thenReturn("Disulfide");

        return annotation;
    }
}