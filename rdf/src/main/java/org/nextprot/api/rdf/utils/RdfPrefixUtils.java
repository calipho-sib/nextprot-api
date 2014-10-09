package org.nextprot.api.rdf.utils;

import java.util.HashMap;
import java.util.Map;

public class RdfPrefixUtils {

	private static Map<String, String> ns2pfx = null;

	public static String getPrefixFromNameSpace(String prefixes, String ns) {
		initialize(prefixes);
		return ns2pfx.get(ns);
	}

	public static String getPrefixedNameFromURI(String prefixes, String uri) {
		initialize(prefixes);
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

	private static void initialize(String prefixes) {
		if (ns2pfx == null) {
			ns2pfx = new HashMap<String, String>();

			// replaces any TAB with SPACE & remove contiguous SPACEs
			String pfx = prefixes.replace("\t", " ");
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

	}

}
