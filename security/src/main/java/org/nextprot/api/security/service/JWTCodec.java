package org.nextprot.api.security.service;

public interface JWTCodec<T> {

	public String encodeJWT(T object, int expiration);
	public T decodeJWT(String token);

}
