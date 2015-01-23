package org.nextprot.api.security.service.impl;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class NextprotUserToken extends AbstractAuthenticationToken {

	private static final long serialVersionUID = 2371882820082543721L;
	private UserDetails principal;

	public NextprotUserToken() {
		super(null);
		setAuthenticated(false);
	}

	public Object getCredentials() {
		return null;
	}

	public UserDetails getPrincipal() {
		return this.principal;
	}

	public void setPrincipal(UserDetails userDetails) {
		this.principal = userDetails;
	}

	public Collection<GrantedAuthority> getAuthorities() {
		return new ArrayList<GrantedAuthority>(this.principal.getAuthorities());
	}
}