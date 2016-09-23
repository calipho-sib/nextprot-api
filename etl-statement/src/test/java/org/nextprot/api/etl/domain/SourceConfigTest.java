package org.nextprot.api.etl.domain;

import java.util.Arrays;

import org.junit.Test;

public class SourceConfigTest {
	
	@Test
	public void testSource(){
		
		SourceConfig config = new SourceConfig();
		Arrays.asList(config.getSource("bioEDitor").getEntries()).contains("BRCA1");
		
	}

}
