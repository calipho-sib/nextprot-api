package org.nextprot.api.solr.indexation.impl.solrdoc.entrydoc;

import org.nextprot.api.core.domain.Identifier;
import org.nextprot.api.core.service.IdentifierService;
import org.nextprot.api.solr.core.impl.schema.EntrySolrField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Service
public class IdentifierSolrFieldCollector extends EntrySolrFieldCollector {

	@Autowired
	private IdentifierService identifierService;

	@Override
	public void collect(Map<EntrySolrField, Object> fields, String entryAccession, boolean gold) {
		
		// Identifiers
		List<Identifier> identifiers = identifierService.findIdentifiersByMaster(entryAccession);

		for (Identifier currident : identifiers) {
			String idtype = currident.getType();
			//if(currident.getDatabase() == null)
			//System.err.println("type: " + idtype + " " + currident.getName());
			if(idtype.equals("Secondary AC")) {
				addEntrySolrFieldValue(fields, EntrySolrField.ALTERNATIVE_ACS, currident.getName());
			}
			else if (idtype.equals("IMAGE") || idtype.equals("FLJ") || idtype.equals("MGC") || idtype.equals("DKFZ") || idtype.equals("Others")) {
				addEntrySolrFieldValue(fields, EntrySolrField.CLONE_NAME, currident.getName());
			} else if (idtype.equals("Illumina") || idtype.equals("Affymetrix")){
				addEntrySolrFieldValue(fields, EntrySolrField.MICROARRAY_PROBE, currident.getName());
			}
			else if (idtype.equals("Entry name")) {
				addEntrySolrFieldValue(fields, EntrySolrField.UNIPROT_NAME, currident.getName());
			}
		}
	}

	@Override
	public Collection<EntrySolrField> getCollectedFields() {
		return Arrays.asList(EntrySolrField.ALTERNATIVE_ACS, EntrySolrField.CLONE_NAME, EntrySolrField.MICROARRAY_PROBE, EntrySolrField.UNIPROT_NAME);
	}
}
