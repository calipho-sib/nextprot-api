package org.nextprot.api.service.impl;

import java.util.List;

import org.nextprot.api.dao.AuthorDao;
import org.nextprot.api.domain.PublicationAuthor;
import org.nextprot.api.service.AuthorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;


@Lazy
@Service
public class AuthorServiceImpl implements AuthorService {

	@Autowired
	private AuthorDao authorDAO;

	@Override
	public List<PublicationAuthor> findAuthorsByPublicationId(Long publicationId) {
		return authorDAO.findAuthorsByPublicationId(publicationId);
	}

	

}
