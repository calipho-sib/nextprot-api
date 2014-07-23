package org.nextprot.api.commons.utils;

import java.util.HashMap;
import java.util.Map;

public class RdfUtils {


	private static final Map<String, String> ns2pfx = new HashMap<String, String>();
	public static final String RDF_PREFIXES = FileUtils.readResourceAsString("/sparql/prefix.rq");
	public static final String BLANK_OBJECT_TYPE = "BlankNodeType";

	static {
		// replaces any TAB with SPACE & remove contiguous SPACEs
		String pfx = RDF_PREFIXES.replace("\t", " ");
		int lng = pfx.length();
		while (true) {
			pfx = pfx.replace("  ", " ");
			if (pfx.length() == lng)
				break;
			lng = pfx.length();
		}
		// put namespace -> prefix into the map
		String[] lines = pfx.split("\n");
		for (int i = 0; i < lines.length; i++) {
			String line = lines[i].trim();
			String[] elems = line.split(" ");
			String ns = elems[2].replace("<", "").replace(">", "");
			String px = elems[1];
			ns2pfx.put(ns, px);
		}

	}

	public static String getPrefixFromNameSpace(String ns) {
		return ns2pfx.get(ns);
	}

	public static String getPrefixedNameFromURI(String uri) {
		String ns = "";
		String ln = "";
		for (String k : ns2pfx.keySet()) {
			if (uri.startsWith(k) && k.length() > ns.length())
				ns = k;
		}
		ln = uri.substring(ns.length());
		if (ns2pfx.containsKey(ns))
			ns = ns2pfx.get(ns);
		return ns + ln;
	}

}
