package org.nextprot.api.core.service.impl;

import org.nextprot.api.core.dao.AuthorDao;
import org.nextprot.api.core.domain.PublicationAuthor;
import org.nextprot.api.core.service.AuthorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;


@Lazy
@Service
class AuthorServiceImpl implements AuthorService {

	@Autowired
	private AuthorDao authorDAO;

	@Override
	public List<PublicationAuthor> findAuthorsByPublicationId(Long publicationId) {
		return authorDAO.findAuthorsByPublicationId(publicationId);
	}

	@Override
	public List<PublicationAuthor> findAuthorsByPublicationIds(List<Long> publicationIds) {
		return authorDAO.findAuthorsByPublicationIds(publicationIds);
	}
}
