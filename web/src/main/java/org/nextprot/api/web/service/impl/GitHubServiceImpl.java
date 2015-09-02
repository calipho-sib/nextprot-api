package org.nextprot.api.web.service.impl;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kohsuke.github.GHContent;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHTree;
import org.kohsuke.github.GHTree.GHTreeEntry;
import org.kohsuke.github.GitHub;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.web.domain.NextProtNews;
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
	
	private static final Log LOGGER = LogFactory.getLog(GitHubServiceImpl.class);

	
	private String githubToken = null;

	// will refresh every minute, because anonymous calls are limited to 60
	// calls per hour

	/**
	 * Connects anonymously if token is not defined
	 * @return
	 * @throws IOException
	 */
	private GitHub getGitHubConnection () throws IOException{
		if((githubToken == null) || githubToken.equalsIgnoreCase("undefined")){
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
			GHContent content = repo.getFileContent(folder + "/" + page + ".md", "master");
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
	
	
	@Override
	@Cacheable(value = "github-news")
	public List<NextProtNews> getNews() {
		
		List<NextProtNews> news = new ArrayList<>();
		
		try {
			GitHub github =  getGitHubConnection();
			GHRepository repo = github.getRepository("calipho-sib/nextprot-docs");
			GHTree tree = repo.getTreeRecursive("master", 1);
			for(GHTreeEntry te : tree.getTree()){
				if(te.getPath().startsWith("news")){ //Add only file on news
					if(te.getType().equalsIgnoreCase("blob")){ // file 
						NextProtNews n = parseGitHubNewsFilePath(te.getPath().replaceAll("news/", ""));
						if(n != null){
							news.add(n);
						}
					}
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
			throw new NextProtException("News not available, sorry for the inconvenience");
		}

		Collections.sort(news);
		return news;

	}
	
	@Value("${github.accesstoken}")
	public void setGithubToken(String githubToken) {
		this.githubToken = githubToken;
	}

	static NextProtNews parseGitHubNewsFilePath(String filePath){
		
		NextProtNews result = new NextProtNews();
		
		if(filePath == null || filePath.equals("")){
			return null;
		}
		
		String[] elements = filePath.split("\\|");
		if(elements.length != 3){
			LOGGER.warn("Number of elements is different than 3 != " + elements.length + " " + elements);
			return null;
		}
		
		//Check for elements not null or empty
		for(String elem : elements){
			if(elem == null || elem.isEmpty()){
				LOGGER.warn("Found a null or empty element on " + filePath);
				return null;
			}
		}
		
		try { // Parse the date
		
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd"); 
			Date date = df.parse(elements[0].trim());
			result.setPublicationDate(date);

		} catch (ParseException e) {
			LOGGER.warn("Failed to parse the date for file" + filePath + " " + e.getMessage());
			e.printStackTrace();
			return null;
		}

		//Gets url and title
		result.setUrl(elements[1].trim());
		result.setTitle(elements[2].replace(".md", "").trim());

		return result;
	}

}
 