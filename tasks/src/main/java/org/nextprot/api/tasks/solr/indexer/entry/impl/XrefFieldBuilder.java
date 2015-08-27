package org.nextprot.api.tasks.solr.indexer.entry.impl;

import java.util.Collection;

import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.solr.index.EntryIndex.Fields;
import org.nextprot.api.tasks.solr.indexer.entry.EntryFieldBuilder;
import org.nextprot.api.tasks.solr.indexer.entry.FieldBuilder;


@EntryFieldBuilder
public class XrefFieldBuilder extends FieldBuilder{
	
	@Override
	protected void init(Entry entry){
		
		/*
		// Xrefs
		List<DbXref> xrefs = entry.getXrefs();
		for (DbXref xref : xrefs) {
			String acc =  xref.getAccession();
			String db = xref.getDatabaseName();
			//System.err.println(db+":"+acc);
			//if(db.equals("IntAct")) System.err.println("id " +  xref.getDbXrefId() + ": " +  xref.getPropertyValue("gene designation")); 
			//if(db.equals("neXtProt")) {
			//	if(acc.equals(id)) continue; // Internal stuff like NX_VG_10_51732257_248
			//	String gen = xref.getPropertyValue("gene designation");
			//	System.err.println("nonxeno: " + gen);
			//}
			if((db.equals("UniProt") || db.equals("neXtProt")) && !id.contains(acc)) { // wrong for nextprot gene designation -> protein name
				String gen = xref.getPropertyValue("gene designation");
				if(gen != null && gen != "-") { gen = gen.toUpperCase(); System.err.println(acc + ": " + gen); doc.addField("interactions", gen);}
				//else System.err.println("no gene for: " + acc );
				} 
			if(db.equals("HPA") && !acc.contains("ENSG")) doc.addField("antibody", acc);
			else if(db.equals("PeptideAtlas") || db.equals("SRMAtlas")) doc.addField("peptide", acc + ", " + db + ":" + acc);
			else if(db.equals("Ensembl")) doc.addField("ensembl", acc);
			else doc.addField("xrefs", acc + ", " + db + ":" + acc);
		}
		*/
				
	}
	
	

	@Override
	public Collection<Fields> getSupportedFields() {
		return null;
	}
	


}
