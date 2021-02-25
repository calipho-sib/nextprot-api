package org.nextprot.api.core.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.nextprot.api.core.domain.DbXref;

public class XrefUtils {

	/**
	 * Returns the list of xrefs for which the id is found in @param ids
	 * @param xrefs a list of xrefs
	 * @param ids a set of xref ids
	 * @return the filtered list of xrefs
	 */
	public static List<DbXref> filterXrefsByIds(List<DbXref> xrefs, Set<Long> ids) {
		List<DbXref> xrefsFiltered = new ArrayList<DbXref>();
		for (DbXref xref : xrefs) {
			if (ids.contains(xref.getDbXrefId())) {
				xrefsFiltered.add(xref);
			}
		}
		return xrefsFiltered;
	}

	/**
	 * Returns the list of xrefs for which the id is NOT found in @param ids
	 * @param xrefs a list of xrefs
	 * @param ids a set of xref ids
	 * @return the filtered list of xrefs
	 */
	public static List<DbXref> filterOutXrefsByIds(List<DbXref> xrefs, Set<Long> ids) {
		List<DbXref> xrefsFiltered = new ArrayList<DbXref>();
		for (DbXref xref : xrefs) {
			if ( ! ids.contains(xref.getDbXrefId())) {
				xrefsFiltered.add(xref);
			}
		}
		return xrefsFiltered;
	}

	public static List<DbXref> filterOutHpaENSGXrefs(List<DbXref> xrefs) {
		List<DbXref> xrefsFiltered = new ArrayList<DbXref>();
		for (DbXref xref : xrefs) {
			if (xref.getDatabaseName().equals("HPA") && xref.getAccession().startsWith("ENSG")) continue;
			xrefsFiltered.add(xref);
		}
		return xrefsFiltered;
	}

	
	
}
