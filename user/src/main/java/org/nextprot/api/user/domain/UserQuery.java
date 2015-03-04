package org.nextprot.api.user.domain;

import java.io.Serializable;
import java.util.Set;

import org.nextprot.api.commons.exception.NPreconditions;
import org.nextprot.api.commons.resource.UserResource;

public class UserQuery implements Serializable, UserResource {

	private static final long serialVersionUID = 3051410556247218680L;

	private long userQueryId;
	private String title;
	private String description;
	private String sparql;
	private boolean published;
	private String owner;
	private long ownerId;

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

	@Override
	public void setOwnerName(String name) { this.owner = name; }

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public long getOwnerId() { return ownerId; }

	public void setOwnerId(long ownerId) { this.ownerId = ownerId; }

	public void checkValidForUpdate() {
		NPreconditions.checkNotNull(sparql, "The sparql should not be null");
		NPreconditions.checkNotNull(title, "The title should not be null");
		NPreconditions.checkTrue(title.length() >= 3,"The title should be at least 3 characters long");
		NPreconditions.checkTrue(userQueryId >= 100000,"The user query id is not valid"); // below this number it is reserved for the nextprot query
	}
	public Set<String> getTags() {
		return tags;
	}

	public void setTags(Set<String> tags) {
		this.tags = tags;
	}

	@Override
	public String getOwnerName() {
		return this.getOwner();
	}
}
