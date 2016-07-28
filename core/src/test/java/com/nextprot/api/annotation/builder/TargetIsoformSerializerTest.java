package com.nextprot.api.annotation.builder;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.commons.statements.TargetIsoformStatement;

import com.nextprot.api.annotation.builder.statement.TargetIsoformSerializer;

public class TargetIsoformSerializerTest {

	@Test
	public void serializeToJsonString() {

		TargetIsoformStatement tis = new TargetIsoformStatement();
		tis.putIsoformPosition("iso-1", 6, 7);

		String json = TargetIsoformSerializer.serializeToJsonString(tis);
		TargetIsoformStatement tis2 = TargetIsoformSerializer.deSerializeFromJsonString(json);

		Assert.assertEquals(tis2.values().iterator().next().getBegin(), Integer.valueOf(6));
		Assert.assertEquals(tis2.values().iterator().next().getEnd(), Integer.valueOf(7));

	}

}
