package org.nextprot.api.security.service;

public interface JWTCodec<T> {

	public String generateToken(T object, int expiration); 
	public T decodeToken(String token);

}
