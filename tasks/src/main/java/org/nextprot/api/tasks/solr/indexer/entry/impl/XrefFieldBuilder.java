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

		// Xrefs
		List<DbXref> xrefs = entry.getXrefs();
		for (DbXref xref : xrefs) {
			String acc = xref.getAccession();
			String db = xref.getDatabaseName();
			if (db.equals("neXtProtSubmission")) continue;
			if (db.equals("HPA") && !acc.contains("ENSG")) { // HPA with ENSG are for expression
				//System.err.println(acc);
				addField(Fields.ANTIBODY, acc);
			}
            if (db.equals("Ensembl")) {
				addField(Fields.ENSEMBL, acc);
			} 
            if (!(db.equals("PeptideAtlas") || db.equals("SRMAtlas"))) { // These are indexed under the 'peptide' field
            	//if(!acc.startsWith("EBI-")) // These are indexed under the 'interaction' field
				   addField(Fields.XREFS,db + ":" + acc + ", " + acc);
				//if(db.equals("IntAct"))
				  //System.err.println(acc);
				  //System.err.println(db + " -> " + xref.getDatabaseCategory());
			}
	
		}

		for (Publication currpubli : entry.getPublications()) {
			Set<DbXref> pubxrefs = currpubli.getDbXrefs();
			for (DbXref pubxref : pubxrefs) {
				String acc = pubxref.getAccession();
				String db = pubxref.getDatabaseName();
				if (!db.equals("neXtProtSubmission")) 
				   addField(Fields.XREFS,db + ":" + acc + ", " + acc);
			}
		}

	}

	@Override
	public Collection<Fields> getSupportedFields() {
		return Arrays.asList(Fields.XREFS, Fields.ENSEMBL, Fields.ANTIBODY);
	}

}
