package org.nextprot.api.tasks.solr.indexer.entry.impl;

import com.google.common.base.Joiner;
import org.nextprot.api.core.domain.ChromosomalLocation;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.solr.index.EntryField;
import org.nextprot.api.tasks.solr.indexer.entry.EntryFieldBuilder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

@Component
public class ChromosomeFieldBuilder extends EntryFieldBuilder {


	@Override
	protected void init(Entry entry) {

		// build GENE_BAND by concatenating distinct band and chr+band
		// build CHR_LOC field by concatenating distinct chromosomal locations (chr + band) after sorting them alphabetically
		// build CHR_LOCS field based of first element in CHR_LOC 
		// Note that CHR_LOC is displayed in UI search result and CHR_LOC_S is used to sort UI search result
		// this is why it is important to compute the CHR_LOC_S based on the first location displayed in UI (consistency)
		List<String> gblist = new ArrayList<String>();
		Set<String> clset = new TreeSet<String>();
		Set<String> gbset = new TreeSet<String>( Collections.reverseOrder() );
		// The reverse is important otherwise solr may find wrong locations with queries like 11q13 in "19q13.11 q13.11"
		List<ChromosomalLocation> chrlocs = entry.getChromosomalLocations();
		for (ChromosomalLocation data : chrlocs) {
			String ch = data.getChromosome()==null ? "" : data.getChromosome();
			String gb1 = data.getBand()==null ? "" : data.getBand();
			String gb2 = ch + gb1;
			String cl = ch + ("unknown".equals(gb1) ? "" : gb1);
			gbset.add(gb1);
			gbset.add(gb2);
			clset.add(cl);
		}
		String gene_band = Joiner.on(" ").skipNulls().join(gbset).trim();
		String chr_loc = Joiner.on(" ").skipNulls().join(clset).trim();
		Integer chr_loc_s = sortChr(chr_loc);
		addField(EntryField.GENE_BAND, gene_band);
		addField(EntryField.CHR_LOC, chr_loc);
		addField(EntryField.CHR_LOC_S, chr_loc_s);
	}


	@Override
	public Collection<EntryField> getSupportedFields() {
		return Arrays.asList(EntryField.CHR_LOC, EntryField.CHR_LOC_S, EntryField.GENE_BAND);
	}

	// Allows to sort results based on chromosomal location
	public static Integer sortChr(String chrs) {
		
		// base the computation of chr_loc_s on first chr_loc
		String chr = chrs.split(" ")[0];

		String[] chr_loc = chr.split("([pq]|cen)"); // split on p or q
		Integer f_chr0 = 1000000;
		Integer f_q = 50000;
		Integer f_chr1 = 1000;
		Integer max_chr = 50; // max chr location after pq
		Integer chr0, chr1;

		// push unknown chromosome at the end
		if (chr.indexOf("unknown") > -1 || chr.equals("")) {
			return f_chr0 * 30;
		}
		if (chr_loc[0].equalsIgnoreCase("x")) {
			chr0 = 23 * f_chr0;
		}
		else if (chr_loc[0].equalsIgnoreCase("y")) {
			chr0 = 24 * f_chr0;
		}
		else if (chr_loc[0].equalsIgnoreCase("mt")) {
			chr0 = 25 * f_chr0;
		}
		else {
			chr0 = Integer.parseInt(chr_loc[0]) * f_chr0;
		}
		
		// sort(cen) = 10E5*XX + 10E4-1
		if (chr.indexOf("cen") > -1)
			return chr0 + f_q - 1;
		// sort(chr) = 10E5*XX
		if (chr_loc.length == 1)
			return (chr0);

		// extract double value from digits after p or q
		Double aux = (Double.parseDouble(chr_loc[1].split("[-,]")[0]) * f_chr1);
		chr1 = aux.intValue();

		// sort(q) = 10E5*XX + 10E4 + 100*YY
		if (chr.indexOf('q') > -1)  {
			return chr0 + chr1 + f_q;
		}
		
		// sort(p) = 10E6*XX + 1000*(45-YY) //descending order
		return chr0 + f_chr1 * max_chr - chr1;
	}
	
}
