package org.nextprot.api.commons.utils;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.UUID;

public final class IdentifierGenerator {

	private static SecureRandom random = new SecureRandom();
	
	public static String generateUUID() {
		return UUID.randomUUID().toString().replace("-", "");
	}

	public static String generateClientId() {
		return UUID.randomUUID().toString();
	}

	public static String generateClientSecret() {
		return new BigInteger(130, random).toString(32);
	}
}
