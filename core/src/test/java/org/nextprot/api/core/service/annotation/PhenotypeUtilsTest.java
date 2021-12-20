package org.nextprot.api.core.service.annotation;

import com.google.common.collect.ImmutableList;
import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.test.base.CoreUnitBaseTest;

import java.util.Arrays;
import java.util.Collections;

import static org.nextprot.api.commons.constants.AnnotationCategory.PHENOTYPIC_VARIATION;
import static org.nextprot.api.commons.constants.AnnotationCategory.VARIANT;

public class PhenotypeUtilsTest extends CoreUnitBaseTest {
    
    @Test
    public void testUpdatePhenotypicEffectProperty_singleVariant() {
        // If a variant is related to one and only one phenotype annotation, the effect is displayed
        Annotation vpAnnot = new Annotation();
        vpAnnot.setAnnotationCategory(PHENOTYPIC_VARIATION);
        vpAnnot.setSubjectComponents(Collections.singletonList("ID1"));
        vpAnnot.setDescription("(ATM-p.Asp126Glu) decreases mitotic G1 DNA damage checkpoint signaling") ;
        
        Annotation variantAnnot = new Annotation();
        variantAnnot.setAnnotationCategory(VARIANT);
        variantAnnot.setAnnotationHash("ID1");
    
        PhenotypeUtils.updatePhenotypicEffectProperty(Arrays.asList(variantAnnot, vpAnnot), "NX_Q13315");
    
        Assert.assertEquals("Decreases mitotic G1 DNA damage checkpoint signaling",
                variantAnnot.getPropertiesByKey("phenotypic effect").iterator().next().getValue());
    }
    
    @Test
    public void testUpdatePhenotypicEffectProperty_doubleVariant() {
        // If variant is related to one and only one phenotype annotation but in association with another variant,
        // the label displayed in the sequence page for the effect will contain "when associated with another variant".
        Annotation vpAnnot = new Annotation();
        vpAnnot.setAnnotationCategory(PHENOTYPIC_VARIATION);
        vpAnnot.setSubjectComponents(ImmutableList.of("ID1", "ID2"));
        vpAnnot.setDescription("(ATM-p.Asp126Glu) decreases mitotic G1 DNA damage checkpoint signaling") ;
        
        Annotation variantAnnot = new Annotation();
        variantAnnot.setAnnotationCategory(VARIANT);
        variantAnnot.setAnnotationHash("ID1");
        
        PhenotypeUtils.updatePhenotypicEffectProperty(Arrays.asList(variantAnnot, vpAnnot), "NX_Q13315");
        
        Assert.assertEquals("Decreases mitotic G1 DNA damage checkpoint signaling when associated with another variant",
                variantAnnot.getPropertiesByKey("phenotypic effect").iterator().next().getValue());
    }
    
    @Test
    public void testUpdatePhenotypicEffectProperty_singleAndDoubleVariant() {
        // If a variant is related to several phenotype annotations, "Has effects" is displayed
        Annotation vpAnnot1 = new Annotation();
        vpAnnot1.setAnnotationCategory(PHENOTYPIC_VARIATION);
        vpAnnot1.setSubjectComponents(ImmutableList.of("ID1", "ID2"));
        vpAnnot1.setDescription("(ATM-p.Asp126Glu) decreases mitotic G1 DNA damage checkpoint signaling") ;
        Annotation vpAnnot2 = new Annotation();
        vpAnnot2.setAnnotationCategory(PHENOTYPIC_VARIATION);
        vpAnnot2.setSubjectComponents(ImmutableList.of("ID1", "ID2"));
        vpAnnot2.setDescription("(ATM-p.Asp126Glu) decreases blablabla") ;
    
        Annotation variantAnnot = new Annotation();
        variantAnnot.setAnnotationCategory(VARIANT);
        variantAnnot.setAnnotationHash("ID1");
        
        PhenotypeUtils.updatePhenotypicEffectProperty(Arrays.asList(variantAnnot, vpAnnot1, vpAnnot2), "NX_Q13315");
        
        Assert.assertEquals("Has effects",
                variantAnnot.getPropertiesByKey("phenotypic effect").iterator().next().getValue());
    }
}