package org.nextprot.api.service;

import java.util.List;

import org.nextprot.api.domain.PublicationAuthor;


public interface AuthorService {

	/**
	 * Gets a list of authors by publication id
	 * 
	 * @param title
	 * @return
	 */
	public List<PublicationAuthor> findAuthorsByPublicationId(Long publicationId);

}
