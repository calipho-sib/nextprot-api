package org.nextprot.api.web.service.impl;

import java.io.IOException;

import org.kohsuke.github.GHContent;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHTree;
import org.kohsuke.github.GitHub;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.web.service.GitHubService;
import org.springframework.beans.factory.annotation.Value;
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
	
	
	private String githubToken = null;

	// will refresh every minute, because anonymous calls are limited to 60
	// calls per hour

	/**
	 * Connects anonymously if token is not defined
	 * @return
	 * @throws IOException
	 */
	private GitHub getGitHubConnection () throws IOException{
		if(githubToken != null || githubToken.equalsIgnoreCase("undefined")){
			return GitHub.connectAnonymously();
		}else return GitHub.connectUsingOAuth(githubToken);
	}
	
	@Override
	@Cacheable(value = "github-pages")
	public String getPage(String folder, String page) {
		// Not sure if this keeps a connection (if not this can be done on the
		// postconstruct)
		try {
			GitHub github = getGitHubConnection();
			GHRepository repo = github.getRepository("calipho-sib/nextprot-docs");
			GHContent content = repo.getFileContent(folder + "/" + page + ".md");
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
			GitHub github =  getGitHubConnection();
			GHRepository repo = github.getRepository("calipho-sib/nextprot-docs");
			return repo.getTreeRecursive("master", 1);

		} catch (IOException e) {
			e.printStackTrace();
			throw new NextProtException("Documentation not available, sorry for the inconvenience");
		}

	}
	
	
	@Value("${github.accesstoken}")
	public void setGithubToken(String githubToken) {
		this.githubToken = githubToken;
	}


}
 