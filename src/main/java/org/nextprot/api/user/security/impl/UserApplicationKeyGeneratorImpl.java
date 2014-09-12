package org.nextprot.api.user.security.impl;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nextprot.api.commons.exception.NPreconditions;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.user.domain.UserApplication;
import org.nextprot.api.user.security.UserApplicationKeyGenerator;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.auth0.jwt.Algorithm;
import com.auth0.jwt.ClaimSet;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.JwtSigner;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Lazy
@Service
public class UserApplicationKeyGeneratorImpl implements UserApplicationKeyGenerator, InitializingBean {
	
	private static final Log Logger = LogFactory.getLog(UserApplicationKeyGeneratorImpl.class);
	
	private String secret = null;

	@Override
	public String generateToken(UserApplication application) {

		Map<String, String> map = new HashMap<String, String>();
		//Don't put all fields to reduce the size of the token
		map.put("name", application.getName());

		String payload, token;
		try {
		
			JwtSigner jwtSigner = new JwtSigner();
			payload = new  ObjectMapper().writeValueAsString(map);

		    ClaimSet claimSet = new ClaimSet();
		    claimSet.setExp(24 * 60 * 60 * 365); // expire in 1 year
		    
			token = jwtSigner.encode(Algorithm.HS256, payload, "payload", secret, claimSet);
		
		} catch (JsonProcessingException e) {
			throw new NextProtException(e);
		} catch (Exception e) {
			throw new NextProtException(e);
		}
		
		return token;
		
	}

	@Override
	public UserApplication decodeToken(String token) {

		JWTVerifier jwtVerifier = new JWTVerifier(Base64.encodeBase64String(secret.getBytes()));
		Map<String, Object> verify;
		try {

			verify = jwtVerifier.verify(token);
			String payload = (String) verify.get("$");
			@SuppressWarnings("unchecked")
			Map<String, String> map = new ObjectMapper().readValue(payload, Map.class);

			//Don't put all fields to reduce the size of the token
			UserApplication app = new UserApplication();
			app.setName(map.get("name"));

			return app;

		} catch (InvalidKeyException e) {
			throw new NextProtException(e);
		} catch (NoSuchAlgorithmException e) {
			throw new NextProtException(e);
		} catch (IllegalStateException e) {
			throw new NextProtException(e);
		} catch (SignatureException e) {
			throw new NextProtException(e);
		} catch (IOException e) {
			throw new NextProtException(e);
		}
		
	}


	public String getSecret() {
		return secret;
	}
	
	@Value("${auth0.clientSecret}") 
	public void setSecret(String secret) {
		this.secret = secret;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		NPreconditions.checkNotNull(secret, "The secret is not set for the application key generator!");
	}

}
