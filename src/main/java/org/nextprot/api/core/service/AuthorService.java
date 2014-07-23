package org.nextprot.api.core.service;

import java.util.List;

import org.nextprot.api.core.domain.PublicationAuthor;


public interface AuthorService {

	/**
	 * Gets a list of authors by publication id
	 * 
	 * @param title
	 * @return
	 */
	public List<PublicationAuthor> findAuthorsByPublicationId(Long publicationId);

}
