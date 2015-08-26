package org.nextprot.api.tasks.solr.indexer.entry.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.core.domain.ChromosomalLocation;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.solr.index.EntryIndex.Fields;
import org.nextprot.api.tasks.solr.indexer.entry.EntryFieldBuilder;
import org.nextprot.api.tasks.solr.indexer.entry.FieldBuilder;


@EntryFieldBuilder
public class ChromosomeFieldBuilder implements FieldBuilder{
	
	private String chrLoc = null;
	private Integer chrLocS = -1;
	private List<String> geneBands = new ArrayList<>();
	
	public ChromosomeFieldBuilder (Entry entry){
		init(entry);
	}
	
	private void init(Entry entry){
		List <ChromosomalLocation> chrlocs = entry.getChromosomalLocations();
		for (ChromosomalLocation currloc : chrlocs) {
			if(chrLoc == null) chrLoc = currloc.getChromosome();
			String band = currloc.getBand();
			if(band != null) {
				chrLoc += band;
				geneBands.add(band);
			}
		}
		chrLocS = sortChr(chrLoc);
	}
	
	
	@Override
	public <T> T build(Entry entry, Fields field, Class<T> requiredType){
		
		if(field.equals(Fields.CHR_LOC)) return requiredType.cast(chrLoc);
		if(field.equals(Fields.CHR_LOC_S)) return requiredType.cast(chrLocS);
		if(field.equals(Fields.GENE_BAND)) return requiredType.cast(geneBands);

		throw new NextProtException("Unsupported type " + field);
			
	}

	@Override
	public Collection<Fields> getSupportedFields() {
		return Arrays.asList(Fields.CHR_LOC, Fields.CHR_LOC_S, Fields.GENE_BAND);
	}
	
	
	static Integer sortChr(String chr) {
		// Allows to sort results based on chromosomal location
		chr=chr.trim();
		
		String[] chr_loc=chr.split("([pq]|cen)");  // split on p or q
		Integer f_chr0=1000000; 	 
		Integer f_q=50000; 	 
		Integer f_chr1=1000; 		
		Integer  max_chr=50;		// max chr localtion after pq 
		Integer chr0, chr1;

		
		// push unknown chromosome at the end 
		if (chr.indexOf("unknown")>-1 || chr.equals("")) { return f_chr0*30; }
		if(chr_loc[0].equalsIgnoreCase("x")){ chr0=23*f_chr0;}
		else if(chr_loc[0].equalsIgnoreCase("y")) { chr0=24*f_chr0; }
		else if(chr_loc[0].equalsIgnoreCase("mt")) { chr0=25*f_chr0;}
		else { chr0=Integer.parseInt(chr_loc[0])*f_chr0; }
		
		// sort(cen) = 10E5*XX + 10E4-1
		if (chr.indexOf("cen")>-1)	return chr0+f_q-1;			
		// sort(chr) = 10E5*XX 
		if (chr_loc.length==1) return (chr0);
		
		// extract double value from digits after p or q
		Double aux = (Double.parseDouble(chr_loc[1].split("[-,]")[0]) * f_chr1);
		chr1 = aux.intValue();
		
		// sort(q) = 10E5*XX + 10E4 + 100*YY
		if(chr.indexOf('q')>-1) return chr0+chr1+f_q;			
		
		// sort(p) = 10E6*XX + 1000*(45-YY)  //descending order
		return chr0 + f_chr1 * max_chr - chr1;
	}

}
