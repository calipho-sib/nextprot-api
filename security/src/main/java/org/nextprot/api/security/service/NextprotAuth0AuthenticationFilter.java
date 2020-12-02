package org.nextprot.api.security.service;


import java.io.IOException;
import java.util.regex.Pattern;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.web.filter.GenericFilterBean;

/**
 * Filter responsible to intercept the JWT in the HTTP header and attempt an authentication. It delegates the authentication to the authentication manager
 *
 * @author Daniel Teixeira
 */
public class NextprotAuth0AuthenticationFilter extends GenericFilterBean {

    @Autowired
    private AuthenticationManager authenticationManager;

    private AuthenticationEntryPoint entryPoint;

    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {

        final HttpServletRequest request = (HttpServletRequest) req;
        final HttpServletResponse response = (HttpServletResponse) res;

        String jwt = getToken(request);
        if (jwt != null) {
            try {
                Auth0JWT token = new Auth0JWT(jwt);
                Authentication authResult = authenticationManager.authenticate(token);
                SecurityContextHolder.getContext().setAuthentication(authResult);
            } catch (AuthenticationException failed) {
                SecurityContextHolder.clearContext();
                entryPoint.commence(request, response, failed);
                return;
            }
        }
        chain.doFilter(request, response);
    }

    /**
     * Looks at the authorization bearer and extracts the JWT
     */
    private String getToken(HttpServletRequest httpRequest) {
        String token = null;
        final String authorizationHeader = httpRequest.getHeader("authorization");
        if (authorizationHeader == null) {
            // "Unauthorized: No Authorization header was found"
            return null;
        }

        String[] parts = authorizationHeader.split(" ");
        if (parts.length != 2) {
            // "Unauthorized: Format is Authorization: Bearer [token]"
            return null;

        }

        String scheme = parts[0];
        String credentials = parts[1];

        Pattern pattern = Pattern.compile("^Bearer$", Pattern.CASE_INSENSITIVE);
        if (pattern.matcher(scheme).matches()) {
            token = credentials;
        }
        return token;
    }

    public AuthenticationEntryPoint getEntryPoint() {
        return entryPoint;
    }

    public void setEntryPoint(AuthenticationEntryPoint entryPoint) {
        this.entryPoint = entryPoint;
    }

}

