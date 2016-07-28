package com.nextprot.api.annotation.builder;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.commons.constants.IsoTargetSpecificity;
import org.nextprot.commons.statements.TargetIsoformStatementPosition;

import com.nextprot.api.annotation.builder.statement.TargetIsoformSerializer;

public class TargetIsoformSerializerTest {

	@Test
	public void serializeToJsonString() {

		TargetIsoformStatementPosition tis = new TargetIsoformStatementPosition("iso-1", 6, 7, IsoTargetSpecificity.BY_DEFAULT.name());

		String json = TargetIsoformSerializer.serializeToJsonString(new HashSet<>(Arrays.asList(tis)));
		Set<TargetIsoformStatementPosition> tis2 = TargetIsoformSerializer.deSerializeFromJsonString(json);

		Assert.assertEquals(tis2.iterator().next().getBegin(), Integer.valueOf(6));
		Assert.assertEquals(tis2.iterator().next().getEnd(), Integer.valueOf(7));

	}

}
