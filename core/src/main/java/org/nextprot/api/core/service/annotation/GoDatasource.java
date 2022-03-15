package org.nextprot.api.core.service.annotation;

import java.util.HashMap;
import java.util.Map;


/**
 * 
 * @author pmichel
 * Used to display datasource for evidences collected by GO
 * The values returned by the method should be used for display only cos it has no corresponding record in the cv_datasources table
 * The value is added to the evidence.goAssignedBy field at load time
 */
public class GoDatasource {

	private static Map<String, String> ref2src;
	static { init(); }
	
	public static String getGoAssignedBy(String goref) {
		return ref2src.get(goref);
	}
	
	private static void init() {
		
		ref2src = new HashMap<>();
		
		ref2src.put("GO_REF:0000001", "GO consortium");
		ref2src.put("GO_REF:0000002", "InterPro 2 GO");
		ref2src.put("GO_REF:0000003", "EC 2 GO");
		ref2src.put("GO_REF:0000004", "UniProtKB KW");
		ref2src.put("GO_REF:0000011", "TIGR");
		ref2src.put("GO_REF:0000012", "TIGR");
		ref2src.put("GO_REF:0000015", "ND");
		ref2src.put("GO_REF:0000019", "Ortholog Compara");
		ref2src.put("GO_REF:0000020", "UniProtKB Annot");
		ref2src.put("GO_REF:0000023", "UniProtKB SubCell");
		ref2src.put("GO_REF:0000024", "Ortholog Curator");
		ref2src.put("GO_REF:0000029", "UniProtKB Annot");
		ref2src.put("GO_REF:0000030", "JCVI");
		ref2src.put("GO_REF:0000031", "NIAID");
		ref2src.put("GO_REF:0000032", "Berkeley BOP");
		ref2src.put("GO_REF:0000033", "RefGenome");
		ref2src.put("GO_REF:0000036", "GO curators");
		ref2src.put("GO_REF:0000037", "UniProtKB KW");
		ref2src.put("GO_REF:0000038", "UniProtKB KW");
		ref2src.put("GO_REF:0000039", "UniProtKB SubCell");
		ref2src.put("GO_REF:0000040", "UniProtKB SubCell");
		ref2src.put("GO_REF:0000041", "UniPathway");
		ref2src.put("GO_REF:0000042", "UniProtKB annot");
		ref2src.put("GO_REF:0000043", "UniProtKB annot");
		ref2src.put("GO_REF:0000044", "UniProtKB annot");
		ref2src.put("GO_REF:0000045", "UniProtKB annot");
		ref2src.put("GO_REF:0000046", "UniProtKB annot");
		ref2src.put("GO_REF:0000047", "GO curators");
		ref2src.put("GO_REF:0000052", "HPA subcell");
		ref2src.put("GO_REF:0000053", "GO curators");
		ref2src.put("GO_REF:0000054", "LIFEdb");
		ref2src.put("GO_REF:0000056", "GO consortium");
		ref2src.put("GO_REF:0000057", "GO apoptosis working group");
		ref2src.put("GO_REF:0000096", "Mouse Genome Informatics scientific curators");
		ref2src.put("GO_REF:0000107", "GOA curators");
		ref2src.put("GO_REF:0000108", "GOA curators");
		ref2src.put("GO_REF:0000111", "GOA curators");
		ref2src.put("GO_REF:0000113", "GOA curators");
		ref2src.put("GO_REF:0000116", "GO Central curators, GOA curators, Rhea curators");
		ref2src.put("GO_REF:0000117", "Electronic Gene Ontology annotations created by ARBA machine learning models");
	}
}
