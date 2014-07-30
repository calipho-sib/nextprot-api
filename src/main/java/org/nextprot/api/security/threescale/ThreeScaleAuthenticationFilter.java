package org.nextprot.api.security.threescale;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

public class ThreeScaleAuthenticationFilter extends GenericFilterBean {

	private AuthenticationManager authenticationManager;

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

		System.out.println("CONTEXT" + SecurityContextHolder.getContext() + " CLASS AUTH MANAGER" + authenticationManager.getClass().getCanonicalName());

		String userKey = ((HttpServletRequest) request).getParameter("user_key");
		if (userKey != null) {
		
			ThreeScaleAuthenticationToken token = new ThreeScaleAuthenticationToken(userKey);
			Authentication authResult = authenticationManager.authenticate(token);
			
			SecurityContextHolder.getContext().setAuthentication(authResult);
			
			chain.doFilter(request, response);
		
		
		} else {
			throw new AccessDeniedException("You must provide an apikey");
		}

		
		
	}

	public AuthenticationManager getAuthenticationManager() {
		return authenticationManager;
	}

	public void setAuthenticationManager(AuthenticationManager authenticationManager) {
		this.authenticationManager = authenticationManager;
	}
	
	
}