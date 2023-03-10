package org.nextprot.api.core.dao;

import org.junit.Test;
import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.service.AnnotationService;
import org.nextprot.api.core.test.base.CoreUnitBaseTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.Assert.assertTrue;
import java.util.List;

@ActiveProfiles({"dev", "cache"})
public class EntryKeywordCreationTest extends CoreUnitBaseTest {

	@Autowired private AnnotationService annotService;	
	
	@Test
	public void testFindEntryProperties() {
		// NX_A0A075B6K0 NX_A6NE52 NX_I6L899 should have result = true
		boolean result = false;
		List<Annotation> annots = annotService.findAnnotations("NX_A6NE52");
		for (Annotation annot : annots) {
			if (annot.getAPICategory()== AnnotationCategory.UNIPROT_KEYWORD &&
				annot.getCvTermAccessionCode().equals("KW-1267")) {
				result = true;
				break;
			}
		}
		assertTrue(result);
	}
}
