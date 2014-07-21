package org.nextprot.api.domain;

import java.io.Serializable;

import org.nextprot.utils.NPreconditions;

public class UserQuery implements Serializable {

	private static final long serialVersionUID = 3051410556247218680L;

	private long userQueryId;
	private String title;
	private String description;
	private String sparql;
	private boolean published;
	private String submitted;
	private String username;

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

	public String getSubmitted() {
		return submitted;
	}

	public void setSubmitted(String submitted) {
		this.submitted = submitted;
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
		NPreconditions.checkTrue(title.length() >= 3, "The title should be at least 3 characters long");
	}

}
