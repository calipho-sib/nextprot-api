package org.nextprot.api.security.service.impl;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nextprot.api.user.domain.UserApplication;
import org.nextprot.api.user.service.UserApplicationService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import com.auth0.jwt.JWTVerifier;
import com.auth0.spring.security.auth0.Auth0JWTToken;

public class NextprotAuthProvider implements AuthenticationProvider, InitializingBean {

	private JWTVerifier jwtVerifier;
	private String clientSecret;
	private String clientId;
	private final Log logger = LogFactory.getLog(NextprotAuthProvider.class);
	private static final SecurityException AUTH_ERROR = new SecurityException("Authentication error occured");

	@Autowired
	private UserDetailsService userDetailsService;
	private UserApplicationService userApplicationService;

	public Authentication authenticate(Authentication authentication) throws AuthenticationException {

		String token = ((Auth0JWTToken) authentication).getJwt();

		this.logger.info("Trying to authenticate with token: " + token);
		try {
			
			Auth0JWTToken tokenAuth = (Auth0JWTToken) authentication;
			Map<String, Object> map = this.jwtVerifier.verify(token);
			this.logger.debug("Decoded JWT token" + map);
			
			UserDetails userDetails = null;
			if(map.containsKey("username")){
				String username = (String) map.get("username");
				if(username != null){
					userDetails = userDetailsService.loadUserByUsername(username);
				}else {
					long appId = (Long) map.get("application_id");
					UserApplication userApp = userApplicationService.getUserApplication(appId);
					if(userApp.hasUserDataAccess()){ 
						userDetails = userDetailsService.loadUserByUsername(userApp.getOwner());
					}
				}
			}
			
			authentication.setAuthenticated(true);
			tokenAuth.setPrincipal(userDetails);
			tokenAuth.setDetails(decoded);
			return authentication;
		} catch (InvalidKeyException e) {
			this.logger.debug("InvalidKeyException thrown while decoding JWT token " + e.getLocalizedMessage());
			throw AUTH_ERROR;
		} catch (NoSuchAlgorithmException e) {
			this.logger.debug("NoSuchAlgorithmException thrown while decoding JWT token " + e.getLocalizedMessage());
			throw AUTH_ERROR;
		} catch (IllegalStateException e) {
			this.logger.debug("IllegalStateException thrown while decoding JWT token " + e.getLocalizedMessage());
			throw AUTH_ERROR;
		} catch (SignatureException e) {
			this.logger.debug("SignatureException thrown while decoding JWT token " + e.getLocalizedMessage());
			throw AUTH_ERROR;
		} catch (IOException e) {
			this.logger.debug("IOException thrown while decoding JWT token " + e.getLocalizedMessage());
			throw AUTH_ERROR;
		}
	}

	public boolean supports(Class<?> authentication) {
		return Auth0JWTToken.class.isAssignableFrom(authentication);
	}

	public void afterPropertiesSet() throws Exception {
		if ((this.clientSecret == null) || (this.clientId == null)) {
			throw new RuntimeException("client secret and client id are not set for Auth0AuthenticationProvider");
		}

		this.jwtVerifier = new JWTVerifier(this.clientSecret, this.clientId);
	}

	public String getClientSecret() {
		return this.clientSecret;
	}

	@Value("${auth0.clientSecret}")
	public void setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
	}

	public String getClientId() {
		return this.clientId;
	}

	@Value("${auth0.clientId}")
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}
}