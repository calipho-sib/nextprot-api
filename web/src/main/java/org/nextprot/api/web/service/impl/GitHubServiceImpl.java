package org.nextprot.api.web.service.impl;

import java.io.IOException;

import org.kohsuke.github.GHContent;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHTree;
import org.kohsuke.github.GitHub;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.web.service.GitHubService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * 
 * Service that communicates with github to retrieve the pages
 * 
 * @author dteixeira
 *
 */
@Service
public class GitHubServiceImpl implements GitHubService {

	// will refresh every minute, because anonymous calls are limited to 60
	// calls per hour

	@Override
	@Cacheable(value = "github-pages")
	public String getPage(String page) {
		// Not sure if this keeps a connection (if not this can be done on the
		// postconstruct)
		try {
			GitHub github = GitHub.connectAnonymously();// TODO move this with a
														// token
			GHRepository repo = github.getRepository("calipho-sib/nextprot-docs");
			GHContent content = repo.getFileContent("pages/" + page + ".md");
			return content.getContent();

		} catch (IOException e) {
			e.printStackTrace();
			throw new NextProtException("Documentation not available, sorry for the inconvenience");
		}

	}

	@Override
	@Cacheable(value = "github-tree")
	public GHTree getTree() {
		try {
			GitHub github = GitHub.connectAnonymously();// TODO move this with a token
			GHRepository repo = github.getRepository("calipho-sib/nextprot-docs");
			return repo.getTreeRecursive("master", 1);

		} catch (IOException e) {
			e.printStackTrace();
			throw new NextProtException("Documentation not available, sorry for the inconvenience");
		}

	}

}
 