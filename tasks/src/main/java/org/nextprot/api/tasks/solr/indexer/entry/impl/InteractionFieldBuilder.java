package org.nextprot.api.tasks.solr.indexer.entry.impl;

import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.Interactant;
import org.nextprot.api.core.domain.Interaction;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.service.fluent.EntryConfig;
import org.nextprot.api.solr.index.EntryField;
import org.nextprot.api.tasks.solr.indexer.entry.EntryFieldBuilder;
import org.nextprot.api.tasks.solr.indexer.entry.FieldBuilder;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;


@EntryFieldBuilder
public class InteractionFieldBuilder extends FieldBuilder{
	
	@Override
	protected void init(Entry entry){

		//WAIT FOR BIO OBJECTS !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		//String id = entry.getUniqueName();
		
		String recName = "";
		String interactantAC = "";
		List<Interaction> interactions = entry.getInteractions();
		//System.err.println(interactions.size() + " interactions");
		for (Interaction currinteraction : interactions) {
			//System.err.println(currinteraction.getEvidenceXrefAC()); // EBI-372273,EBI-603319
			List<Interactant> interactants = currinteraction.getInteractants();
			for (Interactant currinteractant : interactants) {
				if(currinteractant.getGenename() != null) { // otherwise it is the entry itself
					interactantAC = currinteractant.getAccession();
					if(currinteractant.isNextprot()) {
					  interactantAC = "NX_" + interactantAC.split("-")[0];	
					  // We need the entryBuilderService to get interactant's recname
				      recName = entryBuilderService.build(EntryConfig.newConfig(interactantAC).withOverview()).getOverview().getMainProteinName();
					}
					else // Xeno interaction
					  recName = "";
					if(!this.isGold() || currinteraction.getQuality().equals("GOLD")) 
				      addField(EntryField.INTERACTIONS,"AC: " + interactantAC + " gene: " + currinteractant.getGenename() + " name: " + recName + " refs: " + currinteraction.getEvidenceXrefAC());
				}
				else if(currinteraction.isSelfInteraction() == true)
					if(!this.isGold() || currinteraction.getQuality().equals("GOLD")) 
					   addField(EntryField.INTERACTIONS,"selfInteraction");
			}
		}
		
		List<Annotation> annots = entry.getAnnotations();
		for (Annotation currannot : annots)
			if(currannot.getCategory().equals("subunit")) // Always GOLD
				addField(EntryField.INTERACTIONS, currannot.getDescription());


		/*
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
		
		//WAIT FOR BIO OBJECTS !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		
		}*/
		
	}
	
	@Override
	public Collection<EntryField> getSupportedFields() {
		return Arrays.asList(EntryField.INTERACTIONS);
	}
	


}
