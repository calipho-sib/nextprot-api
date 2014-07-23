package org.nextprot.api.commons.utils;

import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.regex.Pattern;

public class StringUtils {
	private static final Pattern NONLATIN = Pattern.compile("[^\\w-]");
	private static final Pattern WHITESPACE = Pattern.compile("[\\s]");


	
	/**
	 * Replaces the white spaces and the "-" and transform the String in camel
	 * case
	 * 
	 * @param inputString
	 *            The string to be transformed
	 * @param avoidFirst
	 *            Define true if you want to discard the first word
	 * @return The transformed string in camel case
	 */
	public static String toCamelCase(final String inputString,
			boolean avoidFirst) {
		if (inputString == null)
			return null;

		final StringBuilder ret = new StringBuilder(inputString.length());

		for (final String word : inputString.replaceAll("_", " ").split("[ -]")) {
			if (avoidFirst) {
				ret.append(word.toLowerCase());
				avoidFirst = false;
				continue;
			}
			if (!word.isEmpty() && !avoidFirst) {
				ret.append(word.substring(0, 1).toUpperCase());
				ret.append(word.substring(1).toLowerCase());
			}
		}

		return ret.toString();
	}

	static public String slug(String unicode) {
		String nowhitespace = WHITESPACE.matcher(unicode).replaceAll("_");
		String normalized = Normalizer.normalize(nowhitespace, Form.NFD)
				.replaceAll("[:;.,/(){}\\\\]", "_");
		String slug = NONLATIN.matcher(normalized).replaceAll("");
		return slug.toString();
	}

	static public String clean(String input) {
		return input.replaceAll("[\\\\\"\n]", "");
	}

	static public String replaceDoubleQuotes(Object input) {
		return input.toString().replace("\\\"", "");
	}

	/**
	 * Convert a Unicode string, first to UTF-8 and then to an RFC 2396
	 * compliant URI with optional fragment identifier using %NN escape
	 * mechanism as appropriate. The '%' character is assumed to already
	 * indicated an escape byte. The '%' character must be followed by two
	 * hexadecimal digits.
	 * 
	 * @param unicode
	 *            The uri, in characters specified by RFC 2396 + '#'
	 * @return The corresponding Unicode String
	 */
	static public String encode(String unicode) {
		return URIref.encode(unicode);
	}

	/**
	 * Convert a URI, in US-ASCII, with escaped characters taken from UTF-8, to
	 * the corresponding Unicode string. On ill-formed input the results are
	 * undefined, specifically if the unescaped version is not a UTF-8 String,
	 * some String will be returned. Escaped '%' characters (i.e. "%25") are
	 * left unchanged.
	 * 
	 * @param uri
	 *            The uri, in characters specified by RFC 2396 + '#'.
	 * @return The corresponding Unicode String.
	 * @exception IllegalArgumentException
	 *                If a % hex sequence is ill-formed.
	 */
	static public String decode(String uri) {
		return URIref.decode(uri);
	}

	public static void main(String[] args) {
		System.out.println(clean("H�llo/OP:� - regex,t\nest.{machine\\"));
	}

}
