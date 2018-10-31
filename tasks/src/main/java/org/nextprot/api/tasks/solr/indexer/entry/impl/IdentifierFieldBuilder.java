package org.nextprot.api.tasks.solr.indexer.entry.impl;

import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.Identifier;
import org.nextprot.api.solr.index.EntryField;
import org.nextprot.api.tasks.solr.indexer.entry.EntryFieldBuilder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Service
public class IdentifierFieldBuilder extends EntryFieldBuilder {

	@Override
	public void collect(Entry entry, boolean gold) {
		
		// Identifiers
		List <Identifier> identifiers = entry.getIdentifiers();
		for (Identifier currident : identifiers) {
			String idtype = currident.getType();
			//if(currident.getDatabase() == null)
			//System.err.println("type: " + idtype + " " + currident.getName());
			if(idtype.equals("Secondary AC")) {
				addEntryFieldValue(EntryField.ALTERNATIVE_ACS, currident.getName());
			}
			else if (idtype.equals("IMAGE") || idtype.equals("FLJ") || idtype.equals("MGC") || idtype.equals("DKFZ") || idtype.equals("Others")) {
				addEntryFieldValue(EntryField.CLONE_NAME, currident.getName());
			} else if (idtype.equals("Illumina") || idtype.equals("Affymetrix")){
				addEntryFieldValue(EntryField.MICROARRAY_PROBE, currident.getName());
			}
			else if (idtype.equals("Entry name")) {
				addEntryFieldValue(EntryField.UNIPROT_NAME, currident.getName());
			}
		}
		
	}

	@Override
	public Collection<EntryField> getSupportedFields() {
		return Arrays.asList(EntryField.ALTERNATIVE_ACS, EntryField.CLONE_NAME, EntryField.MICROARRAY_PROBE, EntryField.UNIPROT_NAME);
	}

}
