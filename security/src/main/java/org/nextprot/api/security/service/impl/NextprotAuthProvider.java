package org.nextprot.api.security.service.impl;

import com.auth0.jwt.JWTVerifier;
import com.auth0.spring.security.auth0.Auth0JWTToken;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.Map;

public class NextprotAuthProvider implements AuthenticationProvider, InitializingBean {

	private JWTVerifier jwtVerifier;
	private String clientSecret;
	private String clientId;
	private final Log logger = LogFactory.getLog(NextprotAuthProvider.class);

	@Autowired
	private UserDetailsService userDetailsService;

	public Authentication authenticate(Authentication authentication) throws AuthenticationException {

		String token = ((Auth0JWTToken) authentication).getJwt();

		this.logger.debug("Trying to authenticate with token: " + token);
		try {

			Auth0JWTToken tokenAuth = (Auth0JWTToken) authentication;
			Map<String, Object> map = this.jwtVerifier.verify(token);
			this.logger.debug("Decoded JWT token" + map);

			UserDetails userDetails = null;
			if (map.containsKey("email")) {
				String username = (String) map.get("email");
				if (username != null) {
					userDetails = userDetailsService.loadUserByUsername(username);
					authentication.setAuthenticated(true);
					
					return createSuccessAuthentication(userDetails, map);
				
				}else return null;
				
			} /*//TODO add the application here or as another provider else if (map.containsKey("app_id")) {
				long appId = (Long) map.get("app_id");
				UserApplication userApp = userApplicationService.getUserApplication(appId);
				if (userApp.hasUserDataAccess()) {

					userDetails = userDetailsService.loadUserByUsername(userApp.getOwner());
					if (userDetails == null) {
						userService.createUser(buildUserFromAuth0(map));
					}
					userDetails = userDetailsService.loadUserByUsername(userApp.getOwner());
				}
			}*/ else throw new SecurityException("client id not found");

			
			

		} catch (InvalidKeyException e) {
			e.printStackTrace();
			this.logger.error("InvalidKeyException thrown while decoding JWT token " + e.getLocalizedMessage());
			throw new SecurityException(e);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			this.logger.error("NoSuchAlgorithmException thrown while decoding JWT token " + e.getLocalizedMessage());
			throw new SecurityException(e);
		} catch (IllegalStateException e) {
			e.printStackTrace();
			this.logger.error("IllegalStateException thrown while decoding JWT token " + e.getLocalizedMessage());
			throw new SecurityException(e);
		} catch (SignatureException e) {
			e.printStackTrace();
			this.logger.error("SignatureException thrown while decoding JWT token " + e.getLocalizedMessage());
			throw new SecurityException(e);
		} catch (IOException e) {
			e.printStackTrace();
			this.logger.error("IOException thrown while decoding JWT token " + e.getLocalizedMessage());
			throw new SecurityException(e);
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
	

    /**
     * Creates a successful {@link Authentication} object
     *
     * @return the successful authentication token
     */
    private final Authentication createSuccessAuthentication(UserDetails userDetails, Map<String, Object> map) {
        
    	NextprotUserToken usrToken = new NextprotUserToken();
    	usrToken.setAuthenticated(true);
    	usrToken.setPrincipal(userDetails);
    	usrToken.setDetails(map);

        return usrToken;
    }

	
}