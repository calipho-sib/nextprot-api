package org.nextprot.api.dao;

import java.util.List;

import org.nextprot.api.domain.PublicationAuthor;

public interface AuthorDao {
	
	List<PublicationAuthor> findAuthorsByPublicationId(Long publicationId);
	
	List<PublicationAuthor> findAuthorsByPublicationIds(List<Long> publicationIds);

}
