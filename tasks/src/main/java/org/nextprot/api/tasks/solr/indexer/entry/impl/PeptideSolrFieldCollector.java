package org.nextprot.api.tasks.solr.indexer.entry.impl;

import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.domain.annotation.AnnotationEvidence;
import org.nextprot.api.core.service.dbxref.XrefDatabase;
import org.nextprot.api.solr.index.EntrySolrField;
import org.nextprot.api.tasks.solr.indexer.entry.EntrySolrFieldCollector;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;


@Service
public class PeptideSolrFieldCollector extends EntrySolrFieldCollector {
	
	@Override
	public void collect(Entry entry, boolean gold) {
		for (Annotation currannot : entry.getAnnotations()) {
			String category = currannot.getCategory();
			if (category.contains("peptide mapping")){
				List<AnnotationEvidence> evList = currannot.getEvidences();
				for (AnnotationEvidence currEv : evList) {
					String db = currEv.getResourceDb();
					if(!db.equals("neXtProtSubmission")) {
					   if(db.equals(XrefDatabase.PUB_MED.getName())) addEntrySolrFieldValue(EntrySolrField.PEPTIDE, db + ":" + currEv.getResourceAccession());
					   else addEntrySolrFieldValue(EntrySolrField.PEPTIDE, db + ":" + currEv.getResourceAccession() + ", " + currEv.getResourceAccession());
					}
				}
			}
		}
	}
	
	@Override
	public Collection<EntrySolrField> getCollectedFields() {
		return Arrays.asList(EntrySolrField.PEPTIDE);
	}
	
}
