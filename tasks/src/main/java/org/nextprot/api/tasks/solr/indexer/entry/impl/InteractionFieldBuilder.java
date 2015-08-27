package org.nextprot.api.tasks.solr.indexer.entry.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.nextprot.api.core.domain.DbXref;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.solr.index.EntryIndex.Fields;
import org.nextprot.api.tasks.solr.indexer.entry.EntryFieldBuilder;
import org.nextprot.api.tasks.solr.indexer.entry.FieldBuilder;


@EntryFieldBuilder
public class InteractionFieldBuilder extends FieldBuilder{
	
	@Override
	protected void init(Entry entry){

		String id = entry.getUniqueName();
		
		//Gets interactions using xrefs
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
		}
		
		
		/*
		List<Interaction> interactions = entry.getInteractions();
		//System.err.println(interactions.size() + " interactions");
		for (Interaction currinteraction : interactions) {
			//System.err.println(currinteraction.getEvidenceXrefAC()); // EBI-372273,EBI-603319
			doc.addField("interactions", currinteraction.getEvidenceXrefAC());
			List<Interactant> interactants = currinteraction.getInteractants();
			//System.err.println(interactants.size() + " interactants");
			for (Interactant currinteractant : interactants) {
				//currinteractant.
			     //System.err.println(currinteractant.getNextprotAccession() + " " + currinteractant.getUrl());
			     List<Long> ll = Arrays.asList(currinteractant.getXrefId()); // findDbXRefByIds exists but not findDbXRefById
			     DbXref xref1 = this.dbxrefservice.findDbXRefByIds(ll).get(0);
			     List<DbXrefProperty> xrefprops =  xref1.getProperties();
			     if(xrefprops != null)
			    	for (DbXrefProperty xrefprop : xrefprops) {
			    		 System.err.println("propname: " + xrefprop.getName()); // never shows
			    	 } //else System.err.println("no properties for: " + xref1.getAccession());
			    	 //System.err.println("propval: " + xref1.getAccession());
			    	 //System.err.println("propval: " + xref1.getPropertyValue("gene designation"));
			}
			//doc.addField("interactions", interaction.getAccession());
		}*/
		
				
	}
	
	@Override
	public Collection<Fields> getSupportedFields() {
		return Arrays.asList(Fields.INTERACTIONS);
	}
	


}
