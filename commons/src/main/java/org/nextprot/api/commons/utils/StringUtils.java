package org.nextprot.api.commons.utils;

import com.google.common.base.Preconditions;

import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.regex.Pattern;

public class StringUtils {

	private static final Pattern NON_ASCIIDASH = Pattern.compile("[^\\w-]");
	private static final Pattern WHITESPACE = Pattern.compile("\\s");

	public static StringCaseFormatter createXCaseBuilder(String string) {

		return new StringCaseFormatter(string);
	}

	public static String toCamelCase(final String inputString, boolean firstLetterFirstWordInLowerCase) {

		return new StringCaseFormatter(inputString).camel(firstLetterFirstWordInLowerCase).format();
	}

	public static String camelToKebabCase(String inputString){

		return new StringCaseFormatter(inputString).kebab().format();
	}

	public static String camelToSnakeCase(String inputString) {

		return new StringCaseFormatter(inputString).snake().format();
	}

	public static String snakeToKebabCase(String inputString) {

		return new StringCaseFormatter(inputString).camel().kebab().format();
	}

	/**
	 * Non word characters/hyphen are removed, punctuation and spaces are replaced by single underscores.
	 *
	 * @param unicode
	 * @return
	 */
	public static String slug(String unicode) {

		String nowhitespace = WHITESPACE.matcher(unicode).replaceAll("_");
		String normalized = Normalizer.normalize(nowhitespace, Form.NFD)
				.replaceAll("[:;.,/(){}\\\\]", "_");

		return NON_ASCIIDASH.matcher(normalized).replaceAll("");
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

	static public String lowerFirstChar(String s) {
		if (null==s) return null;
		if (s.length()==0) return "";
		return s.substring(0,1).toLowerCase() + s.substring(1);
	}
	
	static public String upperFirstChar(String s) {
		if (null==s) return null;
		if (s.length()==0) return "";
		return s.substring(0,1).toUpperCase() + s.substring(1);
	}

	/**
	 * Remove the <+> characters from a string
	 * Used for suggestions retuurned by solr service
	 * see issue https://issues.isb-sib.ch/browse/CALIPHOMISC-72
	 * @param s a string
	 * @return s with <+> chars removed if any are found
	 */
	static public String removePlus(String s) {
    	return s.replace("+", "");
    }

	static public String capitalizeFirstLetter(String input){
		return input.substring(0, 1).toUpperCase() + input.substring(1);
	}
	
	static public String quote(String input){
		return "\"" + input + "\"" ;
	}
	
	static public boolean isWholeNumber(String input){
		  return input.matches("\\d+");  //match a number with optional '-' and decimal.
	}

	/**
	 * Recursively format text with lines of <code>max</code> length.
	 *
	 * @param text the text to format
	 * @param maxLineLen the maximum line length
	 * @return formatted text
	 */
	public static String wrapTextRec(String text, int maxLineLen) {

		Preconditions.checkNotNull(text);
		Preconditions.checkArgument(maxLineLen > 0);

		return wrapTextRec(text, maxLineLen, new StringBuilder());
	}

	/**
	 * Format text with lines of <code>max</code> length
	 *
	 * @param text the text to format
	 * @param maxLineLen the maximum line length
	 * @return formatted text
	 */
	public static String wrapText(String text, int maxLineLen) {

		Preconditions.checkNotNull(text);
		Preconditions.checkArgument(maxLineLen > 0);

		StringBuilder sb = new StringBuilder();

		int textLen = text.length();

		int begin=0;
		while (begin<textLen) {

			int end = begin + maxLineLen;

			if (end > textLen) {
				sb.append(text.substring(begin));
				break;
			}

			sb.append(text.substring(begin, end)).append("\n");

			begin = end;
		}

		return sb.toString();
	}

	static String wrapTextRec(String text, int maxLineLen, StringBuilder sb) {

		if (text.length()<maxLineLen) {

			sb.append(text);

			return sb.toString();
		} else {

			String head = text.substring(0, maxLineLen);
			String tail = text.substring(maxLineLen);

			sb.append(head).append("\n");

			return wrapTextRec(tail, maxLineLen, sb);
		}
	}
	
	public static String removeHtmlTags(String htmlString){
		return htmlString.replaceAll("\\<.*?>","");
	}
}
