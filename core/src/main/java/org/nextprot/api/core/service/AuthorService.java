package org.nextprot.api.core.service;

import org.nextprot.api.core.domain.PublicationAuthor;

import java.util.List;


public interface AuthorService {

	/**
	 * Gets a list of authors by publication id
	 * 
	 * @param title
	 * @return
	 */
	List<PublicationAuthor> findAuthorsByPublicationId(Long publicationId);

	List<PublicationAuthor> findAuthorsByPublicationIds(List<Long> publicationIds);
}
