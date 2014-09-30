package org.nextprot.api.rdf.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.nextprot.api.commons.utils.SparqlDictionary;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
@Lazy
public class RdfPrefixService implements InitializingBean{

	@Autowired private SparqlDictionary sparqlDictionary = null;

	private Map<String, String> ns2pfx = null;
	
	public RdfPrefixService (){
		ns2pfx = new HashMap<String, String>();
	}

	public String getPrefixFromNameSpace(String ns) {
		return ns2pfx.get(ns);
	}

	public String getPrefixedNameFromURI(String uri) {
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

	@Override
	public void afterPropertiesSet() throws Exception {
		
		// replaces any TAB with SPACE & remove contiguous SPACEs
		String pfx = sparqlDictionary.getSparqlPrefixes().replace("\t", " ");
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
