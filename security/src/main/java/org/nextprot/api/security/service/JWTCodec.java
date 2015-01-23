package org.nextprot.api.security.service;

public interface JWTCodec<T> {

	String encodeJWT(T object, int expiration);
	T decodeJWT(String token);

}
