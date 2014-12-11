package org.nextprot.api.core.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.nextprot.api.core.domain.Publication;

public class PublicationUtils {
	
	/**
	 * Filter publications by their ids
	 * @param annotations
	 * @param annotationCategory
	 * @return
	 */
	public static List<Publication> filterPublicationsByIds(List<Publication> publications, Set<Long> ids){
		List<Publication> publicationsFiltered = new ArrayList<Publication>();
		for(Publication pub : publications){
			if(ids.contains(pub.getPublicationId())){
				publicationsFiltered.add(pub);
			}
		}
		return publicationsFiltered;
	}


}
