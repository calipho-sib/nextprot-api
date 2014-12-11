package org.nextprot.api.security.service.impl;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nextprot.api.security.service.JWTCodec;
import org.nextprot.api.security.service.exception.NextprotSecurityException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.auth0.jwt.Algorithm;
import com.auth0.jwt.ClaimSet;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.JwtSigner;
import com.auth0.spring.security.auth0.Auth0AuthenticationFilter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class JWTCodecImpl implements JWTCodec<Map<String, String>>, InitializingBean {

	private static final Log Logger = LogFactory.getLog(JWTCodecImpl.class);

	private String clientSecret = null;
	private String clientId = null;

	@Override
	public String encodeJWT(Map<String, String> properties, int expiration) {

		String payload, token;
		try {

			JwtSigner jwtSigner = new JwtSigner();
			payload = new ObjectMapper().writeValueAsString(properties);

			ClaimSet claimSet = new ClaimSet();
			claimSet.setExp(expiration);
			token = jwtSigner.encode(Algorithm.HS256, payload, "payload",
					new String(Base64.decodeBase64(clientSecret)), claimSet);

		} catch (JsonProcessingException e) {
			throw new SecurityException(e);
		} catch (Exception e) {
			throw new SecurityException(e);
		}

		return token;
	}

	@Override
	public Map<String, String> decodeJWT(String token) {

		JWTVerifier jwtVerifier = new JWTVerifier(clientSecret, clientId);

		Map<String, Object> verify;
		try {

			verify = jwtVerifier.verify(token);
			String payload = (String) verify.get("$");
			@SuppressWarnings("unchecked")
			Map<String, String> map = new ObjectMapper().readValue(payload,
					Map.class);
			return map;

		} catch (InvalidKeyException e) {
			throw new NextprotSecurityException(e);
		} catch (NoSuchAlgorithmException e) {
			throw new NextprotSecurityException(e);
		} catch (IllegalStateException e) {
			throw new NextprotSecurityException(e);
		} catch (SignatureException e) {
			throw new NextprotSecurityException(e);
		} catch (IOException e) {
			throw new NextprotSecurityException(e);
		}
	}

	@Override
	public void afterPropertiesSet() {
		Assert.notNull(clientSecret,
				"The client secret is not set for " + this.getClass());
		Assert.notNull(clientId,
				"The client id is not set for " + this.getClass());
	}

	public String getClientSecret() {
		return clientSecret;
	}

    @Value("${auth0.clientId}")
	public void setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
	}

	public String getClientId() {
		return clientId;
	}

    @Value("${auth0.clientSecret}")
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	@Override
	public Auth0AuthenticationFilter A() {
		// TODO Auto-generated method stub
		return null;
	}

}
