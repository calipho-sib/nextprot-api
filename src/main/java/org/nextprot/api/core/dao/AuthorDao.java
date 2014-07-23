package org.nextprot.api.core.dao;

import java.util.List;

import org.nextprot.api.core.domain.PublicationAuthor;

public interface AuthorDao {
	
	List<PublicationAuthor> findAuthorsByPublicationId(Long publicationId);
	
	List<PublicationAuthor> findAuthorsByPublicationIds(List<Long> publicationIds);

}
