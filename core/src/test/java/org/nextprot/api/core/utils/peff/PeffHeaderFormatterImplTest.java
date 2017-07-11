package org.nextprot.api.core.utils.peff;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.Isoform;
import org.nextprot.api.core.domain.Overview;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.domain.annotation.AnnotationVariant;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

public class PeffHeaderFormatterImplTest {

    ///// Peff \Processed key
    @Test
    public void testMatureProteinAnnotation() throws Exception {

        Entry entry = newMockEntry("NX_A0A087X1C5",
                Collections.singletonList(newMockAnnotation(17, 609, AnnotationCategory.MATURE_PROTEIN)));
        Isoform isoform = newMockIsoform("NX_A0A087X1C5-1");

        PeffHeaderFormatter formatter = new PeffHeaderFormatterImpl(entry, isoform);

        String peff = formatter.format();

        Assert.assertEquals("\\Processed=(17|609|CHAIN)", peff);
    }

    @Test
    public void testSignalPeptideAnnotation() throws Exception {

        Entry entry = newMockEntry("NX_A0A087X1C5",
                Collections.singletonList(newMockAnnotation(1, 29, AnnotationCategory.SIGNAL_PEPTIDE)));
        Isoform isoform = newMockIsoform("NX_A0A087X1C5-1");

        PeffHeaderFormatter formatter = new PeffHeaderFormatterImpl(entry, isoform);

        String peff = formatter.format();

        Assert.assertEquals("\\Processed=(1|29|SIGNAL)", peff);
    }

    @Test
    public void testMaturationPeptideAnnotation() throws Exception {

        Entry entry = newMockEntry("NX_A0A087X1C5",
                Collections.singletonList(newMockAnnotation(20, 213, AnnotationCategory.MATURATION_PEPTIDE)));
        Isoform isoform = newMockIsoform("NX_A0A087X1C5-1");

        PeffHeaderFormatter formatter = new PeffHeaderFormatterImpl(entry, isoform);

        String peff = formatter.format();

        Assert.assertEquals("\\Processed=(20|213|PROPEP)", peff);
    }

    @Test
    public void testMitochondrialTransitPeptideAnnotation() throws Exception {

        Entry entry = newMockEntry("NX_A0A087X1C5",
                Collections.singletonList(newMockAnnotation(1, 19, AnnotationCategory.MITOCHONDRIAL_TRANSIT_PEPTIDE)));
        Isoform isoform = newMockIsoform("NX_A0A087X1C5-1");

        PeffHeaderFormatter formatter = new PeffHeaderFormatterImpl(entry, isoform);

        String peff = formatter.format();

        Assert.assertEquals("\\Processed=(1|19|TRANSIT)", peff);
    }

    @Test
    public void testPeroxisomeTransitPeptideAnnotation() throws Exception {

        Entry entry = newMockEntry("NX_A0A087X1C5",
                Collections.singletonList(newMockAnnotation(1, 19, AnnotationCategory.PEROXISOME_TRANSIT_PEPTIDE)));
        Isoform isoform = newMockIsoform("NX_A0A087X1C5-1");

        PeffHeaderFormatter formatter = new PeffHeaderFormatterImpl(entry, isoform);

        String peff = formatter.format();

        Assert.assertEquals("\\Processed=(1|19|TRANSIT)", peff);
    }

    ///// Peff \ModRes key
    @Test
    public void testDisulfideBondAnnotation() throws Exception {

        Entry entry = newMockEntry("NX_A0A087X1C5",
                Collections.singletonList(newMockAnnotation(31, 96, AnnotationCategory.DISULFIDE_BOND)));
        Isoform isoform = newMockIsoform("NX_A0A087X1C5-1");

        PeffHeaderFormatter formatter = new PeffHeaderFormatterImpl(entry, isoform);

        String peff = formatter.format();

        Assert.assertEquals("\\ModRes=(31|Disulfide)(96|Disulfide)", peff);
    }

    @Test
    public void testGlycoAnnotation() throws Exception {

        Entry entry = newMockEntry("NX_A0A087X1C5",
                Collections.singletonList(newMockAnnotation(31, 96, AnnotationCategory.GLYCOSYLATION_SITE, "N-linked (GlcNAc...)")));
        Isoform isoform = newMockIsoform("NX_A0A087X1C5-1");

        PeffHeaderFormatter formatter = new PeffHeaderFormatterImpl(entry, isoform);

        String peff = formatter.format();

        Assert.assertEquals("\\ModRes=(31|N-linked (GlcNAc...))", peff);
    }

    ///// Peff \ModResPsi key
    @Test
    public void testPsiModAnnotation() throws Exception {

        Entry entry = newMockEntry("NX_A0A087X1C5",
                Collections.singletonList(newMockAnnotation(31, 31, AnnotationCategory.MODIFIED_RESIDUE, null, "MOD:0001")));

        Isoform isoform = newMockIsoform("NX_A0A087X1C5-1");

        PeffHeaderFormatter formatter = new PeffHeaderFormatterImpl(entry, isoform);

        String peff = formatter.format();

        Assert.assertEquals("\\ModResPsi=(31|MOD:0001)", peff);
    }

    @Test
    public void testPsiModAnnotation2() throws Exception {

        Annotation mod1 = newMockAnnotation(196, 196, AnnotationCategory.MODIFIED_RESIDUE, "Phosphothreonine", "MOD:0001");
        Annotation mod2 = newMockAnnotation(339, 339, AnnotationCategory.MODIFIED_RESIDUE, "Phosphoserine", "MOD:0002");
        Annotation mod3 = newMockAnnotation(198, 198, AnnotationCategory.MODIFIED_RESIDUE, "Phosphothreonine", "MOD:0003");

        Entry entry = newMockEntry("NX_P22694", Arrays.asList(mod1, mod2, mod3));
        Isoform isoform = newMockIsoform("NX_P22694-1");

        PeffHeaderFormatter formatter = new PeffHeaderFormatterImpl(entry, isoform);

        String peff = formatter.format();

        Assert.assertEquals("\\ModResPsi=(196|MOD:0001)(198|MOD:0003)(339|MOD:0002)", peff);
    }

    ///// Peff \Variant key
    @Test
    public void testVariantAnnotation() throws Exception {

        Entry entry = newMockEntry("NX_P22694",
                Arrays.asList(
                        newMockVariantAnnotation(106, 106, "R", "Q"),
                        newMockVariantAnnotation(300, 300, "T", "M"),
                        newMockVariantAnnotation(26, 26, "A", "P")
                ));
        Isoform isoform = newMockIsoform("NX_P22694-1");

        PeffHeaderFormatter formatter = new PeffHeaderFormatterImpl(entry, isoform);

        String peff = formatter.format();

        Assert.assertEquals("\\Variant=(26|26|P)(106|106|Q)(300|300|M)", peff);
    }

    @Test
    public void testMultipleAnnotations() throws Exception {

        Entry entry = newMockEntry("NX_P22694", Arrays.asList(
                newMockAnnotation(1, 515, AnnotationCategory.MATURE_PROTEIN),
                newMockAnnotation(416, 416, AnnotationCategory.GLYCOSYLATION_SITE, "N-linked (GlcNAc...)"),
                newMockVariantAnnotation(311, 311, "R", "L"),
                newMockVariantAnnotation(70, 70, "T", "N"),
                newMockVariantAnnotation(337, 337, "A", "CS"),
                newMockVariantAnnotation(369, 373, "A", "VHMPY"),
                newMockVariantAnnotation(383, 383, "A", "R"),
                newMockVariantAnnotation(428, 428, "A", "E")
        ));
        Isoform isoform = newMockIsoform("NX_P22694-1");

        PeffHeaderFormatter formatter = new PeffHeaderFormatterImpl(entry, isoform);

        String peff = formatter.format();

        Assert.assertEquals("\\ModRes=(416|N-linked (GlcNAc...)) \\Variant=(70|70|N)(311|311|L)(337|337|CS)(369|373|VHMPY)(383|383|R)(428|428|E) \\Processed=(1|515|CHAIN)", peff);
    }

    private static Isoform newMockIsoform(String name) {

        Isoform isoform = Mockito.mock(Isoform.class);

        when(isoform.getUniqueName()).thenReturn(name);

        return isoform;
    }

    private static Entry newMockEntry(String name, List<Annotation> annotations) {

        Entry entry = Mockito.mock(Entry.class);

        when(entry.getUniqueName()).thenReturn(name);
        when(entry.getAnnotationsByIsoform(anyString())).thenReturn(annotations);
        when(entry.getOverview()).thenReturn(new Overview());

        return entry;
    }

    private static Annotation newMockAnnotation(int from, int to, AnnotationCategory model) {

        return newMockAnnotation(from, to, model, null, null);
    }

    private static Annotation newMockAnnotation(int from, int to, AnnotationCategory model, String cvterm) {

        return newMockAnnotation(from, to, model, cvterm, null);
    }

    private static Annotation newMockAnnotation(int from, int to, AnnotationCategory model, String cvterm, String cvtermcode) {

        Annotation annotation = Mockito.mock(Annotation.class);

        when(annotation.getStartPositionForIsoform(Mockito.anyString())).thenReturn(from);
        when(annotation.getEndPositionForIsoform(Mockito.anyString())).thenReturn(to);
        when(annotation.getAPICategory()).thenReturn(model);
        if (cvterm != null)
            when(annotation.getCvTermName()).thenReturn(cvterm);
        if (cvtermcode != null)
            when(annotation.getCvTermAccessionCode()).thenReturn(cvtermcode);

        return annotation;
    }

    private static Annotation newMockVariantAnnotation(int from, int to, String ori, String var) {

        Annotation annotation = Mockito.mock(Annotation.class);

        when(annotation.getStartPositionForIsoform(Mockito.anyString())).thenReturn(from);
        when(annotation.getEndPositionForIsoform(Mockito.anyString())).thenReturn(to);
        when(annotation.getAPICategory()).thenReturn(AnnotationCategory.VARIANT);
        when(annotation.getVariant()).thenReturn(new AnnotationVariant(ori, var, ""));

        return annotation;
    }
}