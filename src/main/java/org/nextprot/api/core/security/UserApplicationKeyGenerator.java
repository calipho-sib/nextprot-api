package org.nextprot.api.core.security;

import org.nextprot.api.core.domain.user.UserApplication;

public interface UserApplicationKeyGenerator {

	public String generateToken(UserApplication application);
	
}
