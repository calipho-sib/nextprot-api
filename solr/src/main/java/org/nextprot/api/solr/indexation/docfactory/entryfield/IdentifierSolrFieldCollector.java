package org.nextprot.api.solr.indexation.docfactory.entryfield;

import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.Identifier;
import org.nextprot.api.solr.core.schema.EntrySolrField;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Service
public class IdentifierSolrFieldCollector extends EntrySolrFieldCollector {

	@Override
	public void collect(Entry entry, boolean gold) {
		
		// Identifiers
		List <Identifier> identifiers = entry.getIdentifiers();
		for (Identifier currident : identifiers) {
			String idtype = currident.getType();
			//if(currident.getDatabase() == null)
			//System.err.println("type: " + idtype + " " + currident.getName());
			if(idtype.equals("Secondary AC")) {
				addEntrySolrFieldValue(EntrySolrField.ALTERNATIVE_ACS, currident.getName());
			}
			else if (idtype.equals("IMAGE") || idtype.equals("FLJ") || idtype.equals("MGC") || idtype.equals("DKFZ") || idtype.equals("Others")) {
				addEntrySolrFieldValue(EntrySolrField.CLONE_NAME, currident.getName());
			} else if (idtype.equals("Illumina") || idtype.equals("Affymetrix")){
				addEntrySolrFieldValue(EntrySolrField.MICROARRAY_PROBE, currident.getName());
			}
			else if (idtype.equals("Entry name")) {
				addEntrySolrFieldValue(EntrySolrField.UNIPROT_NAME, currident.getName());
			}
		}
		
	}

	@Override
	public Collection<EntrySolrField> getCollectedFields() {
		return Arrays.asList(EntrySolrField.ALTERNATIVE_ACS, EntrySolrField.CLONE_NAME, EntrySolrField.MICROARRAY_PROBE, EntrySolrField.UNIPROT_NAME);
	}

}
