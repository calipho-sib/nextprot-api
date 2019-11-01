package org.nextprot.api.web.service;

import java.util.List;

import org.kohsuke.github.GHTree;
import org.nextprot.api.web.domain.NextProtNews;

public interface GitHubService {

	public String getPage(String folder, String page);

	public GHTree getTree();

	List<NextProtNews> getNews();

	List<String> getReleaseStatList();
}
