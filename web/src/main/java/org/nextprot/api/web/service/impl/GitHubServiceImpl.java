package org.nextprot.api.web.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.kohsuke.github.GHContent;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.PagedIterable;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.web.domain.GitHubTree;
import org.nextprot.api.web.domain.GitHubTrees;
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
			GitHub github = GitHub.connectAnonymously();//TODO move this with a token
			GHRepository repo = github.getRepository("calipho-sib/nextprot-docs");
			GHContent content = repo.getFileContent("pages/" + page + ".md");
			return content.getContent();

		} catch (IOException e) {
			e.printStackTrace();
			throw new NextProtException("Documentation not available, sorry for the inconvenience");
		}

	}

	@Override
	@Cacheable(value = "github-pages")
	public GitHubTrees getDirectoryContent() {

		GitHubTrees trees = new GitHubTrees();
		trees.setSha("cf48b122d3aa50fca6049c0b804ae1cea0223fd2");
		trees.setUrl("https://api.github.com/repos/calipho-sib/nextprot-docs/git/trees/cf48b122d3aa50fca6049c0b804ae1cea0223fd2");

		List<GitHubTree> tree = new ArrayList<GitHubTree>();

		try {

			GitHub github = GitHub.connectAnonymously();
			GHRepository repo = github.getRepository("calipho-sib/nextprot-docs");
			List<GHContent> contents = repo.getDirectoryContent("");

			for (GHContent c : contents) {
				tree.add(buildTree(c));
				if(c.isDirectory()){
					PagedIterable<GHContent> cs = c.listDirectoryContent();
					for(GHContent c1 : cs){
						tree.add(buildTree(c1));
					}
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
			throw new NextProtException("Documentation not available, sorry for the inconvenience");
		}

		trees.setTree(tree);
		return trees;

	}
	
	private static GitHubTree buildTree(GHContent c){
		
		GitHubTree t = new GitHubTree();
		t.setPath(c.getPath());
		t.setMode("100644");
		t.setType(c.getType());
		t.setSha(c.getSha());
		t.setSize(c.getSize());
		t.setUrl(c.getUrl());
		
		return t;


	}

}
