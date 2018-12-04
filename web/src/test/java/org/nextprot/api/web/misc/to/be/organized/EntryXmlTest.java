package org.nextprot.api.web.misc.to.be.organized;

import difflib.Delta;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.nextprot.api.commons.utils.XmlComparator;
import org.nextprot.api.web.dbunit.base.mvc.MVCBaseIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.web.context.WebApplicationContext;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

/**
 * Class used for testing Genomic Mapping controller
 * 
 * @author pam
 */

@Ignore
@ActiveProfiles("pam")
public class EntryXmlTest extends MVCBaseIntegrationTest {

	@Autowired
	private WebApplicationContext wac;

	private MockMvc mockMvc;

	private String[] acs = { "NX_P01308", "NX_Q9NRR5", "NX_P48730", "NX_P06213", "NX_P08631", "NX_P11362", "NX_P00519", "NX_P21802", "NX_P00533", "NX_P36897",
			"NX_P22607", "NX_P35968", "NX_P05067", "NX_P29590", "NX_P54646", "NX_Q13131", "NX_O95271", "NX_P00387", "NX_P18074", "NX_P01130", "NX_Q03001",
			"NX_P27695", "NX_P04629", "NX_P31749", "NX_Q9Y2W7", "NX_P17948", "NX_Q15303", "NX_Q02763", "NX_P03372", "NX_P13497", "NX_Q8NBP7", "NX_P37173",
			"NX_P04637", "NX_Q06187", "NX_Q16620", "NX_P46531", "NX_O15146", "NX_P09619", "NX_P12931", "NX_P38398", "NX_P31751" };

	private String currentAC = acs[0];
	private Document oldDoc;
	private Document newDoc;

	@Before
	public void setup() {
		try {
			this.mockMvc = webAppContextSetup(this.wac).build();
			this.newDoc = getNewXMLDocument(currentAC);
			this.oldDoc = getOldXMLDocument(currentAC);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	// http://uat-dbint:8080/db/search/export?ac=NX_Q9Y6V7,NX_Q9Y6X4,NX_Q9Y6Y8&type=xml
	public String oldServerBaseUrl = "http://uat-web1:8080/db/search/export";

	public void memStatus(String msg) {
		//System.out.println(msg + ": " + "memory max: " + Runtime.getRuntime().maxMemory() / 1024 / 1024 + "M" + " - free: " + Runtime.getRuntime().freeMemory()/ 1024 / 1024 + "M" + " - tot: " + Runtime.getRuntime().totalMemory() / 1024 / 1024 + "M");

	}

	public Document getOldXMLDocument(String ac) throws Exception {
		URL url = new URL(oldServerBaseUrl + "?ac=" + ac + "&type=xml");
		//System.out.println("Old URL is: " + url + "\n...");
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = builderFactory.newDocumentBuilder();
		InputStream in = conn.getInputStream();
		Document xmlDocument = builder.parse(in);
		in.close();
		return xmlDocument;
	}

	public Document getNewXMLDocument(String ac) throws Exception {
		String url = "/entry/" + ac + ".xml";
		RequestBuilder rb = get(url);
		//System.out.println("New URL is: " + url);
		// String s =
		// this.mockMvc.perform(rb).andReturn().getResponse().getContentAsString();
		InputStream in = new ByteArrayInputStream(this.mockMvc.perform(rb).andReturn().getResponse().getContentAsByteArray());
		// System.out.println("XML begins like: " + s.substring(0,200));
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = builderFactory.newDocumentBuilder();
		Document xmlDocument = builder.parse(in);
		return xmlDocument;
	}

	@Test
	public void testUniqueName() throws Exception {
		XPathFactory factory = XPathFactory.newInstance();
		XPath xpath = factory.newXPath();
		String oldS, newS;
		newS = (String) xpath.evaluate("/neXtProtExport/proteins/protein/@uniqueName", newDoc, XPathConstants.STRING);
		oldS = (String) xpath.evaluate("/nextprotExport/proteins/protein/@uniqueName", oldDoc, XPathConstants.STRING);
		assertEquals("uniqueName", oldS, newS);
	}

	/*
	 * There are still errors with acs[1], Daniel is working on it (pam,
	 * 10.07.2013)
	 */
	@Test
	public void testAnnotationsContent() throws Exception {
		StringBuffer errMsg = new StringBuffer("");
		XPathFactory factory = XPathFactory.newInstance();
		XPath xpath = factory.newXPath();
		NodeList oldNl = (NodeList) xpath.evaluate("/nextprotExport/proteins/protein/annotations/annotationList", oldDoc, XPathConstants.NODESET);
		for (int i = 0; i < oldNl.getLength(); i++) {
			Node n = oldNl.item(i);
			String categ = n.getAttributes().getNamedItem("category").getNodeValue();
			// if (!categ.equals("function")) continue;
			// NodeList oldList = (NodeList)
			// xpath.evaluate("/nextprotExport/proteins/protein/annotations/annotationList[@category='"
			// + categ + "']/annotation", oldDoc, XPathConstants.NODESET);
			NodeList newList = (NodeList) xpath.evaluate("/neXtProtExport/proteins/protein/annotations/annotationList[@category='" + categ + "']/annotation",
					newDoc, XPathConstants.NODESET);

			Node oldN = (Node) xpath.evaluate("/nextprotExport/proteins/protein/annotations/annotationList[@category='" + categ + "']", oldDoc,
					XPathConstants.NODE);
			Node newN = (Node) xpath.evaluate("/neXtProtExport/proteins/protein/annotations/annotationList[@category='" + categ + "']", newDoc,
					XPathConstants.NODE);

			// change the values below to help testing / finding the problems
			boolean maskErrors = false;
			boolean maskNewFeatures = true;
			boolean maskOldFeatures = true;
			boolean showTrees = true;
			boolean stopAfterFirstError = true;

			String lines2Ignore = "kdfgjdkfghk"; // keep this value = pattern
													// with no chance to be
													// found

			if (categ.equalsIgnoreCase("go biological process")) {
				if (maskErrors)
					lines2Ignore += "|annotation-description"; // ERROR missing
																// in new XML
																// (=cvname) ?
			} else if (categ.equalsIgnoreCase("go molecular function")) {
				if (maskErrors)
					lines2Ignore += "|annotation-description"; // ERROR missing
																// in new XML
																// (=cvname) ?

			} else if (categ.equalsIgnoreCase("function")) {
				if (maskNewFeatures)
					lines2Ignore += "|annotation-properties"; // new feature:
																// propertyName=rank
																// ?
			} else if (categ.equalsIgnoreCase("enzyme regulation")) {
				if (maskNewFeatures)
					lines2Ignore += "|annotation-properties"; // new feature:
																// propertyName=rank
																// ?
			} else if (categ.equalsIgnoreCase("miscellaneous")) {
				if (maskNewFeatures)
					lines2Ignore += "|annotation-properties"; // new feature:
																// propertyName=rank
																// ?
			} else if (categ.equalsIgnoreCase("caution")) {
				if (maskNewFeatures)
					lines2Ignore += "|annotation-properties"; // new feature:
																// propertyName=rank
																// ?
			} else if (categ.equalsIgnoreCase("catalytic activity")) {
				if (maskNewFeatures)
					lines2Ignore += "|annotation-properties"; // new feature:
																// propertyName=rank
																// ?

			} else if (categ.equalsIgnoreCase("sequence variant")) {
				if (maskErrors)
					lines2Ignore += "|annotation-description-#cdata-section=in [leprch:uniprot_disease:di-01890";
				if (maskErrors)
					lines2Ignore += "|annotation-description-#cdata-section=missing in [leprch:uniprot_disease:di-01890";
				// ERROR with NX_P06213 with annotation-uniqueName=VAR_015913:
				// new = annotation-description-#cdata-section=in
				// [leprch:uniprot_disease:di-01890]
				// old = annotation-description-#cdata-section=missing in
				// [leprch:uniprot_disease:di-01890]

				if (maskErrors)
					lines2Ignore += "|annotation-attr-uniquename"; // ERROR:
																	// uniqueName
																	// missing
																	// in new
																	// XML
				if (maskNewFeatures)
					lines2Ignore += "|property-attr-propertyname=mutation AA"; // new
																				// feature
																				// ?
				if (maskNewFeatures)
					lines2Ignore += "|property-attr-accession"; // new feature ?
				if (maskNewFeatures)
					lines2Ignore += "|property-attr-propertyname=disease"; // new
																			// feature
																			// ?
				if (maskNewFeatures)
					lines2Ignore += "|property-attr-propertyvalue"; // values
																	// for new
																	// features
				if (maskNewFeatures)
					lines2Ignore += "|annotation-properties-property"; // container
																		// for
																		// new
																		// features
				if (maskNewFeatures)
					lines2Ignore += "|annotation-properties"; // container for
																// new features

			} else if (categ.equalsIgnoreCase("disease")) {
				if (maskNewFeatures)
					lines2Ignore += "|annotation-properties"; // new feature:
																// propertyName=rank
																// ?
				// ERROR: Escape codes in CDATA
				// Example:
				// "[-annotationList-annotation-description+ name:#cdata-section - value:Familial advanced sleep-phase syndrome (FASPS) [MIM:604348]: Characterized by very early sleep onset and offset. Individuals are 'morning larks' with a 4 hours advance of the sleep, temperature and melatonin rhythms. Note=The disease is caused by mutations affecting the gene represented in this entry.]"
				// +
				// "[-annotationList-annotation-description+ name:#cdata-section - value:Familial advanced sleep-phase syndrome (FASPS) [MIM:604348]: Characterized by very early sleep onset and offset. Individuals are &apos;morning larks&apos; with a 4 hours advance of the sleep, temperature and melatonin rhythms. Note=The disease is caused by mutations affecting the gene represented in this entry.]]";

			} else if (categ.equalsIgnoreCase("expression info")) {
				if (maskNewFeatures)
					lines2Ignore += "|annotation-properties"; // new features:
																// antibody acc,
																// integration
																// level
				// Example:
				// <properties>
				// <property propertyName="antibody acc"
				// propertyValue="CAB015410"/>
				// <property propertyName="integrationLevel"
				// propertyValue="single"/>
				// </properties>
			} else if (categ.equalsIgnoreCase("developmental stage")) {
				if (maskNewFeatures)
					lines2Ignore += "|annotation-properties"; // new feature:
																// propertyName=rank
																// ?

			} else if (categ.equalsIgnoreCase("mature protein")) {
				if (maskErrors)
					lines2Ignore += "|annotation-attr-uniquename"; // ERROR:
																	// uniqueName
																	// attribute
																	// missing
																	// in new
																	// format
				// Example:
				// <annotationList category="mature protein">
				// <annotation qualityQualifier="GOLD"
				// uniqueName="PRO_0000192833-2">
			} else if (categ.equalsIgnoreCase("PTM")) {
				if (maskNewFeatures)
					lines2Ignore += "|annotation-properties"; // new feature:
																// propertyName=rank
																// ?
			} else if (categ.equalsIgnoreCase("subunit")) {
				if (maskNewFeatures)
					lines2Ignore += "|annotation-properties"; // new feature:
																// propertyName=rank
																// ?
				// if (maskEscapeCharsInCDATA)
				// lines2Ignore+="|annotation-description-#cdata-section=tetramer of 2 alpha and 2 beta chains";
				// // ERROR: escape chars in new XML description with
				// NX_P06213

			} else if (categ.equalsIgnoreCase("subcellular location")) {
				if (maskErrors)
					lines2Ignore += "|annotation-description"; // ERROR missing
																// in new XML
																// (=cvname) ?
				if (maskErrors)
					lines2Ignore += "|annotation-properties-property-attr-value"; // ERROR:
																					// attribute
																					// value
																					// becomes
																					// propertyValue
																					// in
																					// new
																					// XML,
																					// see
																					// NX_P06213
				if (maskErrors)
					lines2Ignore += "|annotation-properties-property-attr-propertyvalue";

			} else if (categ.equalsIgnoreCase("go cellular component")) {
				if (maskErrors)
					lines2Ignore += "|annotation-description"; // ERROR missing
																// in new XML
																// (=cvname) ?

			} else if (categ.equalsIgnoreCase("subcellular location info")) {
				if (maskNewFeatures)
					lines2Ignore += "|annotation-properties"; // new feature:
																// propertyName=rank
																// ?

			} else if (categ.equalsIgnoreCase("tissue specificity")) {
				if (maskOldFeatures)
					lines2Ignore += "|annotation"; // ERROR: missing
													// annotation-description
				// new feature: propertyName=rank ?
				if (maskOldFeatures)
					lines2Ignore += "|annotation-experimentalEvidences"; // apparently
																			// this
																			// won't
																			// be
																			// present
																			// in
																			// the
																			// new
																			// xml
				// ERROR: too many annotation-evidences...
				// ERROR: missing annotation-properties...
				// new feature: annotation-isoformSpecificity ?
			} else if (categ.equalsIgnoreCase("pharmaceutical")) {
				if (maskNewFeatures)
					lines2Ignore += "|annotation-properties"; // new feature:
																// propertyName=rank
																// ?

			} else if (categ.equalsIgnoreCase("maturation peptide")) {
				if (maskErrors)
					lines2Ignore += "|annotation-attr-uniquename"; // ERROR
																	// missing
																	// uniqueName
																	// attribute

			} else if (categ.equalsIgnoreCase("sequence caution")) {
				if (maskNewFeatures)
					lines2Ignore += "|annotation-properties"; // new feature:
																// propertyName=rank
																// ?
																// new feature:
																// propertyName=conflict
																// type ?
				if (maskErrors)
					lines2Ignore += "|annotation-description"; // ERROR missing
																// description
																// element

			} else if (categ.equalsIgnoreCase("transmembrane region")) {
				if (maskNewFeatures)
					lines2Ignore += "|annotation-properties"; // new feature:
																// propertyName="region structure"
																// propertyValue="Helical"
																// ?
				if (maskErrors)
					lines2Ignore += "|annotation-description"; // ERROR missing
																// description
																// element

			} else if (categ.equalsIgnoreCase("domain information")) {
				if (maskNewFeatures)
					lines2Ignore += "|annotation-properties"; // new feature:
																// propertyName=rank
																// ?
			}

			XmlComparator.getNodeTreeAsList2(oldN, "original", lines2Ignore, true, showTrees, false, true);
			XmlComparator.getNodeTreeAsList2(newN, "revised", lines2Ignore, true, showTrees, false, true);

			boolean result = XmlComparator.compareXmlNodes2("annotations type:" + categ + " ", lines2Ignore, oldN, newN, false, true);
			//System.out.println((result ? "OK" : "ERROR") + ": content of annotation category: " + categ + " - " + newList.getLength() + " annotation(s)");
			String m = result ? "" : "ERROR: content of annotation type " + categ + " is not the same in old XML";
			if (m.length() > 0) {
				errMsg.append("\n");
				errMsg.append(m);
				if (stopAfterFirstError)
					break;
			}
		}
		//System.out.println("currentAC was:" + currentAC);
		if (errMsg.length() > 0)
			fail(errMsg.toString());
	}

	@Test
	public void testForAll() throws Exception {
		boolean skip = false;
		for (String ac : this.acs) {
			if (ac.equalsIgnoreCase("skfsjhf"))
				skip = false;
			if (skip) {
				//System.out.println("skipping tests on " + ac);
				continue;
			}
			memStatus("");
			this.currentAC = ac;
			this.oldDoc = getOldXMLDocument(currentAC);
			this.newDoc = getNewXMLDocument(currentAC);
			// testKeywordsContent();
			// testIsoformsContent();
			// testChromosomalLocationsContent();
			// testInteractionsContent();
			testPublicationsContent();
		}
	}

	/*
	 * OK, 23.07.2013 using acs list used by testForAll()
	 */
	@Test
	public void testKeywordsContent() throws Exception {

		StringBuffer errMsg = new StringBuffer("");
		XPathFactory factory = XPathFactory.newInstance();
		XPath xpath = factory.newXPath();
		Node oldN = (Node) xpath.evaluate("/nextprotExport/proteins/protein/keywords", oldDoc, XPathConstants.NODE);
		Node newN = (Node) xpath.evaluate("/neXtProtExport/proteins/protein/keywords", newDoc, XPathConstants.NODE);
		// change the values below to help testing / finding the problems
		boolean showTrees = true;
		String lines2Ignore = "kdfgjdkfghk"; // keep this value = pattern with
												// no chance to be found

		// 2 differences:

		// 1) some keywords are repeated in the old version, the problem is in
		// thus in the old version
		// to remove KW repetitions in this test, I remove identical lines
		// produced by the XmlComparator (sortUnique=true)

		// 2) some KW are ignored in the old version, probably related to
		// contentconfig, excerpt:
		// Amos confirms that KW-1185 et KW-0181 must NOT be included in the XML

		lines2Ignore += "|kw-1185"; // Amos said this keyword one has to be
									// banned as well and the old XML contains
									// it (i.e.
									// http://uat-web1:8080/db/search/export?ac=NX_Q9NRR5&type=xml)

		XmlComparator.getNodeTreeAsList2(oldN, "original", lines2Ignore, true, showTrees, true, false);
		XmlComparator.getNodeTreeAsList2(newN, "revised", lines2Ignore, true, showTrees, true, false);

		boolean result = XmlComparator.compareXmlNodes2("keywords", lines2Ignore, oldN, newN, true, false);
		//System.out.println((result ? "OK" : "ERROR") + " " + currentAC + " keywords");
		String m = result ? "" : "ERROR: " + currentAC + " keywords are not the same in old XML";
		if (m.length() > 0) {
			errMsg.append("\n");
			errMsg.append(m);
		}
		if (errMsg.length() > 0)
			fail(errMsg.toString());
	}

	/*
	 * OK: 31.07.2013 - works for all acs in list - fixed ERROR: 29.07.2013 database attribute & url sub element of intractant of external interactants (uniprot...)
	 * OK: 25.07.2013 works for NX_Q9NRR5
	 */
	@Test
	public void testInteractionsContent() throws Exception {
		int a = 2;
		if (1 == a) {
			String s = "aa|bb||c";
			String[] list = s.split("\\|");
			//for (int i = 0; i < list.length; i++)
			//	System.out.println(list[i]);
			System.exit(0);
		}

		StringBuffer errMsg = new StringBuffer("");
		XPathFactory factory = XPathFactory.newInstance();
		XPath xpath = factory.newXPath();
		Node oldN = (Node) xpath.evaluate("/nextprotExport/proteins/protein/interactions", oldDoc, XPathConstants.NODE);
		Node newN = (Node) xpath.evaluate("/neXtProtExport/proteins/protein/interactions", newDoc, XPathConstants.NODE);
		// change the values below to help testing / finding the problems
		boolean showTrees = false;
		String lines2Ignore = "kdfgjdkfghk"; // keep this value = pattern with
												// no chance to be found

		// lines2Ignore += "|uniquename"; // solved error 1
		// lines2Ignore += "|query=ida"; // remove new URL form for self
		// interaction (error 2 and 3)
		lines2Ignore += "|711226,ebi-711226";   // removes old URL form of self interaction (error 2 and 3)
		lines2Ignore += "|1028277,EBI-1028277"; // removes self interaction of NX_P11362
		lines2Ignore += "|1028658,ebi-1028658"; // removes self interaction of NX_P21802
		lines2Ignore += "|297353,ebi-297353"; // removes self interaction of NX_P00533
		lines2Ignore += "|348399,ebi-348399"; // removes self interaction of NX_P22607
		lines2Ignore += "|821758,ebi-821758"; // removes self interaction of NX_P05067
		lines2Ignore += "|295890,ebi-295890"; // removes self interaction of NX_P29590
		lines2Ignore += "|1383852,ebi-1383852"; // removes self interaction of NX_P54646
		lines2Ignore += "|1181405,ebi-1181405"; // removes self interaction of NX_Q13131

		lines2Ignore += "|1048805,ebi-1048805"; // removes self interaction of NX_P27695
		lines2Ignore += "|296087,ebi-296087"; // removes self interaction of NX_P31749
		lines2Ignore += "|78473,ebi-78473"; // removes self interaction of NX_P03372
		
		lines2Ignore += "|366083,ebi-366083"; // removes self interaction of NX_P04637
		lines2Ignore += "|624835,ebi-624835"; // removes self interaction of NX_Q06187
		lines2Ignore += "|3904881,ebi-3904881"; // removes self interaction of NX_Q16620
		lines2Ignore += "|636374,ebi-636374"; // removes self interaction of NX_P46531
		lines2Ignore += "|621482,ebi-621482"; // removes self interaction of NX_P12931
		lines2Ignore += "|349905,ebi-349905"; // removes self interaction of NX_P38398
		
		// FIXED: ERROR 1: unique name is always the name of the AC of the
		// entry, 2 interactants elements should be given if interaction is not
		// a self interaction
		// FIXED: ERROR 2: URL built doesn't work for self interactions (we get
		// all the interactions of the entry). Would be better to xref the
		// interaction AC
		// FIXED: ERROR 3: self interaction not in the list but we need to
		// ignore the special url created in this case for the comparison to
		// work
		// FIXED: ERROR: interactions with trEMBL partner are listed in new XML
		// but not in old one.
		
		if (oldN==null && newN==null) {
			//System.out.println("OK: " + currentAC + " has no interactions");
			return;
		}
		
		XmlComparator.getNodeTreeAsList2(oldN, "original", lines2Ignore, false, showTrees, false, false);
		XmlComparator.getNodeTreeAsList2(newN, "revised", lines2Ignore, false, showTrees, false, false);

		String oldUrlPart = "intact/search/do/search"; // found in old XML
		String newUrlPart = "intact/pages/interactions/interactions.xhtml"; // found
																			// in
																			// new
																			// xml
		String oldUnamePart = "nx_";
		String newUnamePart = "(empty)";
		String targets = oldUrlPart + "|" + oldUnamePart;
		String replacs = newUrlPart + "|" + newUnamePart;
		boolean result = XmlComparator.compareXmlNodes2("interactions", lines2Ignore, targets, replacs, oldN, newN, false, false, true);
		//System.out.println((result ? "OK" : "ERROR") + " " + currentAC + " interactions");
		String m = result ? "" : "ERROR: " + currentAC + " interactions are not the same in old XML";
		if (m.length() > 0) {
			errMsg.append("\n");
			errMsg.append(m);
		}
		if (errMsg.length() > 0) {
			fail(errMsg.toString());
		}
	}

	/*
	 * ERROR: 31.07.2013
	 * - some unexpected properties
	 * - some incomplete urls
	 */
	@Test
	public void testXrefsContent() throws Exception {

		StringBuffer errMsg = new StringBuffer("");
		XPathFactory factory = XPathFactory.newInstance();
		XPath xpath = factory.newXPath();
		Node oldN = (Node) xpath.evaluate("/nextprotExport/proteins/protein/xrefs", oldDoc, XPathConstants.NODE);
		Node newN = (Node) xpath.evaluate("/neXtProtExport/proteins/protein/xrefs", newDoc, XPathConstants.NODE);
		// change the values below to help testing / finding the problems
		boolean showTrees = true;
		String lines2Ignore = "kdfgjdkfghk"; // keep this value = pattern with no chance to be found
		// db names known not to be present in old XML according to Mario
		lines2Ignore +="|DMDM|DNASU|GeneCards|GeneID|GenomeRNAi|HGNC|HPRD|KO|NextBio|PaxDb|SignaLink|Unigene";		
		// db names not found by pam in old XML
		lines2Ignore +="|Allergome|CTD|NCBI|ChEMBL|EvolutionaryTrace";
		
		// --------------------------- NX_P01308 -----------------------------
		// ERRORS ? 
		lines2Ignore += "|xrefs-xref-properties-property-attr-propertyname=match status";  // never found in old XML of NX_P01308
		lines2Ignore += "|xrefs-xref-properties-property-attr-propertyname=organism name"; // never found in old XML of NX_P01308
		lines2Ignore += "|xrefs-xref-properties-property-attr-propertyname=status"; // never found in old XML of NX_P01308
	
		
		// ERRORS !:
		//for NX_P01308, new XML: <![CDATA[ http://www.omabrowser.org ]]>
		//for NX_P01308, old XML: <![CDATA[ http://omabrowser.org/cgi-bin/gateway.pl?f=DisplayGroup&p1=P01308  ]]>
		//lines2Ignore += "|database=oma"; // found in NX_P01308
		// for NX_P01308, new XML: http://pbil.univ-lyon1.fr/databases/hogenom.php
		// for NX_P01308, old XML: http://pbil.univ-lyon1.fr/cgi-bin/acnuc-ac2tree?query=P01308&db=HOGENOM
		lines2Ignore += "|database=hogenom"; // found in NX_P01308
		// for NX_P01308, new XML: http://pbil.univ-lyon1.fr/databases/hovergen.html	
		// for NX_P01308, new XML: http://pbil.univ-lyon1.fr/cgi-bin/acnuc-ac2tree?query=P01308&db=HOVERGEN
		lines2Ignore += "|database=hovergen"; // NX_P01308
		
		// --------------------------- NX_Q9NRR5 -----------------------------
		//lines2Ignore += "|database=orthodb"; // wrong URL in new XML of NX_Q9NRR5
		lines2Ignore += "|databases-attr-database=mim-attr-id=1481802"; //  xref not found in old xml
		lines2Ignore += "|other-attr-database=chitars-attr-id=27985321"; //  xref not found in old xml		
		
		// --------------------------- NX_P48730 -----------------------------
		lines2Ignore += "|database=mim-attr-id=1609708"; // xref not found in old xml
		lines2Ignore += "|accession=2.7.11.1-attr-category=enzyme and pathway databases-attr-database=brenda-attr-id=964246"; // incomplete url & extra property
		lines2Ignore += "|attr-accession=csnk1d-attr-category=other-attr-database=chitars-attr-id=28003577"; // xref not found in old url
		
		if (oldN==null && newN==null) {
			//System.out.println("OK: " + currentAC + " has no xrefs");
			return;
		}
		
		XmlComparator.getNodeTreeAsList2(oldN, "original", lines2Ignore, false, showTrees, false, false);
		XmlComparator.getNodeTreeAsList2(newN, "revised", lines2Ignore, false, showTrees, false, false);

		String targets = "";
		String replacs = "";
		List<Delta> deltas = XmlComparator.compareXmlNodes2AndGetDeltas("xrefs", lines2Ignore, targets, replacs, oldN, newN, false, false, true);
		boolean result = true;
		for (Delta d: deltas) {
			if (! d.toString().contains("ChangeDelta, position: 0") && 
				! d.toString().contains("03|-xrefs-xref-properties, 03|-xrefs-xref-properties, 03|-xrefs-xref-properties") && // for NX_P01308
				! d.toString().contains("[03|-xrefs-xref-properties, 03|-xrefs-xref-properties") && // for NX_Q9NRR5 &&
				! d.toString().contains("03|-xrefs-xref-url")
				)
			{
				//System.out.println("DELTA=" + d.toString());
				result = false;
			}
		}
		//System.out.println((result ? "OK" : "ERROR") + " " + currentAC + " xrefs");
		String m = result ? "" : "ERROR: " + currentAC + " xrefs are not the same in old XML";
		if (m.length() > 0) {
			errMsg.append("\n");
			errMsg.append(m);
		}
		if (errMsg.length() > 0) {
			fail(errMsg.toString());
		}
	}


	@Test
	public void testIdentifiersContent() throws Exception {

		//TODO Daniel and Mario
		StringBuffer errMsg = new StringBuffer("");
		XPathFactory factory = XPathFactory.newInstance();
		XPath xpath = factory.newXPath();
		Node oldN = (Node) xpath.evaluate("/nextprotExport/proteins/protein/identifiers", oldDoc, XPathConstants.NODE);
		Node newN = (Node) xpath.evaluate("/neXtProtExport/proteins/protein/identifiers", newDoc, XPathConstants.NODE);
		// change the values below to help testing / finding the problems
		boolean showTrees = true;
		String lines2Ignore = "kdfgjdkfghk"; // keep this value = pattern with no chance to be found
		
		// to remove a line from the comparison add a sub-string the line contains
		// if multiple patterns are used, separate them with |
		
		// --------------------------- NX_P01308 -----------------------------
		//lines2Ignore +="|...";
				
		// --------------------------- NX_Q9NRR5 -----------------------------
		//lines2Ignore +="|...";
		
		// --------------------------- NX_P48730 -----------------------------
		//lines2Ignore +="|...";
		
		if (oldN==null && newN==null) {
			//System.out.println("OK: " + currentAC + " has no identifiers");
			return;
		}
		
		// 2 lines below just to show trees the way you want in console, no side effect
		XmlComparator.getNodeTreeAsList2(oldN, "original", lines2Ignore, false, showTrees, false, false);
		XmlComparator.getNodeTreeAsList2(newN, "revised", lines2Ignore, false, showTrees, false, false);

		// if some differences are found and you want to hide them because they are not significant
		// you can TRANSFORM the content of the lines generated based by replacing some substring with another
		String targets = "uniprotkb"; // uniprot database official name is actually UniProKB => keep this difference is okay
		String replacs = "uniprot";
		// 
		List<Delta> deltas = XmlComparator.compareXmlNodes2AndGetDeltas("identifiers", lines2Ignore, targets, replacs, oldN, newN, false,  false, true);
		boolean result = true;
		for (Delta d: deltas) {
			// if you want to ignore some deltas found because they are not significant do it in if condition below
			if (! d.toString().contains("ChangeDelta, position: 0") // this change is never significant
					)
			{
				//System.out.println("DELTA=" + d.toString());
				result = false;
			}
		}
		//System.out.println((result ? "OK" : "ERROR") + " " + currentAC + " identifiers");
		String m = result ? "" : "ERROR: " + currentAC + " identifiers are not the same in old XML";
		if (m.length() > 0) {
			errMsg.append("\n");
			errMsg.append(m);
		}
		if (errMsg.length() > 0) {
			fail(errMsg.toString());
		}
	}

	
	@Test
	public void testPublicationsContent() throws Exception {

		//TODO Daniel and Mario
		StringBuffer errMsg = new StringBuffer("");
		XPathFactory factory = XPathFactory.newInstance();
		XPath xpath = factory.newXPath();
		Node oldN = (Node) xpath.evaluate("/nextprotExport/proteins/protein/publications", oldDoc, XPathConstants.NODE);
		Node newN = (Node) xpath.evaluate("/neXtProtExport/proteins/protein/publications", newDoc, XPathConstants.NODE);
		// change the values below to help testing / finding the problems
		boolean showTrees = true;
		String lines2Ignore = "kdfgjdkfghk"; // keep this value = pattern with no chance to be found
		
		// to remove a line from the comparison add a sub-string the line contains
		// if multiple patterns are used, separate them with |
		
		// --------------------------- NX_P01308 -----------------------------
		//lines2Ignore +="|...";
				
		// --------------------------- NX_Q9NRR5 -----------------------------
		//lines2Ignore +="|...";
		
		// --------------------------- NX_P48730 -----------------------------
		//lines2Ignore +="|...";
		
		if (oldN==null && newN==null) {
			//System.out.println("OK: " + currentAC + " has no publications");
			return;
		}
		
		// 2 lines below just to show trees the way you want in console, no side effect
		XmlComparator.getNodeTreeAsList2(oldN, "original", lines2Ignore, false, showTrees, false, false);
		XmlComparator.getNodeTreeAsList2(newN, "revised", lines2Ignore, false, showTrees, false, false);

		// if some differences are found and you want to hide them because they are not significant
		// you can TRANSFORM the content of the lines generated based by replacing some substring with another
		String targets = ".";       // we remove dots
		String replacs = "(empty)"; // and replace them with empty string

		List<Delta> deltas = XmlComparator.compareXmlNodes2AndGetDeltas("publications", lines2Ignore, targets, replacs, oldN, newN, false,  false, true);
		boolean result = true;
		for (Delta d: deltas) {
			// if you want to ignore some deltas found because they are not significant do it in if condition below
			if (! d.toString().contains("ChangeDelta, position: 0") // this change is never significant
					)
			{
				//System.out.println("DELTA=" + d.toString());
				result = false;
			}
		}
		//System.out.println((result ? "OK" : "ERROR") + " " + currentAC + " publications");
		String m = result ? "" : "ERROR: " + currentAC + " publications are not the same in old XML";
		if (m.length() > 0) {
			errMsg.append("\n");
			errMsg.append(m);
		}
		if (errMsg.length() > 0) {
			fail(errMsg.toString());
		}
	}

	@Test
	public void testPublicationsSubcontent() throws Exception {

		//TODO Daniel and Mario
		StringBuffer errMsg = new StringBuffer("");
		XPathFactory factory = XPathFactory.newInstance();
		XPath xpath = factory.newXPath();
		Node oldN = (Node) xpath.evaluate("/nextprotExport/proteins/protein/publications", oldDoc, XPathConstants.NODE);
		Node newN = (Node) xpath.evaluate("/neXtProtExport/proteins/protein/publications", newDoc, XPathConstants.NODE);
		// change the values below to help testing / finding the problems
		boolean showTrees = true;
		String lines2Ignore = "kdfgjdkfghk"; // keep this value = pattern with no chance to be found
		
		// to remove a line from the comparison add a sub-string the line contains
		// if multiple patterns are used, separate them with |
		
		// --------------------------- NX_P01308 -----------------------------
		lines2Ignore +="|publication-title";

				
		// --------------------------- NX_Q9NRR5 -----------------------------
		//lines2Ignore +="|...";
		
		// --------------------------- NX_P48730 -----------------------------
		//lines2Ignore +="|...";
		
		if (oldN==null && newN==null) {
			//System.out.println("OK: " + currentAC + " has no publications");
			return;
		}
		
		// 2 lines below just to show trees the way you want in console, no side effect
		XmlComparator.getNodeTreeAsList2(oldN, "original", lines2Ignore, false, showTrees, false, false);
		XmlComparator.getNodeTreeAsList2(newN, "revised", lines2Ignore, false, showTrees, false, false);

		// if some differences are found and you want to hide them because they are not significant
		// you can TRANSFORM the content of the lines generated based by replacing some substring with another
		String targets = ".";       // we remove dots
		String replacs = "(empty)"; // and replace them with empty string

		List<Delta> deltas = XmlComparator.compareXmlNodes2AndGetDeltas("publications", lines2Ignore, targets, replacs, oldN, newN, false,  false, true);
		boolean result = true;
		for (Delta d: deltas) {
			// if you want to ignore some deltas found because they are not significant do it in if condition below
			if (! d.toString().contains("ChangeDelta, position: 0") // this change is never significant
					)
			{
				//System.out.println("DELTA=" + d.toString());
				result = false;
			}
		}
		//System.out.println((result ? "OK" : "ERROR") + " " + currentAC + " publications");
		String m = result ? "" : "ERROR: " + currentAC + " publications are not the same in old XML";
		if (m.length() > 0) {
			errMsg.append("\n");
			errMsg.append(m);
		}
		if (errMsg.length() > 0) {
			fail(errMsg.toString());
		}
	}

	
	
	/*
	 * OK: 25.07.2013 using acs list used by testForAll()
	 */
	@Test
	public void testIsoformsContent() throws Exception {

		StringBuffer errMsg = new StringBuffer("");
		XPathFactory factory = XPathFactory.newInstance();
		XPath xpath = factory.newXPath();
		Node oldN = (Node) xpath.evaluate("/nextprotExport/proteins/protein/isoforms", oldDoc, XPathConstants.NODE);
		Node newN = (Node) xpath.evaluate("/neXtProtExport/proteins/protein/isoforms", newDoc, XPathConstants.NODE);
		// change the values below to help testing / finding the problems
		boolean showTrees = true;
		String lines2Ignore = "kdfgjdkfghk"; // keep this value = pattern with
												// no chance to be found

		// lines2Ignore += "|something to ignore";

		XmlComparator.getNodeTreeAsList2(oldN, "original", lines2Ignore, true, showTrees, false, false);
		XmlComparator.getNodeTreeAsList2(newN, "revised", lines2Ignore, true, showTrees, false, false);

		String targets = "swissprotdislayedisoform"; // spelling error in old
														// XML
		String replacs = "swissprotdisplayedisoform"; // correct spelling in new
														// XML
		boolean result = XmlComparator.compareXmlNodes2("isoforms", lines2Ignore, targets, replacs, oldN, newN, true, false, false);
		//System.out.println((result ? "OK" : "ERROR") + " " + currentAC + " isoforms");
		String m = result ? "" : "ERROR: " + currentAC + " isoforms are not the same in old XML";
		if (m.length() > 0) {
			errMsg.append("\n");
			errMsg.append(m);
		}
		if (errMsg.length() > 0)
			fail(errMsg.toString());
	}

	/*
	 * OK: 25.07.2013 using acs list used by testForAll()
	 */
	@Test
	public void testChromosomalLocationsContent() throws Exception {

		StringBuffer errMsg = new StringBuffer("");
		XPathFactory factory = XPathFactory.newInstance();
		XPath xpath = factory.newXPath();
		Node oldN = (Node) xpath.evaluate("/nextprotExport/proteins/protein/chromosomalLocations", oldDoc, XPathConstants.NODE);
		Node newN = (Node) xpath.evaluate("/neXtProtExport/proteins/protein/chromosomalLocations", newDoc, XPathConstants.NODE);
		// change the values below to help testing / finding the problems
		boolean showTrees = true;
		String lines2Ignore = "kdfgjdkfghk"; // keep this value = pattern with
												// no chance to be found

		// lines2Ignore += "|something to ignore";

		XmlComparator.getNodeTreeAsList2(oldN, "original", lines2Ignore, true, showTrees, false, false);
		XmlComparator.getNodeTreeAsList2(newN, "revised", lines2Ignore, true, showTrees, false, false);

		String targets = null;
		String replacs = null;
		boolean result = XmlComparator.compareXmlNodes2("chromosomalLocations", lines2Ignore, targets, replacs, oldN, newN, false, false, false);
		//System.out.println((result ? "OK" : "ERROR") + " " + currentAC + " chromosomalLocations");
		String m = result ? "" : "ERROR: " + currentAC + " chromosomalLocations are not the same in old XML";
		if (m.length() > 0) {
			errMsg.append("\n");
			errMsg.append(m);
		}
		if (errMsg.length() > 0)
			fail(errMsg.toString());
	}

	@Test
	public void testGenomicMappingsContent() throws Exception {

		StringBuffer errMsg = new StringBuffer("");
		XPathFactory factory = XPathFactory.newInstance();
		XPath xpath = factory.newXPath();
		Node oldN = (Node) xpath.evaluate("/nextprotExport/proteins/protein/genomicMappings", oldDoc, XPathConstants.NODE);
		Node newN = (Node) xpath.evaluate("/neXtProtExport/proteins/protein/genomicMappings", newDoc, XPathConstants.NODE);
		if (oldN == null && newN == null) {
			//System.out.println("OK: " + currentAC + " genomicMappings");
			return;
		}

		// change the values below to help testing / finding the problems
		boolean showTrees = true;
		String lines2Ignore = "kdfgjdkfghk"; // keep this value = pattern with
												// no chance to be found
		// lines2Ignore += "|something to ignore";
		XmlComparator.getNodeTreeAsList2(oldN, "original", lines2Ignore, true, showTrees, false, false);
		XmlComparator.getNodeTreeAsList2(newN, "revised", lines2Ignore, true, showTrees, false, false);

		String targets = null;
		String replacs = null;
		boolean result = XmlComparator.compareXmlNodes2("genomicMappings", lines2Ignore, targets, replacs, oldN, newN, false, false, false);
		//System.out.println((result ? "OK" : "ERROR") + " " + currentAC + " genomicMappings");
		String m = result ? "" : "ERROR: " + currentAC + " genomicMappings are not the same in old XML";
		if (m.length() > 0) {
			errMsg.append("\n");
			errMsg.append(m);
		}
		if (errMsg.length() > 0)
			fail(errMsg.toString());
	}

	@Test
	public void testProteinExistence() throws Exception {
		// exemples au 10.07.2013
		// level 1: "NX_A0A5B9", "NX_A0AUZ9",
		// level 2: "NX_B7Z6K7", "NX_P86496",
		// level 3: "NX_P0CG01", "NX_B4DJY2",
		// level 4: "NX_A4D0T2", "NX_A4D263",
		// level 5: "NX_Q9BYX7", "NX_Q9Y4M8"

		String[] specACs = { "NX_A0A5B9", "NX_A0AUZ9", "NX_B7Z6K7", "NX_P86496", "NX_P0CG01", "NX_B4DJY2", "NX_A4D0T2", "NX_A4D263", "NX_Q9BYX7", "NX_Q9Y4M8" };
		StringBuffer errMsg = new StringBuffer("");
		StringBuffer infoMsg = new StringBuffer("");
		for (String ac : specACs) {
			this.currentAC = ac;
			this.oldDoc = getOldXMLDocument(currentAC);
			this.newDoc = getNewXMLDocument(currentAC);

			XPathFactory factory = XPathFactory.newInstance();
			XPath xpath = factory.newXPath();
			Node oldN = (Node) xpath.evaluate("/nextprotExport/proteins/protein/proteinExistence", oldDoc, XPathConstants.NODE);
			Node newN = (Node) xpath.evaluate("/neXtProtExport/proteins/protein/proteinExistence", newDoc, XPathConstants.NODE);
			// change the values below to help testing / finding the problems
			boolean maskErrors = true;
			String lines2Ignore = "kdfgjdkfghk"; // keep this value = pattern
													// with no chance to be
													// found
			if (maskErrors) {
				// lines2Ignore += "|something to hide";
			}
			List<String> lines;
			lines = XmlComparator.getNodeTreeAsList2(oldN, "original", lines2Ignore, true, false, false, false);
			for (String l : lines)
				infoMsg.append(l + "\n");
			lines = XmlComparator.getNodeTreeAsList2(newN, "revised", lines2Ignore, true, false, false, false);
			for (String l : lines)
				infoMsg.append(l + "\n");

			boolean result = XmlComparator.compareXmlNodes2("proteinExistence", lines2Ignore, oldN, newN, true, false);
			//System.out.println((result ? "OK" : "ERROR") + " " + currentAC + " proteinExistence");
			String m = result ? "" : "ERROR: " + currentAC + " proteinExistence are not the same in old XML";

			if (m.length() > 0) {
				errMsg.append("\n");
				errMsg.append(m);
			}
		}
		//System.out.println(infoMsg.toString());
		if (errMsg.length() > 0)
			fail(errMsg.toString());
	}

	/*
	 * OK, 23.07.2013 fails on the 11.07.2013 still fails on the 17.07.2013 even
	 * after patch on view synonyms of type "enzyme name" should be added to the
	 * view_master_identifier_names queried by the service (see with Anne) see
	 * old XML of NX_P48730 for XML structure to create
	 */
	@Test
	public void testProteinNames() throws Exception {
		String[] specACs = { "NX_P48730", "NX_A0A5B9", "NX_A0AUZ9", "NX_P18074", "NX_P01130", "NX_Q03001", "NX_P27695" }; // OK;
																															// 23.07.2013
		// String[] specACs = {"NX_P48730"};
		StringBuffer errMsg = new StringBuffer("");
		StringBuffer infoMsg = new StringBuffer("");

		for (String ac : specACs) {
			this.currentAC = ac;
			this.oldDoc = getOldXMLDocument(currentAC);
			this.newDoc = getNewXMLDocument(currentAC);

			XPathFactory factory = XPathFactory.newInstance();
			XPath xpath = factory.newXPath();
			Node oldN = (Node) xpath.evaluate("/nextprotExport/proteins/protein/proteinNames", oldDoc, XPathConstants.NODE);
			Node newN = (Node) xpath.evaluate("/neXtProtExport/proteins/protein/proteinNames", newDoc, XPathConstants.NODE);
			// change the values below to help testing / finding the problems
			boolean maskErrors = true;
			String lines2Ignore = "kdfgjdkfghk"; // keep this value = pattern
													// with no chance to be
													// found
			if (maskErrors) {
				// lines2Ignore += "|something to hide";
			}
			List<String> lines;
			lines = XmlComparator.getNodeTreeAsList2(oldN, "original", lines2Ignore, true, false, false, false);
			for (String l : lines)
				infoMsg.append(l + "\n");
			lines = XmlComparator.getNodeTreeAsList2(newN, "revised", lines2Ignore, true, false, false, false);
			for (String l : lines)
				infoMsg.append(l + "\n");

			boolean result = XmlComparator.compareXmlNodes2("proteinNames", lines2Ignore, oldN, newN, false, false);
			//System.out.println((result ? "OK" : "ERROR") + " " + currentAC + " proteinNames");
			String m = result ? "" : "ERROR: " + currentAC + " proteinNames are not the same in old XML";

			if (m.length() > 0) {
				errMsg.append("\n");
				errMsg.append(m);
			}
		}
		//System.out.println(infoMsg.toString());
		if (errMsg.length() > 0)
			fail(errMsg.toString());
	}

	@Test
	public void testHistory() throws Exception {
		/*
		 * String[] specACs = {"NX_P48730", "NX_A0A5B9", "NX_P00519",
		 * "NX_P21802", "NX_P00533", "NX_P36897", "NX_P22607", "NX_P35968",
		 * "NX_P05067", "NX_P29590", "NX_P54646", "NX_Q13131", "NX_O95271",
		 * "NX_P00387", "NX_P18074", "NX_P01130", "NX_Q03001", "NX_P27695",
		 * "NX_A0AUZ9"};
		 */
		String[] specACs = { "NX_P00533", "NX_P48730", "NX_A0A5B9", "NX_P00519", "NX_P01130", "NX_Q03001", "NX_P27695" }; // OK,
																															// 23.07.2013
		StringBuffer errMsg = new StringBuffer("");
		StringBuffer infoMsg = new StringBuffer("");

		for (String ac : specACs) {
			this.currentAC = ac;
			this.oldDoc = getOldXMLDocument(currentAC);
			this.newDoc = getNewXMLDocument(currentAC);

			XPathFactory factory = XPathFactory.newInstance();
			XPath xpath = factory.newXPath();
			Node oldN = (Node) xpath.evaluate("/nextprotExport/proteins/protein/history", oldDoc, XPathConstants.NODE);
			Node newN = (Node) xpath.evaluate("/neXtProtExport/proteins/protein/history", newDoc, XPathConstants.NODE);
			// change the values below to help testing / finding the problems
			boolean maskErrors = true;
			String lines2Ignore = "kdfgjdkfghk"; // keep this value = pattern
													// with no chance to be
													// found
			String m = "";
			if (maskErrors) {
				// lines2Ignore += "|something to hide";
			}
			boolean result = XmlComparator.compareXmlNodes2("history", lines2Ignore, oldN, newN, false, false);
			if (result) {
				//System.out.println("OK: " + currentAC + " history");
				infoMsg.append("OK: " + currentAC + " history\n");
			} else {
				//System.out.println("ERROR: " + currentAC + " history");
				m = "ERROR: " + currentAC + " history";
				List<String> lines;
				m = m + "\nDetails:\n";
				lines = XmlComparator.getNodeTreeAsList2(oldN, "original", lines2Ignore, true, false, false, false);
				for (String l : lines)
					m = m + "\n" + l;
				lines = XmlComparator.getNodeTreeAsList2(newN, "revised", lines2Ignore, true, false, false, false);
				for (String l : lines)
					m = m + "\n" + l;
			}
			if (m.length() > 0) {
				errMsg.append("\n");
				errMsg.append(m);
			}
		}
		//System.out.println("Summary:");
		//System.out.println(infoMsg.toString());
		if (errMsg.length() > 0)
			fail(errMsg.toString());
	}

	@Test
	public void testGeneNames() throws Exception {
		// exemples au 11.07.2013
		/*
		 * String[] specACs = {"NX_P48730", "NX_A0A5B9", "NX_P00519",
		 * "NX_P21802", "NX_P00533", "NX_P36897", "NX_P22607", "NX_P35968",
		 * "NX_P05067", "NX_P29590", "NX_P54646", "NX_Q13131", "NX_O95271",
		 * "NX_P00387", "NX_P18074", "NX_P01130", "NX_Q03001", "NX_P27695",
		 * "NX_A0AUZ9"};
		 */
		// String[] specACs = {"NX_P00533"}; // problem UTF-8 ==> solved
		String[] specACs = { "NX_P48730", "NX_P00533", "NX_A0A5B9", "NX_P00519" }; // OK,
																					// 23.07.2013
		StringBuffer errMsg = new StringBuffer("");
		StringBuffer infoMsg = new StringBuffer("");

		for (String ac : specACs) {
			this.currentAC = ac;
			this.oldDoc = getOldXMLDocument(currentAC);
			this.newDoc = getNewXMLDocument(currentAC);

			XPathFactory factory = XPathFactory.newInstance();
			XPath xpath = factory.newXPath();
			Node oldN = (Node) xpath.evaluate("/nextprotExport/proteins/protein/geneNames", oldDoc, XPathConstants.NODE);
			Node newN = (Node) xpath.evaluate("/neXtProtExport/proteins/protein/geneNames", newDoc, XPathConstants.NODE);
			// change the values below to help testing / finding the problems
			boolean maskErrors = true;
			String lines2Ignore = "kdfgjdkfghk"; // keep this value = pattern
													// with no chance to be
													// found
			if (maskErrors) {
				// lines2Ignore += "|something to hide";
			}
			List<String> lines;
			lines = XmlComparator.getNodeTreeAsList2(oldN, "original", lines2Ignore, true, false, false, false);
			for (String l : lines)
				infoMsg.append(l + "\n");
			lines = XmlComparator.getNodeTreeAsList2(newN, "revised", lines2Ignore, true, false, false, false);
			for (String l : lines)
				infoMsg.append(l + "\n");

			boolean result = XmlComparator.compareXmlNodes2("geneNames", lines2Ignore, oldN, newN, false, false);
			//System.out.println((result ? "OK" : "ERROR") + " " + currentAC + " geneNames");
			String m = result ? "" : "ERROR: " + currentAC + " geneNames are not the same in old XML";

			if (m.length() > 0) {
				errMsg.append("\n");
				errMsg.append(m);
			}
		}
		//System.out.println(infoMsg.toString());
		if (errMsg.length() > 0)
			fail(errMsg.toString());
	}

	@Test
	public void testFamilies() throws Exception {

		String[] specACs = { "NX_P48730", "NX_A0A5B9", "NX_P00519", "NX_P21802", "NX_P22607", "NX_P35968", "NX_P05067",
				// "NX_P00533",
				// "NX_P36897", "NX_P22607", "NX_P35968", "NX_P05067",
				// "NX_P29590",
				// "NX_P54646", "NX_Q13131", "NX_O95271", "NX_P00387",
				// "NX_P18074", "NX_P01130", "NX_Q03001", "NX_P27695",
				"NX_A0AUZ9" };
		// String[] specACs = {"NX_P48730"};
		StringBuffer errMsg = new StringBuffer("");
		StringBuffer infoMsg = new StringBuffer("");

		for (String ac : specACs) {
			this.currentAC = ac;
			this.oldDoc = getOldXMLDocument(currentAC);
			this.newDoc = getNewXMLDocument(currentAC);

			XPathFactory factory = XPathFactory.newInstance();
			XPath xpath = factory.newXPath();
			Node oldN = (Node) xpath.evaluate("/nextprotExport/proteins/protein/families", oldDoc, XPathConstants.NODE);
			Node newN = (Node) xpath.evaluate("/neXtProtExport/proteins/protein/families", newDoc, XPathConstants.NODE);
			boolean maskErrors = true;
			String lines2Ignore = "kdfgjdkfghk"; // keep this value = pattern
													// with no chance to be
													// found
			if (maskErrors) {
				// lines2Ignore += "|something to hide";
			}
			String m = "";
			// special case: if no familes Node is found in old XML whilst a
			// families Node is found in the new XML
			// then the test is OK only if the families found has no child
			// Nodes.
			if (oldN == null && newN != null) {
				//System.out.println(m);
				List<String> lines = XmlComparator.getNodeTreeAsList2(newN, "revised", lines2Ignore, false, false, false, false);
				if (lines.size() == 2 && lines.get(1).equalsIgnoreCase("-families")) {
					String info = "OK: " + currentAC + " families node not found in old XML and found node without children in new XML";
					//System.out.println(info);
					infoMsg.append(info + "\n");
				} else {
					m = "ERROR: " + currentAC + " families node not found in old XML but found in new XML";
					//System.out.println(m);
				}

			} else if (oldN != null && newN == null) {
				m = "ERROR: " + currentAC + " families node found in old XML but not found in new XML";
				//System.out.println(m);

			} else if (oldN != null && newN != null) {
				// change the values below to help testing / finding the
				// problems
				boolean result = XmlComparator.compareXmlNodes2("families", lines2Ignore, oldN, newN, false, false);
				m = result ? "" : "ERROR: " + currentAC + " families are not the same in old XML";
				//System.out.println((result ? "OK" : "ERROR") + " " + currentAC + " families");
				if (result) {
					infoMsg.append("OK: " + currentAC + " families\n");
				} else {
					m += " - details:";
					List<String> lines;
					lines = XmlComparator.getNodeTreeAsList2(oldN, "original", lines2Ignore, true, false, false, false);
					for (String l : lines)
						m = m + "\n" + l;
					lines = XmlComparator.getNodeTreeAsList2(newN, "revised", lines2Ignore, true, false, false, false);
					for (String l : lines)
						m = m + "\n" + l;
				}

			} else {
				//System.out.println("OK " + currentAC + ": no families found");
				infoMsg.append("OK " + currentAC + ": no families found\n");
			}

			if (m.length() > 0) {
				errMsg.append("\n");
				errMsg.append(m);
			}

		}
		//System.out.println("Summary:");
		//System.out.println(infoMsg.toString());
		if (errMsg.length() > 0)
			fail(errMsg.toString());
	}

	@Test
	public void testMissingAnnotationTypes() throws Exception {
		StringBuffer errMsg = new StringBuffer("");
		XPathFactory factory = XPathFactory.newInstance();
		XPath xpath = factory.newXPath();
		NodeList oldNl = (NodeList) xpath.evaluate("/nextprotExport/proteins/protein/annotations/annotationList", oldDoc, XPathConstants.NODESET);
		for (int i = 0; i < oldNl.getLength(); i++) {
			Node n = oldNl.item(i);
			String categ = n.getAttributes().getNamedItem("category").getNodeValue();
			NodeList list = (NodeList) xpath.evaluate("/nextprotExport/proteins/protein/annotations/annotationList[@category='" + categ + "']/annotation",
					oldDoc, XPathConstants.NODESET);
			//System.out.println("expected category: " + categ);
			NodeList list2 = (NodeList) xpath.evaluate("/neXtProtExport/proteins/protein/annotations/annotationList[@category='" + categ + "']/annotation",
					newDoc, XPathConstants.NODESET);
			String m = "";
			if (list2 == null) {
				m = "annotationList with category=" + categ + " expected but not found";
				//System.out.println(m);
			} else if (list.getLength() != list2.getLength()) {
				if (categ.equalsIgnoreCase("tissue specificity") && list2.getLength() < list.getLength()) {
					// in the XML new version, we just include annotations
					// related to the term explicitly related to it. We don't
					// generate
					// annotations for parent terms as done in the old version
					// of the XML.
					//System.out.println("WARNING: annotationList category=" + categ + " has " + list2.getLength() + " elements but " + list.getLength()+ " were expected");
				} else {
					m = "annotationList with category=" + categ + " has " + list2.getLength() + " elements but " + list.getLength() + " were expected";
					//System.out.println(m);
				}
			} else {
				//System.out.println("annotationList category=" + categ + ": OK");
			}
			if (m.length() > 0) {
				errMsg.append("\n");
				errMsg.append(m);
			}
		}
		if (errMsg.length() > 0)
			fail(errMsg.toString());
	}

	@Test
	public void testExtraAnnotationTypes() throws Exception {
		StringBuffer errMsg = new StringBuffer("");
		//System.out.println("Starting findExtraAnnotationTypes()");
		XPathFactory factory = XPathFactory.newInstance();
		XPath xpath = factory.newXPath();
		NodeList newNl = (NodeList) xpath.evaluate("/neXtProtExport/proteins/protein/annotations/annotationList", newDoc, XPathConstants.NODESET);
		for (int i = 0; i < newNl.getLength(); i++) {
			String categ = newNl.item(i).getAttributes().getNamedItem("category").getNodeValue();
			Node n = (Node) xpath.evaluate("/nextprotExport/proteins/protein/annotations/annotationList[@category='" + categ + "']", oldDoc,
					XPathConstants.NODE);
			//System.out.println("annotationList with category=" + categ + " found");
			String m = "";
			if (n == null) {
				// if (categ.equals("") || categ.equals("") || categ.equals(""))
				// {}
				m = "annotationList with category=" + categ + " found but not expected !";
				//System.out.println(m);
			} else {
				//System.out.println("annotationList with category=" + categ + " was expected: OK");
			}
			if (m.length() > 0) {
				errMsg.append("\n");
				errMsg.append(m);
			}
		}
		if (errMsg.length() > 0)
			fail(errMsg.toString());
	}

}