package org.nextprot.api.core.security;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.UnsupportedEncodingException;

import org.apache.commons.codec.binary.Base64;
import org.junit.Test;
import org.nextprot.api.dbunit.DBUnitBaseTest;
import org.nextprot.api.user.domain.UserApplication;
import org.nextprot.api.user.security.UserApplicationKeyGenerator;
import org.springframework.beans.factory.annotation.Autowired;

public class UserApplicationKeyGeneratorTest extends DBUnitBaseTest {

	@Autowired
	private UserApplicationKeyGenerator keyGenerator;

	@Test
	public void shouldGenerateAKeyForTheUserApplication() {

		UserApplication app = new UserApplication();
		app.setName("My test application");

		String token = keyGenerator.generateToken(app);

		// Should get a JWT (header, payload and signature)
		String[] jwt = token.split("\\.");
		assertEquals(jwt.length, 3);

		try {
			String decodedPayload = new String(Base64.decodeBase64(jwt[1]), "UTF-8");
			// The payload should contain the id and name
			assertTrue(decodedPayload.contains(app.getName()));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			fail();
		}

	}

	@Test
	public void shouldEncodeAndDecodeUserApplication() {

		UserApplication app = new UserApplication();
		app.setName("My test application");

		String token = keyGenerator.generateToken(app);

		UserApplication appDecoded = keyGenerator.decodeToken(token);

		System.out.println("id" + appDecoded.getId());
		System.out.println("name" + appDecoded.getName());

		assertEquals(app.getId(), appDecoded.getId());
		assertEquals(app.getName(), appDecoded.getName());

		assertFalse(app.equals(appDecoded));

	}

}
