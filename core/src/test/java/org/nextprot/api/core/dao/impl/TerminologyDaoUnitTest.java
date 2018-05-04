package org.nextprot.api.core.dao.impl;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.core.domain.CvTerm;

import java.util.List;

public class TerminologyDaoUnitTest {


	@Test
	public void shouldRetrieveTermAccessionsAndRelationTypes() {
		String TEST_STRING = "TS-0449->part_of|TS-1297->part_of|TS-2101->is_a";

		List<CvTerm.TermAccessionRelation> accessionsRelations = TerminologyDaoImpl.extractPipeDelimitedRelations(TEST_STRING);

		Assert.assertEquals(accessionsRelations.get(0).getTermAccession(), "TS-0449");
		Assert.assertEquals(accessionsRelations.get(0).getRelationType(), "part_of");

		Assert.assertEquals(accessionsRelations.get(2).getTermAccession(), "TS-2101");
		Assert.assertEquals(accessionsRelations.get(2).getRelationType(), "is_a");

	}

}
