package org.nextprot.api.core.service;

import org.junit.Test;
import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.commons.constants.PropertyApiModel;
import org.nextprot.api.core.domain.Interaction;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.domain.annotation.AnnotationEvidence;
import org.nextprot.api.core.domain.annotation.AnnotationProperty;
import org.nextprot.api.core.test.base.CoreUnitBaseTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.Assert.assertTrue;

@ActiveProfiles({ "dev" })
public class InteractionServiceIntegrationTest extends CoreUnitBaseTest {

	@Autowired
	private InteractionService interactionService;

	@Test
	public void shouldReturn4BinaryInteractions() {
		List<Interaction> interactions = this.interactionService.findInteractionsByEntry("NX_Q9UNQ0");
		System.out.println("interaction count: " + interactions.size());
		assertTrue(interactions.size() == 4);
	}
	
	
/*
 * This queries retrieves entries with their 
 * - count of xeno interactions
 * - count of self interactions
 * - count of interactions with another protein entry defined in nextprot
 * - count of interactions whicth another protein isoform defined in nextprot
 * 
select accession, sum(has_xeno) as xenos, sum(has_self) as selves, sum(has_iso) as isos, sum(has_entry) as entries from (
select xr1.accession, 
case when inter.is_xeno then 1 else 0 end as has_xeno,
case when interactant1.is_self_interaction then 1 else 0 end as has_self,
case when inter.is_xeno is false and interactant1.is_self_interaction is false and xr2.accession ilike '%-%' then 1 else 0 end as has_iso,
case when inter.is_xeno is false and interactant1.is_self_interaction is false and xr2.accession not ilike '%-%' then 1 else 0 end as has_entry
from partnerships inter 
inner join partnership_partner_assoc interactant1 on (inter.partnership_id=interactant1.partnership_id)
inner join db_xrefs xr1 on (interactant1.db_xref_id=xr1.resource_id)
left outer join partnership_partner_assoc interactant2 on (inter.partnership_id=interactant2.partnership_id and interactant1.assoc_id != interactant2.assoc_id or interactant2.assoc_id is null)
left outer join db_xrefs xr2 on (interactant2.db_xref_id=xr2.resource_id)
--limit 10
) a 
group by accession
having sum(has_xeno)>0 and sum(has_self)>0 and sum(has_iso)>0 and sum(has_entry)>0
order by sum(has_xeno)+ sum(has_self)+ sum(has_iso)+ sum(has_entry) 
*/
	
	/*
	 * NX_Q9UNQ0 should contain 4 interactions: one of each type:
	 * - self interaction 
	 * - xeno interaction (interaction with a protein not defined in nextprot, see resourceinternalrefs
	 * - interaction with another nextprot entry
	 * - interaction with another nextprot specific isoform
	 */
	@Test
	public void shouldReturn4AnnotationsWithProperties() {
		List<Annotation> annots = this.interactionService.findInteractionsAsAnnotationsByEntry("NX_Q9UNQ0");
		assertTrue(annots.size() == 4);
		int numberOfExperiments = 0;
		int entryacs = 0;
		int isoacs = 0;
		int resourceinternalrefs = 0;
		int self=0;
		for (Annotation annot: annots) {
			assertTrue(annot.getCategory().equals("BinaryInteraction"));
			assertTrue(annot.getAPICategory() == AnnotationCategory.BINARY_INTERACTION);
			assertTrue(annot.getEvidences().size()==1);
			AnnotationEvidence evi = annot.getEvidences().get(0);
			evi.getAssignedBy().equals("IntAct");
			if (evi.getResourceAccession().equals("EBI-1569435,EBI-1569435")) self++;
			assertTrue(evi.getQualityQualifier().equals("GOLD") || evi.getQualityQualifier().equals("SILVER"));	
			assertTrue(evi.getResourceAccession().contains("EBI-")  && evi.getResourceAccession().contains("1569435") );
			assertTrue(evi.getResourceDb().equals("IntAct"));
			
			if (annot.getEvidences().get(0).getPropertyValue("numberOfExperiments") != null) numberOfExperiments++;
			
			for (AnnotationProperty prop: annot.getProperties()) {
				//if (prop.getName().equals("numberOfExperiments")) numberOfExperiments++;
				if (prop.getName().equals(PropertyApiModel.NAME_INTERACTANT) && prop.getValueType().equals(PropertyApiModel.VALUE_TYPE_ENTRY_AC)) entryacs++;
				if (prop.getName().equals(PropertyApiModel.NAME_INTERACTANT) && prop.getValueType().equals(PropertyApiModel.VALUE_TYPE_ISO_AC)) isoacs++;
				if (prop.getName().equals(PropertyApiModel.NAME_INTERACTANT) && prop.getValueType().equals(PropertyApiModel.VALUE_TYPE_RIF)) resourceinternalrefs++;				
			}
		}
		assertTrue(numberOfExperiments==4);
		assertTrue(entryacs==0);
		assertTrue(isoacs==0);
		assertTrue(resourceinternalrefs==0);
		assertTrue(self==1);
	}

	
/*
 * This query retrieves isoforms that are annotated as having as specific interaction with 
 * another nextprot entry or isoform
 * isoformWithSpecificInteraction: use the entry of this isoform to get an example of isoform specific interaction
 * interactingEntity: the interaction partner of isoformWithSpecificInteraction
 * 
select xr1.accession as isoformWithSpecificInteraction, xr2.accession as interactingEntity
from partnerships inter 
inner join partnership_partner_assoc interactant1 on (inter.partnership_id=interactant1.partnership_id)
inner join db_xrefs xr1 on (interactant1.db_xref_id=xr1.resource_id)
inner join partnership_partner_assoc interactant2 on (inter.partnership_id=interactant2.partnership_id and interactant1.assoc_id != interactant2.assoc_id or interactant2.assoc_id is null)
inner join db_xrefs xr2 on (interactant2.db_xref_id=xr2.resource_id)
where xr1.accession ilike '%-%'
limit 10
 * 	
 */
	
	/*
	 * NX_Q6ZMQ8 should contain an interaction with P61810 having the specificity SPECIFIC 
	 * Note that other isoform SPECIFIC interactions (as annotations) exist for this entry.
	 */
}
