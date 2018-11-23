package org.nextprot.api.commons.utils;

import difflib.Delta;
import difflib.DiffUtils;
import difflib.Patch;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class XmlComparator {

	static public int getLineLevel(String line) {
		String s = line.substring(0,2);
		int level = Integer.parseInt(s);
		return level;
	}
	
	static public List<String> getNodeTreeAsList2(Node n, String parent, String linesToIgnore, boolean sortList, boolean showList, boolean sortUnique, boolean singleAttrByLine) {		
		return getNodeTreeAsList2(n, parent, linesToIgnore, sortList, showList, sortUnique, singleAttrByLine, false);
	}
	
	
	/**
	 * Returns a list of String representing the XML Node n and its sub components (attrs, text node and sub-elements).
	 * The list can optionally be sorted alphabetically (useful for comparing 2 lists when the order of sub-components is not controlled.
	 * The list can optionally be sent to System.out for debug purpose.
	 * The parent String is inserted as the first line of Lines returned and is just used as a title for the list of lines.
	 * @param n the XML node to represent as a List of String
	 * @param parent is displayed as a prefix when the List is written to System.out
	 * @param linesToIgnore pipe separated list of patterns causing the line containing one of them not to be included in the returned String List
	 * @param sortList decides if the list of String has to be sorted before being returned, set it to True when the order of sube-components of Node n is uncontrolled
	 * @param showListdecides if the List is sent to System.out before returning
	 * @return the List of String representing the Node branch
	 */
	static public List<String> getNodeTreeAsList2(Node n, String parent, String linesToIgnore, boolean sortList, boolean showList, boolean sortUnique, boolean singleAttrByLine, boolean ignoreChildrenLinesAsWell) {		
		// generate xml as List of String lines
		List<String> lines = new ArrayList<String>();
		buildNodeTree2(n, "" , null, lines, singleAttrByLine);
		// remove lines matching patterns to ignore
		Set<String> patterns = linesToIgnore==null ? new HashSet<String>() : new HashSet<String>(Arrays.asList(linesToIgnore.toLowerCase().split("\\|")));
		List<String> filteredLines = new ArrayList<String>();
		boolean ignoreLine = false;
		int lastLevelIgnored = 1000;
		for (String line:lines) {
			int level = getLineLevel(line);
			if (level<=lastLevelIgnored && ignoreLine==true) {
				ignoreLine=false;  // reset to false when current level < last level (current line not a child of last line ignored 
				lastLevelIgnored=1000;
			}
			if (ignoreChildrenLinesAsWell==false) ignoreLine=false; // init to false for each line if ignoreChildrenLinesAsWell is false
			for (String pattern: patterns) {
				if (pattern.length()>0 && line.contains(pattern)) {
					if (ignoreLine==false) {
						ignoreLine=true;
						lastLevelIgnored=level;
					}
					break;
				}
			}
			if (!ignoreLine) filteredLines.add(line);
			//if (ignoreLine==true) System.out.println(parent + "ignored line:" + line);
		}
		// apply sort options
		if (sortUnique) {
			filteredLines = new ArrayList<String>(new TreeSet<String>(filteredLines)); // use set to make elements unique and sort them as well
		} else if (sortList) {
			Collections.sort(filteredLines);
		}
		// insert title line at top
		filteredLines.add(0, parent);
		// send to stdout if option is on
		//if (showList) for (int i=0;i<filteredLines.size();i++) System.out.println(parent + " - " + i + " - " + filteredLines.get(i));
		return filteredLines;
	}

	
	/*
	 * Builds a tree by recursively traversing the branch of the node n.
	 * Each XML element becomes a line in lines.
	 * linesToIgnore are String patterns. If one is found in a line during Node conversion it is not included in the String List
	 * The parent should be an empty string if you want to later compare the lines of two different nodes
	 * Use a non empty parent String to tag sub-component of a node for debug purpose. 
	 */
	static protected void buildNodeTree2(Node n, String parent, String level, List<String> lines, boolean singleAttrByLine) {

		// increments level
		if (level==null || level.length()==0) level = "0";
		level = ""+ (Integer.parseInt(level)+1);
		while (level.length()<2) level="0"+level;
		
		String line = parent;
		if (n.getNodeType()!=30) {
			String name = n.getNodeName().toLowerCase();
			line = line + "-" + name ;
			String value = n.getNodeValue();
			if (value==null) value = "";
			value=value.toLowerCase().replaceAll("\\s","").trim();
			if (name.equals("#text") && value.length()==0) return;  // we ALWAYS ignore empty text nodes !
			//if (name.equals("#text")) {}
			if (value.length()>0) line = line + "=" + value; 
		
			if (singleAttrByLine) {
				// one line for the element itself
				lines.add(level + "|" + line);
				// one more line for each attribute of the element
				if (n.hasAttributes()) {
					for (int i=0;i<n.getAttributes().getLength();i++) {
						Node at = n.getAttributes().item(i);
						String atName = at.getNodeName().toLowerCase();
						String atValue = at.getNodeValue().toLowerCase().trim();
						String line2 = line + "-attr-" + atName + "=" + atValue;
						lines.add(level + "|" + line2);
					}
				}
			} else {
				// add all the attr info to the element line
				if (n.hasAttributes()) {
					for (int i=0;i<n.getAttributes().getLength();i++) {
						Node at = n.getAttributes().item(i);
						String atName = at.getNodeName().toLowerCase();
						String atValue = at.getNodeValue().toLowerCase().trim();
						line += "-attr-" + atName + "=" + atValue;
					}
				}
				lines.add(level + "|" + line);
			}
			// recurse to child nodes
			if (n.hasChildNodes()) {
				for (int i=0;i<n.getChildNodes().getLength();i++) {
					buildNodeTree2(n.getChildNodes().item(i), parent + "-" + name, level, lines, singleAttrByLine);
				}
			}
		}

	}
	
	static public List<Delta> compareXmlNodes2AndGetDeltas(String tag, String lines2Ignore, String targetList, String replacmentList, Node n1, Node n2, boolean sortUnique, boolean singleAttrByLine, boolean ignoreChildrenLinesAsWell) {
		List<String> original = getNodeTreeAsList2(n1, "n1-original", lines2Ignore, true, false, sortUnique, singleAttrByLine,ignoreChildrenLinesAsWell);
		List<String> revised =  getNodeTreeAsList2(n2, "n2-revised", lines2Ignore, true, false, sortUnique, singleAttrByLine,ignoreChildrenLinesAsWell);
		filterList(original,  targetList,  replacmentList);
		filterList(revised,  targetList,  replacmentList);
		//showList("original>>>", original);
		//showList("revised>>>", original);
		Patch patch = DiffUtils.diff(original, revised);
	    return patch.getDeltas();
	}
	
	/**
	 * Compares 2 XML nodes after converting them in a sorted List of String.
	 * @param tag is displayed as a prefix when differences are written to System.out
	 * @param lines2Ignore pipe separated list of patterns. When a pattern in a String generated during the Node conversion into a List, the String is not added to the List and thuus ignored later during Node comparison
	 * @param targetList is a String containing values pipe separated to be replaced with replacmentList String in each line
	 * @param replacmentList String are pipe separated values to use in replacment for targetList String
	 * @param n1 Node to be compared
	 * @param n2 Node to be compared with
	 * @param sortUnique sorts the List of String representing the Node and removes doublons (each String in the List is unique)
	 * @param singleAttrByLine generates one String in the List for each attribute of each element 
	 * @return true if no differences are found, else false. The list of differences are sent to System.out.
	 */	
	static public boolean compareXmlNodes2(String tag, String lines2Ignore, String targetList, String replacmentList, Node n1, Node n2, boolean sortUnique, boolean singleAttrByLine, boolean ignoreChildrenLinesAsWell) {
		List<Delta> deltas = compareXmlNodes2AndGetDeltas(tag, lines2Ignore, targetList, replacmentList, n1, n2, sortUnique, singleAttrByLine, ignoreChildrenLinesAsWell);
		boolean status = true;
        for (Delta delta: deltas) {
        	String deltaStr = delta.toString();
        	if (! deltaStr.contains("position: 0")) {
        		//System.out.println(tag + " - delta: " + deltaStr);
        		status = false;
        	}
        }
        return status;
	}

	static void showList(String tag, List<String> list) {
		for (int i=0;i<list.size();i++) {
			System.out.println(tag + " - " + i + " - " + list.get(i));
		}
	}
	
	static public void filterList(List<String> list, String targetList, String replacmentList) {
		if (targetList!=null) {
			String[] targets= targetList.split("\\|");
			//System.out.println("targets size:" + targets.length);
			String[] repls= replacmentList.split("\\|");
			// un-escape my special (empty) value 
			// String.split() doesn't create an element if there is empty string after last sep found !
			for (int i=0;i<repls.length;i++) { if (repls[i].equals("(empty)")) repls[i]=""; } 
			//System.out.println("repls size:" + repls.length);
			for (int i=0;i<list.size();i++) {
				for (int j=0;j<targets.length;j++) {
					String s1 = list.get(i);
					String s2 = s1.replace(targets[j],  repls[j]); 
					String t = targets[j];
					String r = repls[j];
					//System.out.println("filtering line " + i + " pass " + j + " t:<" + t + "> r:<" + r + "> result: " + s2);
					list.set(i,list.get(i).replace(targets[j],  repls[j])); 
				}
			}
		}
	}
	

	static public boolean compareXmlNodes2(String tag, String lines2Ignore, Node n1, Node n2, boolean sortUnique, boolean singleAttrByLine, boolean ignoreChildrenLinesAsWell) {
		return compareXmlNodes2(tag,lines2Ignore,null,null,n1,n2,sortUnique,singleAttrByLine, ignoreChildrenLinesAsWell);
	}
	
	static public boolean compareXmlNodes2(String tag, String lines2Ignore, Node n1, Node n2, boolean sortUnique, boolean singleAttrByLine) {
		return compareXmlNodes2(tag,lines2Ignore,null,null,n1,n2,sortUnique,singleAttrByLine, false);
	}

}
