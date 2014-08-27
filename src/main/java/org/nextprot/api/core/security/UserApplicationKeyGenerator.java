package org.nextprot.api.core.security;

import org.nextprot.api.core.domain.user.UserApplication;

public interface UserApplicationKeyGenerator {

	/**
	 * Generates a token for the user application with the expiry of 1 year
	 * 
	 * The user application may not have all fields set, depending on how many fields are used to generate the token
	 * 
	 * @param token 
	 * @return The user application (just with the set fields)
	 */
	public String generateToken(UserApplication application);
	
	/**
	 * Return the user application based on the given token.
	 * 
	 * The user application may not have all fields set, depending on how many fields are used to generate the token
	 * 
	 * @param token 
	 * @return The user application (just with the set fields)
	 */
	public UserApplication decodeToken(String token);
	
}
