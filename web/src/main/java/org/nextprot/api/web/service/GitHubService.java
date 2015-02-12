package org.nextprot.api.web.service;

import org.nextprot.api.web.domain.GitHubTrees;

public interface GitHubService {

	public String getPage(String page);

	public GitHubTrees getDirectoryContent();

}
