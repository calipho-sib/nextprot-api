package org.nextprot.api.security.service.impl;

import java.util.Map;

//import com.auth0.jwt.JWTVerifyException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nextprot.api.security.service.Auth0JWT;
import org.nextprot.api.security.service.JWTCodec;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

//import com.auth0.Auth0User;
import com.auth0.jwt.JWTVerifier;
import org.springframework.stereotype.Component;
//import com.auth0.spring.security.auth0.Auth0JWTToken;
//import com.auth0.spring.security.auth0.Auth0TokenException;

@Component
public class NextprotAuthProvider implements AuthenticationProvider, InitializingBean {

    private JWTVerifier jwtVerifier;
    private String clientSecret;
    private String clientId;
    private final Log logger = LogFactory.getLog(NextprotAuthProvider.class);

    @Autowired
    private UserDetailsService userDetailsService;

    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        String token = ((Auth0JWT) authentication).getJwt();
        Map<String, Object> map = null;

        try {
            //Auth0 Universal login flow is implemented now
            //Only an access token, signed with RSA256 should be passed to the API with Authorization header
            if (token.split("\\.").length == 3) {
                JWTCodec jwtCodec = new JWTCodecImpl();
                map = (Map<String, Object>) jwtCodec.decodeJWT(token);
            }
            this.logger.debug("Decoded JWT token" + map);

        } catch(Exception e) {
            //e.printStackTrace();
            NextprotUserToken userToken = new NextprotUserToken();
            userToken.setAuthenticated(false);
            return userToken;
        }

        UserDetails userDetails;
        String username = (String) map.get(JWTCodecImpl.EMAIL);
        if (username != null) {
            userDetails = userDetailsService.loadUserByUsername(username);
            authentication.setAuthenticated(true);

            return createSuccessAuthentication(userDetails, map);
        } else {
            throw new SecurityException("client id not found");
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return Auth0JWT.class.isAssignableFrom(authentication);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if ((this.clientSecret == null) || (this.clientId == null)) {
            throw new RuntimeException("client secret and client id are not set for Auth0AuthenticationProvider");
        }

        //this.jwtVerifier = new JWTVerifier(this.clientSecret, this.clientId);
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
        usrToken.getAuthorities().addAll(userDetails.getAuthorities());

        return usrToken;
    }


}