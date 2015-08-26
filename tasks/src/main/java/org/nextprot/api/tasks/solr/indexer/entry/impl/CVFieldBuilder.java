package org.nextprot.api.tasks.solr.indexer.entry.impl;

import java.util.Collection;

import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.solr.index.EntryIndex.Fields;
import org.nextprot.api.tasks.solr.indexer.entry.EntryFieldBuilder;
import org.nextprot.api.tasks.solr.indexer.entry.FieldBuilder;

@EntryFieldBuilder
public class CVFieldBuilder implements FieldBuilder {

	@Override
	public <T> T build(Entry entry, Fields field, Class<T> requiredType) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<Fields> getSupportedFields() {
		// TODO Auto-generated method stub
		return null;
	}

/*	public CVFieldBuilder(Entry entry) {
		init(entry);
	}*/
/*
	private void init(Entry entry) {

		List<Annotation> annots = entry.getAnnotations();
		int cvac_cnt = 0;
		for (Annotation currannot : annots) {
			String category = currannot.getCategory();
			if (category.equals("tissue specificity")) {
				// No duplicates this is a Set
				cv_tissues.add(currannot.getCvTermAccessionCode()); 
				cv_tissues.add(currannot.getCvTermName()); 
			} else {
				String cvac = currannot.getCvTermAccessionCode();
				if (cvac != null) {
					doc.addField("cv_acs", cvac);
					cvac_cnt++;
					cv_acs.add(cvac); // No duplicates: this is a Set, will be
										// used for synonyms and ancestors
					doc.addField("cv_names", currannot.getCvTermName());
				}
			}
		}

	}

	@Override
	public <T> T build(Entry entry, Fields field, Class<T> requiredType) {

		if (field.equals(Fields.CV_ACS))
			return requiredType.cast(chrLoc);
		if (field.equals(Fields.CV_NAMES))
			return requiredType.cast(chrLocS);

		throw new NextProtException("Unsupported type " + field);

	}

	@Override
	public Collection<Fields> getSupportedFields() {
		return Arrays.asList(Fields.CHR_LOC, Fields.CHR_LOC_S, Fields.GENE_BAND);
	}
*/
}
