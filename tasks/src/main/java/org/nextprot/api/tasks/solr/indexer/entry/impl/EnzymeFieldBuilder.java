package org.nextprot.api.tasks.solr.indexer.entry.impl;

import java.util.Collection;

import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.solr.index.EntryIndex.Fields;
import org.nextprot.api.tasks.solr.indexer.entry.EntryFieldBuilder;
import org.nextprot.api.tasks.solr.indexer.entry.FieldBuilder;

@EntryFieldBuilder
public class EnzymeFieldBuilder extends FieldBuilder {

	@Override
	protected void init(Entry entry) {

		/*
		List<Terminology> enzymes = entry.getEnzymes();
		String ec_names = "";
		for (Terminology currenzyme : enzymes) {
			//TODO DANIEL cvac_cnt++;
			cv_acs.add(currenzyme.getAccession());
			doc.addField("cv_names", currenzyme.getName());
			ec_names += "EC " + currenzyme.getAccession() + ", ";
			
			List <String> synonyms = currenzyme.getSynonyms();
			if(synonyms != null)
			   for (String synonym : synonyms)  doc.addField("cv_synonyms", synonym.trim());
		}
		doc.addField("ec_name", ec_names);
		*/
	}

	@Override
	public Collection<Fields> getSupportedFields() {
		return null;
	}

}
