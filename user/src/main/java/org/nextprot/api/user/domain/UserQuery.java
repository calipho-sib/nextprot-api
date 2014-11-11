package org.nextprot.api.user.domain;

import org.nextprot.api.commons.exception.NPreconditions;

import java.io.Serializable;
import java.util.Set;

public class UserQuery implements Serializable, UserResource {

	private static final long serialVersionUID = 3051410556247218680L;

	private long userQueryId;
	private String title;
	private String description;
	private String sparql;
	private boolean published;
	private String username;
	private Set<String> tags;

	public long getUserQueryId() {
		return userQueryId;
	}

	public void setUserQueryId(long userQueryId) {
		this.userQueryId = userQueryId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getSparql() {
		return sparql;
	}

	public void setSparql(String sparql) {
		this.sparql = sparql;
	}

	public Boolean getPublished() {
		return published;
	}

	public void setPublished(boolean published) {
		this.published = published;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void checkValid() {
		NPreconditions.checkNotNull(sparql, "The sparql should not be null");
		NPreconditions.checkNotNull(title, "The title should not be null");
		NPreconditions.checkTrue(title.length() >= 3,
				"The title should be at least 3 characters long");
	}
	public Set<String> getTags() {
		return tags;
	}

	public void setTags(Set<String> tags) {
		this.tags = tags;
	}

	@Override
	public String getResourceOwner() {
		return this.getUsername();
	}

}
