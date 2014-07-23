package org.nextprot.api.core.domain;

import java.io.Serializable;

import org.nextprot.api.commons.exception.NPreconditions;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

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
		NPreconditions.checkTrue(title.length() >= 3,
				"The title should be at least 3 characters long");
	}

	public static boolean isAuthorized(UserQuery q) {

		String securityUserName = "";

		SecurityContext sc = SecurityContextHolder.getContext();
		if (sc == null)
			return false;

		Authentication a = SecurityContextHolder.getContext()
				.getAuthentication();
		if (a == null)
			return false;

		if (a.getPrincipal() instanceof UserDetails) {
			UserDetails currentUserDetails = (UserDetails) a.getPrincipal();
			securityUserName = currentUserDetails.getUsername();
		} else {
			securityUserName = a.getPrincipal().toString();
		}

		return (q.getUsername().equals(securityUserName));

	}

}
