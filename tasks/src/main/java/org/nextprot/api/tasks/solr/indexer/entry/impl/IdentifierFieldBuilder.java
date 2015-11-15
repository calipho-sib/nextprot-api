package org.nextprot.api.tasks.solr.indexer.entry.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.Identifier;
import org.nextprot.api.solr.index.EntryIndex.Fields;
import org.nextprot.api.tasks.solr.indexer.entry.EntryFieldBuilder;
import org.nextprot.api.tasks.solr.indexer.entry.FieldBuilder;

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
				addField(Fields.ALTERNATIVE_ACS, currident.getName());
			}
			else if (idtype.equals("IMAGE") || idtype.equals("FLJ") || idtype.equals("MGC") || idtype.equals("DKFZ") || idtype.equals("Others")) {
				addField(Fields.CLONE_NAME, currident.getName());
			} else if (idtype.equals("Illumina") || idtype.equals("Affymetrix")){
				addField(Fields.MICROARRAY_PROBE, currident.getName());
			}
			else if (idtype.equals("Entry name")) {
				addField(Fields.UNIPROT_NAME, currident.getName());
			}
		}
		
	}

	@Override
	public Collection<Fields> getSupportedFields() {
		return Arrays.asList(Fields.ALTERNATIVE_ACS, Fields.CLONE_NAME, Fields.MICROARRAY_PROBE, Fields.UNIPROT_NAME);
	}

}
