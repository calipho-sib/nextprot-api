package org.nextprot.api.core.service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.commons.constants.IsoTargetSpecificity;
import org.nextprot.commons.statements.TargetIsoformSet;
import org.nextprot.commons.statements.TargetIsoformStatementPosition;

public class TargetIsoformSerializerTest {

	@Test
	public void serializeToJsonString() {

		TargetIsoformStatementPosition tis = new TargetIsoformStatementPosition("iso-1", 6, 7, IsoTargetSpecificity.BY_DEFAULT.name(), null);

		String json = new TargetIsoformSet(new HashSet<>(Arrays.asList(tis))).serializeToJsonString();
		Set<TargetIsoformStatementPosition> tis2 = TargetIsoformSet.deSerializeFromJsonString(json);

		Assert.assertEquals(tis2.iterator().next().getBegin(), Integer.valueOf(6));
		Assert.assertEquals(tis2.iterator().next().getEnd(), Integer.valueOf(7));

	}

}
