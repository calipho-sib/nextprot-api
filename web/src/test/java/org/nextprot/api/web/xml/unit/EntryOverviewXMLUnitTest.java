package org.nextprot.api.web.xml.unit;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.Overview;
import org.nextprot.api.core.domain.Overview.EntityName;
import org.nextprot.api.web.dbunit.base.mvc.WebUnitBaseTest;
import org.nextprot.api.web.utils.XMLUnitUtils;
import org.w3c.dom.NodeList;

public class EntryOverviewXMLUnitTest extends WebUnitBaseTest {

	// Tests issue CALIPHOMISC-330
	// https://issues.isb-sib.ch/browse/CALIPHOMISC-330
	@Test
	public void shouldContainOverviewWithGeneNameList() throws Exception {

		// Create an entry for test purposes
		Entry entry = new Entry("my-test-entry");
		Overview overview = new Overview();	entry.setOverview(overview);
		List<EntityName> names = new ArrayList<Overview.EntityName>();	overview.setGeneNames(names);
		EntityName mainName = new EntityName(); mainName.setMain(true); mainName.setName("ABCD"); 
		EntityName synonym = new EntityName(); synonym.setMain(false); synonym.setName("EFGH");
		EntityName orf = new EntityName(); orf.setMain(false); orf.setName("IJKL");	orf.setCategory("ORF"); 
		mainName.setSynonyms(Arrays.asList(synonym, orf));
		names.add(mainName);
		

		// Gets the velocity output
		String output = this.getVelocityOutput(entry);

		NodeList nodes = XMLUnitUtils.getMatchingNodes(output, "entry/overview");
		assertEquals(1, nodes.getLength());

		// Test the content using xmlunit
		NodeList recommendedNodes = XMLUnitUtils.getMatchingNodes(output, "entry/overview/gene-list/gene/gene-name[@type='primary']");
		assertEquals("ABCD", recommendedNodes.item(0).getTextContent());
		NodeList alternativeNodeList = XMLUnitUtils.getMatchingNodes(output, "entry/overview/gene-list/gene/gene-name[@type='synonym']");
		assertEquals("EFGH", alternativeNodeList.item(0).getTextContent());
		NodeList orfNodeList = XMLUnitUtils.getMatchingNodes(output, "entry/overview/gene-list/gene/gene-name[@type='ORFName']");
		assertEquals("IJKL", orfNodeList.item(0).getTextContent());

	}

}
