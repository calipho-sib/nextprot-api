package org.nextprot.api.security.service.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.Payload;
import org.apache.lucene.analysis.tokenattributes.PayloadAttributeImpl;
import org.codehaus.jackson.map.ObjectMapper;
import org.nextprot.api.security.service.JWTCodec;
import org.nextprot.api.security.service.exception.NextprotSecurityException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.interfaces.RSAPublicKey;
import java.util.HashMap;
import java.util.Map;

@Service
public class JWTCodecImpl implements JWTCodec<Map<String, Object>>, InitializingBean {

	@Value(value = "classpath:keys/pubkey")
	private Resource publicKeyFile;

	private String clientSecret = null;
	private String clientId = null;

	@Override
	public String encodeJWT(Map<String, Object> properties, int expiration) {

		String payload, token;
		try {

			/*JWTSigner jwtSigner = new JWTSigner();
			payload = new ObjectMapper().writeValueAsString(properties);

			ClaimSet claimSet = new ClaimSet();
			claimSet.setExp(expiration);
			token = jwtSigner.(Algorithm.RS256, payload, "payload",
					new String(Base64.decodeBase64(clientSecret)), claimSet);*/


		} catch (Exception e) {
			throw new SecurityException(e);
		}

		return null ;
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
					.build(); //Reusable verifier instance
			DecodedJWT jwt = verifier.verify(token);


			Map<String, Object> map = new HashMap<>();
			map.put("email", jwt.getClaim("https://www.nextprot.org/userinfo/email").asString());
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
