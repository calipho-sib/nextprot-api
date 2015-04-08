package org.nextprot.api.commons.exception;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExceptionUtils {

	public static final String EXAMPLE1 = "Malformed SPARQL: Lexical error at line 30, column 54. Encountered: \"\\n\" (10), after: \"kfdhjf\"";
	public static final String EXAMPLE2 = "Encountered \" <INTEGER> \"22 \"\" at line 31, column 45.\n"
			+ "Was expecting one of:\n" + "\"values\" ...\n" + "\"graph\" ...\n" + "\"optional\" ...\n"
			+ "\"minus\" ...\n" + "\"bind\" ...\n" + "\"service\" ...\n" + "\"filter\" ...\n" + "\"{\" ...\n"
			+ "\"}\" ...\n" + "\";\" ...\n" + "\",\" ...\n" + "\".\" ...";

	static public String fixLineNumberInErrorMessage(String errMsg) {
		try {
			Pattern p = Pattern.compile("(^.*at line )([0-9]+)(,.*$)");
			String[] lines = errMsg.split("\n");
			String newMsg = "";
			for (String line: lines) {
				Matcher m = p.matcher(line);
				if (m.matches() && m.groupCount() == 3) {
					String realLineNo = String.valueOf(Integer.parseInt(m.group(2)) - 26);
					newMsg +=  m.group(1) + realLineNo + m.group(3) + "\n";
				} else {
					newMsg += line + "\n";
				}
			}
			return newMsg;
		} catch (Exception e) {
			return errMsg;
		}
	}

	public static void main(String[] args) {
		String s = EXAMPLE1;
		System.out.println("Original: " + s);
		System.out.println("Modified: " + fixLineNumberInErrorMessage(s));

	}

}
