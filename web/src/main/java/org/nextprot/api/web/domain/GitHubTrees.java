package org.nextprot.api.web.domain;

import java.util.List;

public class GitHubTrees {

	private String sha;

	public String getSha() {
		return sha;
	}

	public void setSha(String sha) {
		this.sha = sha;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public List<GitHubTree> getTree() {
		return tree;
	}

	public void setTree(List<GitHubTree> tree) {
		this.tree = tree;
	}

	private String url;
	private List<GitHubTree> tree;

}
