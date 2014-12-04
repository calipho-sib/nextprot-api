package org.nextprot.api.security.service;

import com.auth0.spring.security.auth0.Auth0AuthenticationFilter;

public interface JWTCodec<T> {

	public String encodeJWT(T object, int expiration);
	public T decodeJWT(String token);
	Auth0AuthenticationFilter A ();
}
