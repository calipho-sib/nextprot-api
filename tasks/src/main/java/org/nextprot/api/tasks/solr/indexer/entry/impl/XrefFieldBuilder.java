package org.nextprot.api.tasks.solr.indexer.entry.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.nextprot.api.core.domain.DbXref;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.Publication;
import org.nextprot.api.solr.index.EntryIndex.Fields;
import org.nextprot.api.tasks.solr.indexer.entry.EntryFieldBuilder;
import org.nextprot.api.tasks.solr.indexer.entry.FieldBuilder;

@EntryFieldBuilder
public class XrefFieldBuilder extends FieldBuilder {

	@Override
	protected void init(Entry entry) {

		String id = entry.getUniqueName();

		// Xrefs
		List<DbXref> xrefs = entry.getXrefs();
		for (DbXref xref : xrefs) {
			String acc = xref.getAccession();
			String db = xref.getDatabaseName();

			// wrong for nextprot gene designation -> protein name
			if ((db.equals("UniProt") || db.equals("neXtProt")) && !id.contains(acc)) {
				String gen = xref.getPropertyValue("gene designation");
				if (gen != null && gen != "-") {
					gen = gen.toUpperCase();
					addField(Fields.INTERACTIONS, gen);
				}
			}

			if (db.equals("HPA") && !acc.contains("ENSG")) {
				addField(Fields.ANTIBODY, acc);
			} else if (db.equals("PeptideAtlas") || db.equals("SRMAtlas")) {
				addField(Fields.PEPTIDE, acc + ", " + db + ":" + acc);
			} else if (db.equals("Ensembl")) {
				addField(Fields.ENSEMBL, acc);
			} else
				addField(Fields.XREFS, acc + ", " + db + ":" + acc);

		}

		for (Publication currpubli : entry.getPublications()) {
			Set<DbXref> pubxrefs = currpubli.getDbXrefs();
			for (DbXref pubxref : pubxrefs) {
				String acc = pubxref.getAccession();
				String db = pubxref.getDatabaseName();
				addField(Fields.XREFS, acc + ", " + db + ":" + acc);
			}
		}

	}

	@Override
	public Collection<Fields> getSupportedFields() {
		return Arrays.asList(Fields.XREFS);
	}

}
