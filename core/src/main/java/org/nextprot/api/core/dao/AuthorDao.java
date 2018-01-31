package org.nextprot.api.core.dao;

import org.nextprot.api.core.domain.PublicationAuthor;

import java.util.List;

public interface AuthorDao {
	
	List<PublicationAuthor> findAuthorsByPublicationId(Long publicationId);
	
	List<PublicationAuthor> findAuthorsByPublicationIds(List<Long> publicationIds);
}
