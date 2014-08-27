package org.nextprot.api.core.security;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;
import org.nextprot.api.core.domain.user.UserApplication;
import org.nextprot.api.dbunit.DBUnitBaseTest;
import org.springframework.beans.factory.annotation.Autowired;

public class UserApplicationKeyGeneratorTest extends DBUnitBaseTest {

	@Autowired
	private UserApplicationKeyGenerator keyGenerator;

	@Test
	public void shouldGenerateAKeyForTheUserApplication() {

		UserApplication app = new UserApplication();
		app.setId("app-1");
		app.setName("My test application");

		String token = keyGenerator.generateToken(app);
		
		System.out.println(token);

		// Should get a JWT (header, payload and signature)
		assertEquals(token.split("\\.").length, 3);

	}


	@Test
	public void shouldEncodeAndDecodeUserApplication() {

		UserApplication app = new UserApplication();
		app.setId("app-1");
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
