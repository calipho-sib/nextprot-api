package org.nextprot.api.core.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.nextprot.api.core.domain.DbXref;

public class XrefUtils {
	
	/**
	 * Filter xrefs by their ids
	 * @param annotations
	 * @param annotationCategory
	 * @return
	 */
	public static List<DbXref> filterXrefsByIds(List<DbXref> xrefs, Set<Long> ids){
		List<DbXref> xrefsFiltered = new ArrayList<DbXref>();
		for(DbXref xref : xrefs){
			if(ids.contains(xref.getDbXrefId())){
				xrefsFiltered.add(xref);
			}
		}
		return xrefsFiltered;
	}
	

}
