package org.nextprot.api.tasks.solr.indexer.entry.impl;

import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.domain.annotation.AnnotationEvidence;
import org.nextprot.api.core.utils.dbxref.XrefDatabase;
import org.nextprot.api.solr.index.EntryIndex.Fields;
import org.nextprot.api.tasks.solr.indexer.entry.EntryFieldBuilder;
import org.nextprot.api.tasks.solr.indexer.entry.FieldBuilder;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;


@EntryFieldBuilder
public class PeptideFieldBuilder extends FieldBuilder{
	
	@Override
	protected void init(Entry entry){ 
		for (Annotation currannot : entry.getAnnotations()) {
			String category = currannot.getCategory();
			if (category.contains("peptide mapping")){
				List<AnnotationEvidence> evList = currannot.getEvidences();
				for (AnnotationEvidence currEv : evList) {
					String db = currEv.getResourceDb();
					if(!db.equals("neXtProtSubmission")) {
					   if(db.equals(XrefDatabase.PUB_MED.getName())) addField(Fields.PEPTIDE, db + ":" + currEv.getResourceAccession());
					   else addField(Fields.PEPTIDE, db + ":" + currEv.getResourceAccession() + ", " + currEv.getResourceAccession());
					}
				}
			}
		}
	}
	
	@Override
	public Collection<Fields> getSupportedFields() {
		return Arrays.asList(Fields.PEPTIDE);
	}
	
}
