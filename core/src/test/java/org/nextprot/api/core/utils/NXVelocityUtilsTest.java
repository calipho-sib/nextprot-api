package org.nextprot.api.core.utils;

import org.junit.Test;
import org.apache.commons.collections.bag.SynchronizedSortedBag;
import org.apache.poi.util.SystemOutLogger;
import org.junit.Assert;
import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.commons.constants.PropertyApiModel;
import org.nextprot.api.commons.constants.PropertyWriter;
import org.nextprot.api.core.domain.Family;
import org.nextprot.api.core.test.base.CoreUnitBaseTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class NXVelocityUtilsTest extends CoreUnitBaseTest {

	
    @Test
    public void test1() {
    	Map<String,String> m = new HashMap<>();
    	m.put("a", "a value");
    	m.put("b", "b value");
    	m.put("c", "c value");
    	Set<String> s = new HashSet<>(m.keySet());
    	s.remove("b");
    	m.keySet().stream().forEach(k -> System.out.println(k + "=" + m.get(k)));
    }
	
    @Test
    public void test_expected_property_behavior_for_uri_property() {
    	
    	PropertyWriter w = NXVelocityUtils.getTtlPropertyWriter(AnnotationCategory.EXPRESSION_PROFILE, PropertyApiModel.NAME_EXPRESSION_LEVEL);
    	Assert.assertTrue(w != null);
    	Assert.assertTrue(w.getName().equals("observedExpression"));
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
