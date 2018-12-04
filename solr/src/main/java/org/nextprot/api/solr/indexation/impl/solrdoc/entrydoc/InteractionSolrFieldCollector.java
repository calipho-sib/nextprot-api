package org.nextprot.api.solr.indexation.impl.solrdoc.entrydoc;

import org.nextprot.api.core.domain.Interactant;
import org.nextprot.api.core.domain.Interaction;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.service.AnnotationService;
import org.nextprot.api.core.service.InteractionService;
import org.nextprot.api.core.service.OverviewService;
import org.nextprot.api.solr.core.impl.schema.EntrySolrField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;


@Service
public class InteractionSolrFieldCollector extends EntrySolrFieldCollector {

	@Autowired
	private InteractionService interactionService;

	@Autowired
	private AnnotationService annotationService;

	@Autowired
	private OverviewService overviewService;

	@Override
	public void collect(Map<EntrySolrField, Object> fields, String entryAccession, boolean gold){

		//WAIT FOR BIO OBJECTS !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		//String id = entry.getUniqueName();
		
		String recName = "";
		String interactantAC = "";
		List<Interaction> interactions = interactionService.findInteractionsByEntry(entryAccession);
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
				      recName = overviewService.findOverviewByEntry(interactantAC).getMainProteinName();
					}
					else // Xeno interaction
					  recName = "";
					if(!gold || currinteraction.getQuality().equals("GOLD"))
				      addEntrySolrFieldValue(fields, EntrySolrField.INTERACTIONS,"AC: " + interactantAC + " gene: " + currinteractant.getGenename() + " name: " + recName + " refs: " + currinteraction.getEvidenceXrefAC());
				}
				else if(currinteraction.isSelfInteraction() == true)
					if(!gold || currinteraction.getQuality().equals("GOLD"))
					   addEntrySolrFieldValue(fields, EntrySolrField.INTERACTIONS,"selfInteraction");
			}
		}
		
		List<Annotation> annots = annotationService.findAnnotations(entryAccession);
		for (Annotation currannot : annots)
			if(currannot.getCategory().equals("subunit")) // Always GOLD
				addEntrySolrFieldValue(fields, EntrySolrField.INTERACTIONS, currannot.getDescription());
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
	public Collection<EntrySolrField> getCollectedFields() {
		return Arrays.asList(EntrySolrField.INTERACTIONS);
	}
}
