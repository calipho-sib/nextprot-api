package org.nextprot.api.core.dao;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.test.base.CoreUnitBaseTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.transaction.TransactionConfiguration;

import java.util.List;

@DatabaseSetup(value = "AntibodyMappingDaoTest.xml", type = DatabaseOperation.INSERT)
@TransactionConfiguration(defaultRollback = true)
public class AntibodyMappingDaoTest extends CoreUnitBaseTest {

	@Autowired
	private AntibodyMappingDao antibodyMappingDao;

	@Test
	public void testFindAntibodiesById() {
		List<Annotation> annotations = this.antibodyMappingDao.findAntibodyMappingAnnotationsById(636535L);
		Assert.assertEquals(2, annotations.size());

		Annotation annotation = annotations.get(0);

		Assert.assertEquals(AnnotationCategory.ANTIBODY_MAPPING, annotation.getAPICategory());
		Assert.assertEquals("AN_HPA036302_4049518", annotation.getUniqueName());
		Assert.assertEquals("GOLD", annotation.getQualityQualifier());
		Assert.assertEquals(4049518, annotation.getAnnotationId());
		Assert.assertEquals(1, annotation.getEvidences().size());
		Assert.assertEquals(4049518, annotation.getEvidences().get(0).getAnnotationId());
		Assert.assertEquals(17201575, annotation.getEvidences().get(0).getResourceId());
		Assert.assertEquals("database", annotation.getEvidences().get(0).getResourceType());
		Assert.assertEquals("Human protein atlas", annotation.getEvidences().get(0).getAssignedBy());
		Assert.assertEquals("HPA", annotation.getEvidences().get(0).getResourceDb());
		Assert.assertEquals("evidence", annotation.getEvidences().get(0).getResourceAssociationType());
		Assert.assertEquals("ECO:0000154", annotation.getEvidences().get(0).getEvidenceCodeAC());
		Assert.assertTrue(!annotation.getEvidences().get(0).isNegativeEvidence());

		Assert.assertEquals(2, annotation.getTargetingIsoformsMap().size());
		Assert.assertTrue(annotation.getTargetingIsoformsMap().containsKey("NX_P06213-1"));
		Assert.assertTrue(annotation.getTargetingIsoformsMap().containsKey("NX_P06213-2"));
		Assert.assertEquals(608, annotation.getTargetingIsoformsMap().get("NX_P06213-1").getFirstPosition().intValue());
		Assert.assertEquals(608, annotation.getTargetingIsoformsMap().get("NX_P06213-2").getFirstPosition().intValue());
		Assert.assertEquals(742, annotation.getTargetingIsoformsMap().get("NX_P06213-1").getLastPosition().intValue());
		Assert.assertEquals(742, annotation.getTargetingIsoformsMap().get("NX_P06213-2").getLastPosition().intValue());

		annotation = annotations.get(1);

		Assert.assertEquals(AnnotationCategory.ANTIBODY_MAPPING, annotation.getAPICategory());
		Assert.assertEquals("AN_HPA036303_9547085", annotation.getUniqueName());
		Assert.assertEquals("GOLD", annotation.getQualityQualifier());
		Assert.assertEquals(9547085, annotation.getAnnotationId());
		Assert.assertEquals(1, annotation.getEvidences().size());
		Assert.assertEquals(9547085, annotation.getEvidences().get(0).getAnnotationId());
		Assert.assertEquals(39235676, annotation.getEvidences().get(0).getResourceId());
		Assert.assertEquals("database", annotation.getEvidences().get(0).getResourceType());
		Assert.assertEquals("HPA", annotation.getEvidences().get(0).getResourceDb());
		Assert.assertEquals("Human protein atlas", annotation.getEvidences().get(0).getAssignedBy());
		Assert.assertEquals("evidence", annotation.getEvidences().get(0).getResourceAssociationType());
		Assert.assertEquals("ECO:0000154", annotation.getEvidences().get(0).getEvidenceCodeAC());
		Assert.assertTrue(!annotation.getEvidences().get(0).isNegativeEvidence());

		Assert.assertEquals(2, annotation.getTargetingIsoformsMap().size());
		Assert.assertTrue(annotation.getTargetingIsoformsMap().containsKey("NX_P06213-1"));
		Assert.assertTrue(annotation.getTargetingIsoformsMap().containsKey("NX_P06213-2"));
		Assert.assertEquals(246, annotation.getTargetingIsoformsMap().get("NX_P06213-1").getFirstPosition().intValue());
		Assert.assertEquals(246, annotation.getTargetingIsoformsMap().get("NX_P06213-2").getFirstPosition().intValue());
		Assert.assertEquals(326, annotation.getTargetingIsoformsMap().get("NX_P06213-1").getLastPosition().intValue());
		Assert.assertEquals(326, annotation.getTargetingIsoformsMap().get("NX_P06213-2").getLastPosition().intValue());
	}
}
