package org.nextprot.api.security.threescale;

import org.springframework.security.authentication.AbstractAuthenticationToken;

public class ThreeScaleAuthenticationToken extends AbstractAuthenticationToken {

	private static final long serialVersionUID = 2371882820082543721L;
	private final String userKey;

	public ThreeScaleAuthenticationToken(String userKey) {
		super(null);
		this.userKey = userKey;
		setAuthenticated(false);
	}

	public String getUserKey() {
		return userKey;
	}

	public void setAuthenticated(boolean isAuthenticated)
			throws IllegalArgumentException {
		if (isAuthenticated) {
			throw new IllegalArgumentException(
					"Cannot set this token to trusted - use constructor which takes a GrantedAuthority list instead");
		}

		super.setAuthenticated(false);
	}

	@Override
	public Object getCredentials() {
		return null;
	}

	@Override
	public Object getPrincipal() {
		return null;
	}

}
