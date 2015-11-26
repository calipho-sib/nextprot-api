package org.nextprot.api.core.utils;

import org.junit.Test;
import org.junit.Assert;
import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.commons.constants.PropertyApiModel;
import org.nextprot.api.commons.constants.PropertyWriter;
import org.nextprot.api.core.domain.Family;
import org.nextprot.api.core.test.base.CoreUnitBaseTest;

import java.util.Arrays;

public class NXVelocityUtilsTest extends CoreUnitBaseTest {

    @Test
    public void test_expected_property_behavior_for_uri_property() {
    	
    	PropertyWriter w = NXVelocityUtils.getTtlPropertyWriter(AnnotationCategory.EXPRESSION_PROFILE, PropertyApiModel.NAME_EXPRESSION_LEVEL);
    	Assert.assertTrue(w != null);
    	Assert.assertTrue(w.getName().equals("expressionLevel"));
    	Assert.assertTrue(w.formatValue("low").equals(":Low"));
    	Assert.assertTrue(w.getDataType().equals("string"));
    }
    
    @Test
    public void test_no_model_for_unexisting_annot_prop_association() {
    	
    	PropertyWriter w = NXVelocityUtils.getTtlPropertyWriter(AnnotationCategory.EXPRESSION_PROFILE, PropertyApiModel.NAME_INTERACTANT);
    	Assert.assertTrue(w == null);
    }

    @Test
    public void test_expected_property_behavior_for_literal_property() {
    	
    	PropertyWriter w = NXVelocityUtils.getTtlPropertyWriter(AnnotationCategory.PDB_MAPPING, PropertyApiModel.NAME_RESOLUTION);
    	Assert.assertTrue(w != null);
    	Assert.assertTrue(w.getName().equals("resolution"));
    	Assert.assertTrue(w.formatValue("18.4").equals("\"18.4\"^^xsd:double"));
    	Assert.assertTrue(w.getDataType().equals("double"));
    }

	@Test
	public void testGetFamilyHierarchyFromRoot() throws Exception {

		Family superfamily = new Family();
		superfamily.setName("ba superfamily");

		Family family = new Family();
		family.setName("be family");

		Family subfamily = new Family();
		subfamily.setName("bi subfamily");

		subfamily.setParent(family);
		family.setParent(superfamily);

		Assert.assertEquals(Arrays.asList(superfamily, family, subfamily), NXVelocityUtils.getFamilyHierarchyFromRoot(subfamily));
	}
}
