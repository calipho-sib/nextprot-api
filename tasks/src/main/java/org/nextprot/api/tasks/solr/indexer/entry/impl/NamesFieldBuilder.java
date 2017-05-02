package org.nextprot.api.tasks.solr.indexer.entry.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.nextprot.api.core.dao.EntityName;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.Family;
import org.nextprot.api.core.domain.Overview;
import org.nextprot.api.solr.index.EntryIndex.Fields;
import org.nextprot.api.tasks.solr.indexer.entry.EntryFieldBuilder;
import org.nextprot.api.tasks.solr.indexer.entry.FieldBuilder;

@EntryFieldBuilder
public class NamesFieldBuilder extends FieldBuilder {

	@Override
	protected void init(Entry entry) {
		
		Overview ovv = entry.getOverview();
		
		//TODO Daniel repeated code in CvFieldBuilder
		
		List <EntityName> altnames = null;
		altnames = ovv.getProteinNames();
		if(altnames != null )
			for (EntityName altname : altnames) {
				List <EntityName> paltnames = altname.getSynonyms();
				if(paltnames != null )
				for (EntityName paltfullname : paltnames) {
					if(!paltfullname.getType().equals("enzyme name")) // Enzymes are delt with elsewhere
						addField(Fields.ALTERNATIVE_NAMES, paltfullname.getName());
				    List <EntityName> paltshortnames = paltfullname.getSynonyms();
				    if(paltshortnames != null )
				      for (EntityName paltshortname : paltshortnames) {
				        if(!paltshortname.getType().equals("enzyme name")) addField(Fields.ALTERNATIVE_NAMES, paltshortname.getName());
				    }
				}
			}
		
		altnames = ovv.getAdditionalNames(); // special names (INN, allergens)
		if(altnames != null )
			for (EntityName altname : altnames) {
				//System.err.println(altname.getName());
				addField(Fields.ALTERNATIVE_NAMES, altname.getName());
				String nametype = altname.getType();
		    	if(nametype.equals("CD antigen"))  
				  addField(Fields.CD_ANTIGEN, altname.getName());
		    	else if(nametype.equals("International Nonproprietary Names"))  
				  addField(Fields.INTERNATIONAL_NAME, altname.getName());
			}
		
		altnames = ovv.getFunctionalRegionNames(); // The enzymatic activities of a multifunctional enzyme (maybe redundent with getEnzymes)
		if(altnames != null )
			for (EntityName altname : altnames) {
				addField(Fields.REGION_NAME, altname.getName()); // region_name should be renamed activity_name
				List <EntityName> paltnames = altname.getSynonyms();
				if(paltnames != null )
				 for (EntityName ecname : paltnames) {
				    addField(Fields.REGION_NAME, ecname.getName());
				    List <EntityName> shortnames = ecname.getSynonyms();
				    if(shortnames != null ){
				    	for (EntityName xname : shortnames)	addField(Fields.REGION_NAME, xname.getName());
				    }
				} 
			}
		
		// Gene names, synonyms and orf names
		List <EntityName> genenames = ovv.getGeneNames();
		if(genenames != null ) {
			String allgenenames = null;
			for (EntityName currname : genenames) { // Concatenate official gene names
				if (allgenenames == null) allgenenames = currname.getName();
				else allgenenames += "; " + currname.getName();
				List <EntityName> genesynonames = currname.getSynonyms();
				if(genesynonames != null)
					for (EntityName genesynoname : genesynonames) {
						if(!genesynoname.getType().equals("open reading frame"))
							addField(Fields.ALTERNATIVE_GENE_NAMES, genesynoname.getName());
					}
				}			
			addField(Fields.RECOMMENDED_GENE_NAMES, allgenenames);
			addField(Fields.RECOMMENDED_GENE_NAMES_S, allgenenames);
			
			List <String> orfnames = getORFNames(ovv);
			if(orfnames != null)
				for( String orfname : orfnames)
					addField(Fields.ORF_NAMES, orfname);
			
		}
		//else System.err.println("no gene names for: " + entry.getUniqueName());
		
		List<Family> families = ovv.getFamilies();
		String allfamilies = null;
		for (Family family : families) { // We choose to index the descriptions (like "Belongs to the DNase II family") instead of the names
			if (allfamilies == null) allfamilies = family.getDescription(); // allfamilies = family.getName();
			else allfamilies += " , " + family.getDescription();
		}

		if (allfamilies != null) {
			addField(Fields.FAMILY_NAMES, allfamilies);
			addField(Fields.FAMILY_NAMES_S, allfamilies);
		}
	}
	
	static List<String> getORFNames (Overview ovv){
		List<String> orfnames = new ArrayList<String>();
		for(EntityName gn : ovv.getGeneNames()){
			if(gn.getCategory().equals("ORF")){
				orfnames.add(gn.getName());
			}
			List<EntityName> synonyms = gn.getSynonyms();
			if(synonyms != null)
				for(EntityName syn: gn.getSynonyms()){
					if(syn.getCategory().equals("ORF"))	orfnames.add(syn.getName());
					}
		}
		return orfnames;
	}

	@Override
	public Collection<Fields> getSupportedFields() {
		return Arrays.asList(Fields.INTERNATIONAL_NAME, Fields.CD_ANTIGEN, Fields.ORF_NAMES, Fields.FAMILY_NAMES, Fields.FAMILY_NAMES_S, Fields.ALTERNATIVE_GENE_NAMES, Fields.RECOMMENDED_GENE_NAMES, Fields.RECOMMENDED_GENE_NAMES_S, Fields.REGION_NAME, Fields.ALTERNATIVE_NAMES);
	}

}
