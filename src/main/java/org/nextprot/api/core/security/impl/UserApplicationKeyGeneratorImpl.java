package org.nextprot.api.core.security.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nextprot.api.commons.exception.NPreconditions;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.core.domain.user.UserApplication;
import org.nextprot.api.core.security.UserApplicationKeyGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.auth0.jwt.Algorithm;
import com.auth0.jwt.ClaimSet;
import com.auth0.jwt.JwtSigner;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Lazy
@Service
public class UserApplicationKeyGeneratorImpl implements UserApplicationKeyGenerator {
	
	private static final Log Logger = LogFactory.getLog(UserApplicationKeyGeneratorImpl.class);
	
	private String secret = null;

	@Override
	public String generateToken(UserApplication application) {

		NPreconditions.checkNotNull(secret, "The secret is not set for the application key generator!");

		Map<String, String> map = new HashMap<String, String>();
		map.put("id", application.getId());
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


	public String getSecret() {
		return secret;
	}
	
	@Value("${auth0.clientSecret}") 
	public void setSecret(String secret) {
		this.secret = secret;
	}

}
