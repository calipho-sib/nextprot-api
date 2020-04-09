package org.nextprot.api.commons.utils;

import com.google.common.base.Preconditions;

import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.Arrays;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Pattern;

public class StringUtils {

	private static final String CR = "\r";
	public static final String CR_LF = CR+"\n";
	private static final Pattern NON_ASCIIDASH = Pattern.compile("[^\\w-]");
	private static final Pattern WHITESPACE = Pattern.compile("\\s");
	private static final AuthorNameFormatter AUTHOR_NAME_FORMATTER = new AuthorNameFormatter();

	private StringUtils() {
		throw new AssertionError("should not be instanciable");
	}

	public static boolean isEnsgAccession(String accession) {
		return accession !=null && accession.startsWith("ENSG");
	}
	
	public static boolean isVirtualGeneAccession(String accession) {
		return accession !=null && accession.startsWith("VG_");
	}
	
	public static StringFormatter createXCaseBuilder(String string) {

		return new StringFormatter(string);
	}

	public static String toCamelCase(String inputString, boolean firstWordLetterLowerCase) {

		return new StringFormatter(inputString).camelFirstWordLetterLowerCase(firstWordLetterLowerCase).format();
	}
	
	public static String camelToKebabCase(String inputString){

		return new StringFormatter(inputString).kebab().format();
	}

	public static String camelToSnakeCase(String inputString) {

		return new StringFormatter(inputString).snake().format();
	}

	public static String snakeToKebabCase(String inputString) {

		return new StringFormatter(inputString).camel().kebab().format();
	}

	/**
	 * Non word characters/hyphen are removed, punctuation and spaces are replaced by single underscores.
	 *
	 * @param unicode
	 * @return
	 */
	public static String slug(String unicode, String pattern, String replaceChar) {

		String nowhitespace = WHITESPACE.matcher(unicode).replaceAll(replaceChar);
		String normalized = Normalizer.normalize(nowhitespace, Form.NFD).replaceAll(pattern, replaceChar);

		return NON_ASCIIDASH.matcher(normalized).replaceAll("");
	}
	public static String slug(String unicode) {
		//TODO: PAM remove next line (temp fix)
		if (unicode==null) return "nullValue";
		return slug(unicode, "[:;.,/(){}\\\\]",  "_");
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

	static public String uppercaseFirstLetter(String input){
        if (null == input || input.isEmpty()) {
            return input;
        }

		return input.substring(0, 1).toUpperCase() + input.substring(1);
	}
	
	static public String quote(String input){
		return "\"" + input + "\"" ;
	}
	
	static public boolean isWholeNumber(String input){
		  return input.matches("\\d+");  //match a number with optional '-' and decimal.
	}

	public static AuthorNameFormatter getAuthorNameFormatter() {

		return AUTHOR_NAME_FORMATTER;
	}

	/**
	 * Recursively format text with lines of <code>max</code> length.
	 *
	 * @param text the text to format
	 * @param maxLineLen the maximum line length
	 * @return formatted text
	 */
	public static String wrapText(String text, int maxLineLen) {

		Preconditions.checkNotNull(text);
		Preconditions.checkArgument(maxLineLen > 0);

		return wrapTextRec(text, maxLineLen, new StringBuilder());
	}

	static String wrapTextRec(String text, int maxLineLen, StringBuilder sb) {

		if (text.length()<=maxLineLen) {

			sb.append(text);

			return sb.toString();
		} else {

			String head = text.substring(0, maxLineLen);
			String tail = text.substring(maxLineLen);

			sb.append(head).append(CR_LF);

			return wrapTextRec(tail, maxLineLen, sb);
		}
	}
	
	public static String removeHtmlTags(String htmlString){
		return htmlString.replaceAll("\\<.*?>","");
	}
	
	
	public static String getSortedValueFromPipeSeparatedField(String pipefield) {
		String aux = "";

		if(!pipefield.contains("|")) return pipefield;

		SortedSet<String> sset = new TreeSet<>(Arrays.asList(pipefield.split(" \\| ")));
		for(String elem: sset) {
			if(aux != "") aux += " | ";
			aux += elem;
		}
		return aux;
	}

	/**
	 * Concatenate strings (null String replaced by "")
	 * @param strings the strings to concatenate
	 * @return the concatenated strings
	 */
	public static String concat(String... strings) {

		StringBuilder sb = new StringBuilder();

		for (String str : strings) {

			sb.append((str != null) ? str : "");
		}

		return sb.toString();
	}

	/**
	 * Get carriage return (used in velocity template)
	 * @return carriage return
	 */
	public static String getCR() {
		return CR;
	}
}
