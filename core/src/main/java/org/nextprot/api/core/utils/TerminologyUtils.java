package org.nextprot.api.core.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.commons.utils.Tree;
import org.nextprot.api.commons.utils.Tree.Node;
import org.nextprot.api.core.domain.CvTerm;
import org.nextprot.api.core.domain.DbXref;
import org.nextprot.api.core.domain.Terminology;
import org.nextprot.api.core.service.TerminologyService;

import java.util.*;

//import org.nextprot.api.core.domain.TerminologyProperty;

public class TerminologyUtils {

	private static final Log LOGGER = LogFactory.getLog(TerminologyUtils.class);

	public static List<CvTerm.TermProperty> convertToProperties(String propliststring, Long termid, String termacc) {

		if (propliststring == null) return null;
		// Decomposes a pipe-separated string (generated by a SQL query) in a list property objects  containing  name/value pairs
		List<CvTerm.TermProperty> properties = new ArrayList<CvTerm.TermProperty>();
		// keep spaces in splitting pattern, since pipe alone can occur within fields
		List<String> allprop = Arrays.asList(propliststring.split(" \\| "));
		
		for (String propertystring : allprop) {
			// The splitter is ':=' since both ':' and '=' can occur alone within field
			List<String> currprop = Arrays.asList(propertystring.split(":="));
			if(currprop.size() != 2) {
			   String msg = "Problem with property in " + termacc + ": " + propertystring + " propString: " + propertystring;
			   LOGGER.warn(msg);
			   System.err.println(msg);
			   continue;
			}
			CvTerm.TermProperty property = new CvTerm.TermProperty();
			String propertyName = currprop.get(0) ;
			property.setPropertyName(propertyName);
			String propertyValue = currprop.get(1);
			property.setPropertyValue(propertyValue);
			property.settermId(termid);
			properties.add(property);
		}
		return properties;
	}

	public static List<String> getAllAncestors(String cvterm, TerminologyService terminologyservice) {
		Set<String> finalSet = new TreeSet<String>();
		Set<String> multiParentSet = new TreeSet<String>();
		Set<String> multiSetCurrent = new TreeSet<String>();
		List<String> mylist = Arrays.asList("XXX");
		String currTerm = cvterm;

		while(!mylist.isEmpty()) {
			CvTerm cvt = terminologyservice.findCvTermByAccession(currTerm);
			if (cvt == null ) break;

			mylist = cvt.getAncestorAccession();
			if(mylist == null) break;
			if(mylist.size() > 1) for (int i=1; i<mylist.size(); i++) multiParentSet.add(mylist.get(i));

			// when root loop on itself !
			if (currTerm.equals(mylist.get(0)))
				break;
			currTerm = mylist.get(0);
			finalSet.add(currTerm);
		}
		
		while(!multiParentSet.isEmpty()) {
			multiSetCurrent.clear();
			multiSetCurrent.addAll(multiParentSet);
			for(String cv : multiSetCurrent) {
				finalSet.add(cv);
				multiParentSet.remove(cv);
				mylist = terminologyservice.findCvTermByAccession(cv).getAncestorAccession();
				if(mylist == null) break;
				while(mylist != null && !mylist.isEmpty()) {
					if(mylist.size() > 1)
						for (int i=1; i<mylist.size(); i++)
							multiParentSet.add(mylist.get(i));
					// when root loop on itself !
					if (currTerm.equals(mylist.get(0)))
						break;
					currTerm = mylist.get(0);
					finalSet.add(currTerm);
					mylist = terminologyservice.findCvTermByAccession(currTerm).getAncestorAccession();
				}
			}
		}
		
		return(new ArrayList<>(finalSet));
	}
	
	public static List<DbXref> convertToXrefs (String xrefsstring) {

		if (xrefsstring == null) return null;
		// Builds DbXref list from String of xrefs formatted as "dbcat, db, acc, linkurl" quartetss separated by pipes
		List<DbXref> xrefs = new ArrayList<>();
		List<String> allxrefs = Arrays.asList(xrefsstring.split(" \\| "));
		for (String onexref: allxrefs) {			
			List<String> fields = Arrays.asList(onexref.split("\\^ "));

			DbXref dbref = new DbXref();

			dbref.setDatabaseCategory(fields.get(0));
			dbref.setDatabaseName(fields.get(1));
			dbref.setAccession(fields.get(2));
			dbref.setDbXrefId(Long.parseLong(fields.get(3)));

			String url = null;
			String linkurl = null;

			if (fields.size() > 4) {
				url = fields.get(4);
				if (fields.size() > 5)
					linkurl = fields.get(5);
			}

			if (url == null || url.isEmpty() || "none".equalsIgnoreCase(url)) {
				dbref.setUrl("None");
				dbref.setLinkUrl("None");
			}
			else {
				dbref.setUrl(url);
				dbref.setLinkUrl(linkurl);
			}
			xrefs.add(dbref);
		}

		return xrefs;
		
	}
	
	public static String convertPropertiesToString(List<CvTerm.TermProperty> properties) {

		if (properties == null) return null;
		// Build a String where propertyname/propertyvalue pairs are separated by pipes
		StringBuilder sb = new StringBuilder();
        int i = properties.size();
		for (CvTerm.TermProperty property : properties) {
			sb.append(property.getPropertyName());
			//sb.append(":=");
			sb.append(":");
			sb.append(property.getPropertyValue());
			if(--i != 0)
			  sb.append(" | ");
		}
		return sb.toString();
	}

	public static String convertXrefsToString(List<DbXref> xrefs) {

		if (xrefs == null) return null;
		// Build a String of xrefs formatted as "dbcat, db:acc" pairs separated by pipes
		StringBuilder sb = new StringBuilder();
        int i = xrefs.size();
		for (DbXref xref : xrefs) {
			sb.append(xref.getDatabaseCategory());
			sb.append(", ");
			sb.append(xref.getDatabaseName());
			sb.append(":");
			sb.append(xref.getAccession());
			if(--i != 0)
			  sb.append(" | ");
		}
		return sb.toString();
	}

	public static String convertXrefsToSolrString(List<DbXref> xrefs) {

		if (xrefs == null) return null;
		// Build a String of xrefs for solr formatted as "acc, db:acc" pairs separated by pipes
		StringBuilder sb = new StringBuilder();
        int i = xrefs.size();
		for (DbXref xref : xrefs) {
			sb.append(xref.getAccession());
			sb.append(", ");
			sb.append(xref.getDatabaseName());
			sb.append(":");
			sb.append(xref.getAccession());
			if(--i != 0)
			  sb.append(" | ");
		}
		return sb.toString();
	}

	public static List<String> convertXrefsToSameAsStrings(List<DbXref> xrefs) {

		if (xrefs == null) return null;
		// Build List of strings of xref accessions  as needed for the old Terminology.getSameAs method
		List<String> sameas = new ArrayList<String>();
		for (DbXref xref : xrefs) {
			sameas.add(xref.getAccession());
		}
		return sameas;
	}

	public static Map<String, CvTerm> convertToTerminologyMap(List<CvTerm> terms) {
		Map<String, CvTerm> termMap = new HashMap<>();
		for(CvTerm term: terms){
			termMap.put(term.getAccession(), term);
		}
		return termMap;
	}

		
	public static Terminology convertCvTermsToTerminology(List<CvTerm> terms, final int maxDepth) {
		
		String topLevelTermPrefix = "CVAN";
		
		Terminology terminology = new Terminology();
		
		for(CvTerm term: terms){
			
			
			
			
			//System.err.println(term.getAccession() +  " " + term.getAncestorAccession());
			if((term.getAncestorAccession() == null) || (term.getAncestorAccession().isEmpty())){ //root
				terminology.addTreeRoot(term);
			} else {
				
				//For example DO-00218 from the terminology NextprotDomain, has as ancestor the top level terminology cv annotation (CVAN)
				boolean localRoot = false;
				if(!term.getAccession().startsWith(topLevelTermPrefix)){ //TOP Level domain (case where other terminologies link to this one)
						for(String ancestorAccession : term.getAncestorAccession()){
							if(ancestorAccession.startsWith(topLevelTermPrefix)){
								localRoot = true;
								break;
							}
						}
				}
				
				if(localRoot){
					terminology.addTreeRoot(term);
				}
			}
		}
		
		for(Tree<CvTerm> tree : terminology){
			populateTree(tree.getRoot(), convertToTerminologyMap(terms), 0, maxDepth);
		}
		
		return terminology;
		
	}
	
	static void populateTree(Tree.Node<CvTerm> currentNode, Map<String, CvTerm> termMap, int depth, final int maxDepth) {

		if(depth > maxDepth) return;

		if(depth > 100) throw new NextProtException("Getting stuck in building graph");
		
		if(currentNode.getValue() == null || currentNode.getValue().getChildAccession() == null || currentNode.getValue().getChildAccession().isEmpty()) {
			return;
		}
		
		for(String childAccession : currentNode.getValue().getChildAccession()){
			CvTerm childTerm = termMap.get(childAccession);
			if(childTerm != null) { // may be null in case of the terminology being another one like DO

				if(currentNode.getChildren() == null){
					currentNode.setChildren(new ArrayList<Tree.Node<CvTerm>>());
				}
				
				Tree.Node<CvTerm> childNode = new Tree.Node<CvTerm>(childTerm);
				childNode.setParents(Arrays.asList(currentNode));
				currentNode.getChildren().add(childNode);
				
				populateTree(childNode, termMap, depth+1, maxDepth);
				
			}
		}
		
	}
	
	public static List<Node<CvTerm>> getNodeListByName(Tree<CvTerm> tree, String accession) {
		List<Node<CvTerm>> result = new ArrayList<>();
		getNodeListByNameAndPopulateResult(result, tree.getRoot(), accession);
		return result;
		
	}

	private static void getNodeListByNameAndPopulateResult(List<Node<CvTerm>> currentResult, Node<CvTerm> node, String accession) {

		if(node.getValue().getAccession().equals(accession)){
				currentResult.add(node);
				return;
			}
		
		
		if(node.getChildren() != null && !node.getChildren().isEmpty()){
			for(Node<CvTerm> child : node.getChildren()){
				getNodeListByNameAndPopulateResult(currentResult, child, accession);
			}
		}
	}

}
