package org.nextprot.api.tasks.solr.indexer.entry.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.nextprot.api.core.domain.DbXref;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.Publication;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.domain.annotation.AnnotationEvidence;
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
				//System.err.println("AB: " + acc);
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

		// It is weird to have to go thru this to get the CAB antibodies, they should come with getXrefs()
		Set<String> CABSet = new HashSet<String>();
		List<Annotation> annots = entry.getAnnotations();
		for (Annotation currannot : annots) {
			String category = currannot.getCategory();
			if (category.equals("expression info")) {
				List<AnnotationEvidence> evlist = currannot.getEvidences();
				for (AnnotationEvidence evidence : evlist) { 
					String CAB = evidence.getPropertyValue("antibodies acc");
					if(CAB != null && CAB.startsWith("CAB"))
						CABSet.add(CAB);
				}
			}
		}
		if(CABSet.size() > 0) 
		  for (String CAB : CABSet)
			  addField(Fields.ANTIBODY, CAB);

	}

	@Override
	public Collection<Fields> getSupportedFields() {
		return Arrays.asList(Fields.XREFS, Fields.ENSEMBL, Fields.ANTIBODY);
	}

}
