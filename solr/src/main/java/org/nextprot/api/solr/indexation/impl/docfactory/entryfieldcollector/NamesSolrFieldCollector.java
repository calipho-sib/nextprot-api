package org.nextprot.api.solr.indexation.impl.docfactory.entryfieldcollector;

import org.nextprot.api.core.domain.EntityName;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.Family;
import org.nextprot.api.core.domain.Overview;
import org.nextprot.api.solr.core.impl.schema.EntrySolrField;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Service
public class NamesSolrFieldCollector extends EntrySolrFieldCollector {

	@Override
	public void collect(Entry entry, boolean gold) {
		
		Overview ovv = entry.getOverview();
		
		//TODO Daniel repeated code in CvFieldBuilder

		List<EntityName> altnames = ovv.getProteinNames();
		if(altnames != null )
			for (EntityName altname : altnames) {
				List <EntityName> paltnames = altname.getSynonyms();
				if(paltnames != null )
				for (EntityName paltfullname : paltnames) {
					if(!paltfullname.getType().equals("enzyme name")) // Enzymes are delt with elsewhere
						addEntrySolrFieldValue(EntrySolrField.ALTERNATIVE_NAMES, paltfullname.getName());
				    List <EntityName> paltshortnames = paltfullname.getSynonyms();
				    if(paltshortnames != null )
				      for (EntityName paltshortname : paltshortnames) {
				        if(!paltshortname.getType().equals("enzyme name")) addEntrySolrFieldValue(EntrySolrField.ALTERNATIVE_NAMES, paltshortname.getName());
				    }
				}
			}
		
		altnames = ovv.getAdditionalNames(); // special names (INN, allergens)
		if(altnames != null )
			for (EntityName altname : altnames) {
				//System.err.println(altname.getName());
				addEntrySolrFieldValue(EntrySolrField.ALTERNATIVE_NAMES, altname.getName());
				String nametype = altname.getType();
		    	if(nametype.equals("CD antigen"))  
				  addEntrySolrFieldValue(EntrySolrField.CD_ANTIGEN, altname.getName());
		    	else if(nametype.equals("International Nonproprietary Names"))  
				  addEntrySolrFieldValue(EntrySolrField.INTERNATIONAL_NAME, altname.getName());
			}
		
		altnames = ovv.getFunctionalRegionNames(); // The enzymatic activities of a multifunctional enzyme (maybe redundent with getEnzymes)
		if(altnames != null )
			for (EntityName altname : altnames) {
				addEntrySolrFieldValue(EntrySolrField.REGION_NAME, altname.getName()); // region_name should be renamed activity_name
				List <EntityName> paltnames = altname.getSynonyms();
				if(paltnames != null )
				 for (EntityName ecname : paltnames) {
				    addEntrySolrFieldValue(EntrySolrField.REGION_NAME, ecname.getName());
				    List <EntityName> shortnames = ecname.getSynonyms();
				    if(shortnames != null ){
				    	for (EntityName xname : shortnames)	addEntrySolrFieldValue(EntrySolrField.REGION_NAME, xname.getName());
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
							addEntrySolrFieldValue(EntrySolrField.ALTERNATIVE_GENE_NAMES, genesynoname.getName());
					}
				}			
			addEntrySolrFieldValue(EntrySolrField.RECOMMENDED_GENE_NAMES, allgenenames);
			addEntrySolrFieldValue(EntrySolrField.RECOMMENDED_GENE_NAMES_S, allgenenames);
			
			List <String> orfnames = getORFNames(ovv);
			if(orfnames != null)
				for( String orfname : orfnames)
					addEntrySolrFieldValue(EntrySolrField.ORF_NAMES, orfname);
			
		}
		//else System.err.println("no gene names for: " + entry.getUniqueName());
		
		List<Family> families = ovv.getFamilies();
		String allfamilies = null;
		for (Family family : families) { // We choose to index the descriptions (like "Belongs to the DNase II family") instead of the names
			if (allfamilies == null) allfamilies = family.getDescription(); // allfamilies = family.getName();
			else allfamilies += " , " + family.getDescription();
		}

		if (allfamilies != null) {
			addEntrySolrFieldValue(EntrySolrField.FAMILY_NAMES, allfamilies);
			addEntrySolrFieldValue(EntrySolrField.FAMILY_NAMES_S, allfamilies);
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
	public Collection<EntrySolrField> getCollectedFields() {
		return Arrays.asList(EntrySolrField.INTERNATIONAL_NAME, EntrySolrField.CD_ANTIGEN, EntrySolrField.ORF_NAMES, EntrySolrField.FAMILY_NAMES, EntrySolrField.FAMILY_NAMES_S, EntrySolrField.ALTERNATIVE_GENE_NAMES, EntrySolrField.RECOMMENDED_GENE_NAMES, EntrySolrField.RECOMMENDED_GENE_NAMES_S, EntrySolrField.REGION_NAME, EntrySolrField.ALTERNATIVE_NAMES);
	}

}
