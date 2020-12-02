package org.nextprot.api.security.service.impl;

import org.nextprot.api.security.service.NextprotAuth0Endpoint;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
//import us.monoid.json.JSONException;
//import us.monoid.web.JSONResource;
//import us.monoid.web.Resty;

import java.io.IOException;

@Lazy
@Service
public class NextprotAuth0EndpointImpl implements InitializingBean, NextprotAuth0Endpoint {

	private String clientSecret = null;
	private String clientId = null;
	private String clientDomain = null;

	/*@Cacheable(value = "user-auth", sync = true)
	public Auth0User fetchUser(String accessToken) throws IOException, JSONException {
		Resty resty = new Resty();

		String userInfoUri = getUserInfoUri(accessToken);
		JSONResource json = resty.json(userInfoUri);
		return new Auth0User(json.toObject());

	}*/

	private String getUserInfoUri(String accessToken) {
		return getUri("/userinfo?access_token=" + accessToken);
	}

	private String getUri(String path) {
		return String.format("https://%s%s", clientDomain, path);
	}

	@Override
	public void afterPropertiesSet() {
		Assert.notNull(clientSecret, "The client secret is not set for " + this.getClass());
		Assert.notNull(clientId, "The client id is not set for " + this.getClass());
	}

	public String getClientSecret() {
		return clientSecret;
	}

	@Value("${auth0.clientDomain}")
	public void setClientDomain(String clientDomain) {
		this.clientDomain = clientDomain;
	}
	
	@Value("${auth0.clientSecret}")
	public void setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
	}

	public String getClientId() {
		return clientId;
	}

	@Value("${auth0.clientId}")
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}
}
