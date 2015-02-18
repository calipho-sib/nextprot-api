package org.nextprot.api.web.service;

import org.kohsuke.github.GHTree;

public interface GitHubService {

	public String getPage(String page);

	public GHTree getTree();

}
