package org.nextprot.api.tasks.solr.indexer.entry.impl;

import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.Identifier;
import org.nextprot.api.solr.index.EntryField;
import org.nextprot.api.tasks.solr.indexer.entry.EntryFieldBuilder;
import org.nextprot.api.tasks.solr.indexer.entry.FieldBuilder;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@EntryFieldBuilder
public class IdentifierFieldBuilder extends FieldBuilder {

	@Override
	protected void init(Entry entry) {
		
		// Identifiers
		List <Identifier> identifiers = entry.getIdentifiers();
		for (Identifier currident : identifiers) {
			String idtype = currident.getType();
			//if(currident.getDatabase() == null)
			//System.err.println("type: " + idtype + " " + currident.getName());
			if(idtype.equals("Secondary AC")) {
				addField(EntryField.ALTERNATIVE_ACS, currident.getName());
			}
			else if (idtype.equals("IMAGE") || idtype.equals("FLJ") || idtype.equals("MGC") || idtype.equals("DKFZ") || idtype.equals("Others")) {
				addField(EntryField.CLONE_NAME, currident.getName());
			} else if (idtype.equals("Illumina") || idtype.equals("Affymetrix")){
				addField(EntryField.MICROARRAY_PROBE, currident.getName());
			}
			else if (idtype.equals("Entry name")) {
				addField(EntryField.UNIPROT_NAME, currident.getName());
			}
		}
		
	}

	@Override
	public Collection<EntryField> getSupportedFields() {
		return Arrays.asList(EntryField.ALTERNATIVE_ACS, EntryField.CLONE_NAME, EntryField.MICROARRAY_PROBE, EntryField.UNIPROT_NAME);
	}

}
