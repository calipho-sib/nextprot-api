package org.nextprot.api.tasks.solr.indexer.entry.impl;

import java.util.Collection;

import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.solr.index.EntryIndex.Fields;
import org.nextprot.api.tasks.solr.indexer.entry.EntryFieldBuilder;
import org.nextprot.api.tasks.solr.indexer.entry.FieldBuilder;

@EntryFieldBuilder
public class NamesFieldBuilder extends FieldBuilder {

	@Override
	protected void init(Entry entry) {
		
		/*
		List <EntityName> altnames = null;
		altnames = ovv.getProteinNames();
		if(altnames != null )
			for (EntityName altname : altnames) {
				List <EntityName> paltnames = altname.getSynonyms();
				if(paltnames != null )
				for (EntityName paltfullname : paltnames) {
				    doc.addField("alternative_names", paltfullname.getName());
				    List <EntityName> paltshortnames = paltfullname.getSynonyms();
				    if(paltshortnames != null )
				    for (EntityName paltshortname : paltshortnames) {
				    	doc.addField("alternative_names", paltshortname.getName());
				    }
				}
			}
		
		altnames = ovv.getAdditionalNames(); // special names (INN, allergens)
		if(altnames != null )
			for (EntityName altname : altnames) {
				doc.addField("alternative_names", altname.getName());
			}
		
		altnames = ovv.getFunctionalRegionNames(); // The enzymatic activities of a multifunctional enzyme (maybe redundent with getEnzymes)
		if(altnames != null )
			for (EntityName altname : altnames) {
				doc.addField("region_name", altname.getName()); // region_name should be renamed activity_name
				// Synonyms allready collected in the getEnzymes loop
				// List <EntityName> paltnames = altname.getSynonyms();
				// if(paltnames != null )
				// for (EntityName ecname : paltnames) {
				    //doc.addField("ec_name", ecname.getName());
				//	System.err.println(id + " fromincludes: " + ecname.getName());
				//} 
			}
		
		// Gene names, synonyms and orf names
		List <EntityName> genenames = ovv.getGeneNames();
		if(genenames != null ) {
			String maingenename = ovv.getMainGeneName(); // TODO: check for multigene entries
			doc.addField("recommended_gene_names", maingenename);
			doc.addField("recommended_gene_names_s", maingenename);
			for (EntityName currname : genenames) {
				List <EntityName> genesynonames = currname.getSynonyms();
				if(genesynonames != null)
				for (EntityName genesynoname : genesynonames) {
				doc.addField("alternative_gene_names", genesynoname.getName());
			    //System.err.println("syn: " + genesynoname.getName()); 
				}
			}
		}
		//else System.err.println("no gene names for: " + id);
		
		List<Family> families = ovv.getFamilies();
		String allfamilies = null;
		for (Family family : families) { // alternatively use a multivalue solr field
			if(allfamilies == null) allfamilies = family.getName();
			else allfamilies += " | " + family.getName();
			cv_acs.add(family.getAccession());
			doc.addField("cv_acs", family.getAccession());
		}
		if(allfamilies == null) {doc.addField("family_names", allfamilies); doc.addField("family_names_s", allfamilies);}
		*/

	
	}

	@Override
	public Collection<Fields> getSupportedFields() {
		return null;
	}

}
