package org.nextprot.api.core.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Map;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.commons.utils.Tree;
import org.nextprot.api.core.aop.InstrumentationAspect;
import org.nextprot.api.core.domain.DbXref;
import org.nextprot.api.core.domain.Terminology;
//import org.nextprot.api.core.domain.TerminologyProperty;
import org.nextprot.api.core.service.TerminologyService;

public class TerminologyUtils {

	private static final Log LOGGER = LogFactory.getLog(TerminologyUtils.class);

	public static List<Terminology.TermProperty> convertToProperties(String propliststring, Long termid, String termacc) {

		if (propliststring == null) return null;
		// Decomposes a pipe-separated string (generated by a SQL query) in a list property objects  containing  name/value pairs
		List<Terminology.TermProperty> properties = new ArrayList<Terminology.TermProperty>();
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
			Terminology.TermProperty property = new Terminology.TermProperty();
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
		
		//if(cvterm.contains("TS-0576")) System.err.println("lookin for ancestors of " + cvterm);
		while(mylist.size() > 0) {
			mylist = terminologyservice.findTerminologyByAccession(currTerm).getAncestorAccession();
			if(mylist == null) break;
			if(mylist.size() > 1) for (int i=1; i<mylist.size(); i++) multiParentSet.add(mylist.get(i));
			currTerm = mylist.get(0);
			finalSet.add(currTerm);
		}
		
		while(!multiParentSet.isEmpty()) {
			multiSetCurrent.clear();
			multiSetCurrent.addAll(multiParentSet);
			for(String cv : multiSetCurrent) {
				finalSet.add(cv);
				multiParentSet.remove(cv);
				mylist = terminologyservice.findTerminologyByAccession(cv).getAncestorAccession();
				if(mylist == null) break;
				while(mylist != null && mylist.size() > 0) {
				if(mylist.size() > 1) for (int i=1; i<mylist.size(); i++) multiParentSet.add(mylist.get(i));
				currTerm = mylist.get(0);
				finalSet.add(currTerm);
				mylist = terminologyservice.findTerminologyByAccession(currTerm).getAncestorAccession();
				}
			}
		}
		
		return(new ArrayList<String>(finalSet));	
	}
	
	public static List<DbXref> convertToXrefs (String xrefsstring) {

		if (xrefsstring == null) return null;
		// Builds DbXref list from String of xrefs formatted as "dbcat, db, acc, linkurl" quartetss separated by pipes
		List<DbXref> xrefs = new ArrayList<DbXref>();
		List<String> allxrefs = Arrays.asList(xrefsstring.split(" \\| "));
		for (String onexref: allxrefs) {
			
			List<String> fields = Arrays.asList(onexref.split(", "));
			String dbcat = fields.get(0);
			String db = fields.get(1);
			String acc = fields.get(2);
			String linkurl = "nolink.org/%s";
			if(fields.size() > 3) {linkurl = fields.get(3);}
			//else {System.err.println("No link for: " + onexref);}
			DbXref dbref = new DbXref();
			dbref.setDatabaseName(db);
			dbref.setAccession(acc);
			dbref.setDatabaseCategory(dbcat);
			dbref.setLinkUrl(linkurl);
			//dbref.setDbXrefId(?);
			xrefs.add(dbref);
			}
		return xrefs;
		
	}
	
	public static String convertPropertiesToString(List<Terminology.TermProperty> properties) {

		if (properties == null) return null;
		// Build a String where propertyname/propertyvalue pairs are separated by pipes
		StringBuilder sb = new StringBuilder();
        int i = properties.size();
		for (Terminology.TermProperty property : properties) {
			sb.append(property.getPropertyName());
			sb.append(":=");
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

	public static Map<String, Terminology> convertToTerminologyMap(List<Terminology> terms) {
		Map<String, Terminology> termMap = new HashMap<>();
		for(Terminology term: terms){
			termMap.put(term.getAccession(), term);
		}
		return termMap;
	}

		
	public static List<Tree<Terminology>> convertTerminologyListToTreeList(List<Terminology> terms, final int maxDepth) {
		
		List<Tree<Terminology>> trees = new ArrayList<Tree<Terminology>>();
		
		for(Terminology term: terms){
			if((term.getAncestorAccession() == null) || (term.getAncestorAccession().isEmpty())){
				trees.add(new Tree<Terminology>(term));
			}
		}
		
		for(Tree<Terminology> tree : trees){
			populateTree(tree.getRoot(), convertToTerminologyMap(terms), 0, maxDepth);
		}
		
		return trees;
		
	}
	
	static void populateTree(Tree.Node<Terminology> currentNode, Map<String, Terminology> termMap, int depth, final int maxDepth) {

		if(depth > maxDepth) return;

		if(depth > 100) throw new NextProtException("Getting stuck in building graph");
		
		if(currentNode.getValue() == null || currentNode.getValue().getChildAccession() == null || currentNode.getValue().getChildAccession().isEmpty()) {
			return;
		}
		
		for(String childAccession : currentNode.getValue().getChildAccession()){
			Terminology childTerm = termMap.get(childAccession);
			if(currentNode.getChildren() == null){
				currentNode.setChildren(new ArrayList<Tree.Node<Terminology>>());
			}
			Tree.Node<Terminology> childNode = new Tree.Node<Terminology>(childTerm);
			currentNode.getChildren().add(childNode);
			
			populateTree(childNode, termMap, depth+1, maxDepth);
		}
		
	}

}
