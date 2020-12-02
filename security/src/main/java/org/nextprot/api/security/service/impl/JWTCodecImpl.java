package org.nextprot.api.security.service.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.nextprot.api.security.service.JWTCodec;
import org.nextprot.api.security.service.exception.NextprotSecurityException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.interfaces.RSAPublicKey;
import java.util.HashMap;
import java.util.Map;

@Service
public class JWTCodecImpl implements JWTCodec<Map<String, Object>>, InitializingBean {

	private String clientSecret = null;
	private String clientId = null;

	// EMAIL claim from the auth0 token payload
	public static final String EMAIL = "email";

	@Override
	public String encodeJWT(Map<String, Object> properties, int expiration) {
		return null;
	}

	@Override
	public Map<String, Object> decodeJWT(String token) {

		try {
			File publicKeyFile = new File(this.getClass().getClassLoader().getResource("keys/pubkey").toURI());
			RSAPublicKey publicKey = (RSAPublicKey) PemUtils.readPublicKeyFromFile(publicKeyFile.toString(), "RSA");
			Algorithm algorithm = Algorithm.RSA256(publicKey);
			JWTVerifier verifier = JWT.require(algorithm)
					.withIssuer("https://nextprot.auth0.com/")
					.withAudience("https://nextprot.auth0.com/api/v2/")
					.withAudience("https://nextprot.auth0.com/userinfo")
					.build();
			DecodedJWT jwt = verifier.verify(token);
			Map<String, Object> map = new HashMap<>();
			map.put(EMAIL, jwt.getClaim("https://www.nextprot.org/userinfo/email").asString());
			return map;
		} catch (IOException e) {
			throw new NextprotSecurityException(e);
		} catch (URISyntaxException e) {
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
