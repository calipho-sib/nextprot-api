package org.nextprot.api.core.dao;

import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.nextprot.api.commons.constants.AnnotationApiModel;
import org.nextprot.api.core.domain.DbXref;
import org.nextprot.api.core.domain.Interaction;
import org.nextprot.api.core.domain.IsoformSpecificity;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.domain.annotation.AnnotationEvidence;
import org.nextprot.api.core.domain.annotation.AnnotationIsoformSpecificity;
import org.nextprot.api.core.domain.annotation.AnnotationProperty;
import org.nextprot.api.core.service.DbXrefService;
import org.nextprot.api.core.test.base.CoreUnitBaseTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles({ "dev" })
public class MasterIsoformMappingIntegrationTest extends CoreUnitBaseTest {

	@Autowired
	private MasterIsoformMappingDao mimdao;
	
	@Test
	public void shouldReturn_Something() {
 		List<IsoformSpecificity> specs = mimdao.findIsoformMappingByMaster("NX_P26439");
 		assertTrue(specs.size()==4); // for each of the 2 isoform we have 2 mapping positions: 2 x 2 = 4
 		for (IsoformSpecificity spec: specs) {
 			assertTrue(spec.getIsoformName()!=null);
 			assertTrue(spec.getPositions().size()==1); // we expect one position item by row and thus by IsoformSpecificity
 			//System.out.println(spec.toString());
 		}
	}	
	
}
