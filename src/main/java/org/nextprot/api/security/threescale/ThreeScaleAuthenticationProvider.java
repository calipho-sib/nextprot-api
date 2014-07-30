package org.nextprot.api.security.threescale;

import org.nextprot.api.commons.exception.NPreconditions;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import threescale.v3.api.AuthorizeResponse;
import threescale.v3.api.ParameterMap;
import threescale.v3.api.ServerError;
import threescale.v3.api.ServiceApi;
import threescale.v3.api.impl.ServiceApiDriver;

public class ThreeScaleAuthenticationProvider implements AuthenticationProvider, InitializingBean {

	private ServiceApi serviceApi = null;

	private String serviceId;
	private String accountKey;
	
	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		
		NPreconditions.checkTrue(ThreeScaleAuthenticationToken.class.isAssignableFrom(authentication.getClass()), authentication.getClass() + " not assignable for " + ThreeScaleAuthenticationProvider.class.getName());
		
		String userKey = ((ThreeScaleAuthenticationToken) authentication).getUserKey();

		ParameterMap params = new ParameterMap(); // the parameters of your call
		params.add("service_id", serviceId); // Add the service id of your
		params.add("user_key", userKey); 
		
		AuthorizeResponse response = null;
		try {
		
			response = serviceApi.authrep(params);

			System.out.println("AuthRep on User Key Success: "+ response.success());
			if (response.success() == true) {
				// your api access got authorized and the traffic added to
				// 3scale backend
				System.out.println("Plan: " + response.getPlan());
				return authentication;

			} else {
				// your api access did not authorized, check why
				System.out.println("Error: " + response.getErrorCode());
				System.out.println("Reason: " + response.getReason());
				
				throw new BadCredentialsException(response.getErrorCode() + " " + response.getReason());
				
			}

		} catch (ServerError serverError) {
			new AuthenticationServiceException(serverError.getMessage());
		}

		return null;
	}
	

	@Override
	public boolean supports(Class<?> authentication ) {
		return ThreeScaleAuthenticationToken.class.isAssignableFrom(authentication);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		serviceApi = new ServiceApiDriver(accountKey);
	}
	
	
	public String getServiceId() {
		return serviceId;
	}


	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}


	public String getAccountKey() {
		return accountKey;
	}


	public void setAccountKey(String accountKey) {
		this.accountKey = accountKey;
	}

}
