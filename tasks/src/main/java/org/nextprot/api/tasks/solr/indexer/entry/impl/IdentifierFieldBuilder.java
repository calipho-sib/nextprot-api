package org.nextprot.api.tasks.solr.indexer.entry.impl;

import java.util.Collection;

import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.solr.index.EntryIndex.Fields;
import org.nextprot.api.tasks.solr.indexer.entry.EntryFieldBuilder;
import org.nextprot.api.tasks.solr.indexer.entry.FieldBuilder;

@EntryFieldBuilder
public class IdentifierFieldBuilder extends FieldBuilder {

	@Override
	protected void init(Entry entry) {

		/*
		// Identifiers
		List <Identifier> identifiers = entry.getIdentifiers();
		for (Identifier currident : identifiers) {
			String idtype = currident.getType();
			//if(currident.getDatabase() == null)
			//System.err.println("type: " + idtype + " " + currident.getName());
			if(idtype.equals("Secondary AC")) doc.addField("alternative_acs", currident.getName());
			else if (idtype.equals("IMAGE") || idtype.equals("FLJ") || idtype.equals("MGC") || idtype.equals("DKFZ") || idtype.equals("Others")) doc.addField("clone_name", currident.getName());
			else if (idtype.equals("Illumina") || idtype.equals("Affymetrix")) doc.addField("microarray_probe", currident.getName());
			else if (idtype.equals("Entry name"))  doc.addField("uniprot_name", currident.getName());
			//else System.err.println("type: " + idtype);
		}
		*/
	}

	@Override
	public Collection<Fields> getSupportedFields() {
		return null;
	}

}
