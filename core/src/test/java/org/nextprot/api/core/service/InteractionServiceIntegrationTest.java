package org.nextprot.api.core.service;

import org.junit.Ignore;
import org.junit.Test;
import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.core.domain.BioObject;
import org.nextprot.api.core.domain.Interaction;
import org.nextprot.api.core.domain.Isoform;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.domain.annotation.AnnotationEvidence;
import org.nextprot.api.core.domain.annotation.AnnotationIsoformSpecificity;
import org.nextprot.api.core.test.base.CoreUnitBaseTest;
import org.nextprot.api.core.utils.BinaryInteraction2Annotation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

@ActiveProfiles({ "dev" })
public class InteractionServiceIntegrationTest extends CoreUnitBaseTest {

	@Autowired private InteractionService interactionService;
	@Autowired private IsoformService isoformService;
	@Autowired private MainNamesService mainNamesService;


	
	@Ignore
	@Test
	public void shouldWork() throws BinaryInteraction2Annotation.MissingInteractantEntryException {
		String entryName = "NX_P38398";
		List<Annotation> annots = new ArrayList<>();
		List<Isoform> isoforms = this.isoformService.findIsoformsByEntryName(entryName);
		List<Interaction> interactions = this.interactionService.findInteractionsByEntry(entryName);
		for (Interaction inter : interactions) {
		    
			
			Annotation annot = BinaryInteraction2Annotation.transform(inter, entryName, isoforms, mainNamesService);
			annots.add(annot);
			
			BioObject bo = annot.getBioObject();
			if (bo!=null && (bo.getAccession().equals("NX_Q92560") || bo.getAccession().equals("Q99PU7")) )  {
				System.out.print(inter.getEvidenceXrefAC() + ": ");
				System.out.print (inter.getInteractants().get(0).getAccession());
				if (inter.getInteractants().size()==2) System.out.print( " <==> " + inter.getInteractants().get(1));
			}
		}
	}

/*
 * This queries retrieves entries with their 
 * - count of xeno interactions
 * - count of self interactions
 * - count of interactions with another protein entry defined in nextprot
 * - count of interactions whih another protein isoform defined in nextprot
 * - count of isoform specific interactions 
 * 
 *  and can be used to find test examples
 *  
select entry_ac, sum(is_iso_spec) as iso_spec_interactions, sum(has_xeno) as with_xeno, sum(has_self) as with_self, sum(has_iso) as with_isos, 
sum(has_entry) as entries, sum(has_xeno)+ sum(has_self)+ sum(has_iso)+ sum(has_entry) as interaction_count from (
select xr1.accession, 
(regexp_split_to_array(xr1.accession,'-'))[1]  as entry_ac,
case when xr1.accession like '%-%' then 1 else 0 end as is_iso_spec,
case when inter.is_xeno then 1 else 0 end as has_xeno,
case when interactant1.is_self_interaction then 1 else 0 end as has_self,
case when inter.is_xeno is false and interactant1.is_self_interaction is false and xr2.accession ilike '%-%' then 1 else 0 end as has_iso,
case when inter.is_xeno is false and interactant1.is_self_interaction is false and xr2.accession not ilike '%-%' then 1 else 0 end as has_entry
from partnerships inter 
inner join partnership_partner_assoc interactant1 on (inter.partnership_id=interactant1.partnership_id)
inner join db_xrefs xr1 on (interactant1.db_xref_id=xr1.resource_id)
left outer join partnership_partner_assoc interactant2 on (inter.partnership_id=interactant2.partnership_id and interactant1.assoc_id != interactant2.assoc_id or interactant2.assoc_id is null)
left outer join db_xrefs xr2 on (interactant2.db_xref_id=xr2.resource_id)
) a 
group by entry_ac
having sum(has_xeno)>0 and sum(has_self)>0 and sum(has_iso)>0 and sum(has_entry)>0
order by sum(has_xeno)+ sum(has_self)+ sum(has_iso)+ sum(has_entry) 
*/
	
	/*
	 * NX_Q9UNQ0 should contain at least 1 interactions of each type:
	 * - self interaction 
	 * - xeno interaction (interaction with a protein not defined in nextprot, see resourceinternalrefs
	 * - interaction with another nextprot entry
	 * - interaction with another nextprot specific isoform
	 * and there should at least 1 interaction declared as isoform specific
	 * 
	 * see query above to find other examples if necessary in future releases
	 * 
	 * 
	 * Pam 07/10/2020:
	 * We are now using NP2 pipeline to get IntAct (and other) binary interactions.
	 * This test should work again once we have the nxflat db filled with IntAct data
	 * 
	 */
	@Test
	public void shouldDealWithAnyInteractionSpecialInteraction() {
		String entry_ac="NX_Q9UNQ0";
		List<Annotation> annots = this.interactionService.findInteractionsAsAnnotationsByEntry(entry_ac);
		int numberOfExperiments = 0;
		int withNxEntries = 0;
		int withNxIsos = 0;
		int withSelf = 0;
		int withXrefs = 0;
		int isoSpecs = 0;
		
		for (Annotation annot: annots) {
			
			/*
			System.out.println("partner " + annot.getBioObject().getAccession() +
					" " + annot.getBioObject().getBioType() + " / " + annot.getBioObject().getResourceType());
			*/

			// basic checks
			assertTrue(annot.getCategory().equals("BinaryInteraction"));
			assertTrue(annot.getAPICategory() == AnnotationCategory.BINARY_INTERACTION);
			
			// partners 
			if (isAnnotationASelfInteraction(annot, entry_ac)) withSelf ++;
			if (isAnnotationAnInteractionWithAnExternalXrefAsPartner(annot)) withXrefs++;
			if (isAnnotationAnInteractionWithaNextprotEntryAsPartner(annot, entry_ac)) withNxEntries++;
			if (isAnnotationAnInteractionWithANextprotIsoformAsPartner(annot)) withNxIsos++;

			// specificity of annotation subject
			if (isAnnotationAnInteractionWithANextprotIsoformAsSubject(annot)) isoSpecs++;
			
			// evidences
			assertTrue(annot.getEvidences().size()==1);
			AnnotationEvidence evi = annot.getEvidences().get(0);
			assertTrue(evi.getQualityQualifier().equals("GOLD") || evi.getQualityQualifier().equals("SILVER"));
			assertTrue(evi.getResourceDb().equals("IntAct"));
			if (annot.getEvidences().get(0).getPropertyValue("numberOfExperiments") != null) numberOfExperiments++;
			
		}
		/*
		System.out.println("numberOfExperiments:" + numberOfExperiments);
		System.out.println("withNxEntries:" + withNxEntries);
		System.out.println("withNxIsos:" + withNxIsos);
		System.out.println("withSelf:" + withSelf );
		System.out.println("withXrefs:" + withXrefs );
		System.out.println("isoSpecs:" + isoSpecs );
		*/
		assertTrue(numberOfExperiments==annots.size()); // should exist for each interaction
		assertTrue(withNxEntries >= 1); // 8 cases
		assertTrue(withNxIsos >= 1); // 1 case
		assertTrue(withXrefs >= 1); // 10 cases
		assertTrue(withSelf == 1);  // 1 case
		assertTrue(isoSpecs > 1);  // 16 cases
	}

	private boolean isAnnotationASelfInteraction(Annotation annot, String entry_ac) {
		return annot.getBioObject().getAccession().equals(entry_ac);
	}
	private boolean isAnnotationAnInteractionWithAnExternalXrefAsPartner(Annotation annot) {
		return annot.getBioObject().getResourceType().equals(BioObject.ResourceType.EXTERNAL);
	}
	private boolean isAnnotationAnInteractionWithaNextprotEntryAsPartner(Annotation annot, String entry_ac) {
		return annot.getBioObject().getResourceType().equals(BioObject.ResourceType.INTERNAL) &&
				annot.getBioObject().getBioType().equals(BioObject.BioType.PROTEIN) &&
				! annot.getBioObject().getAccession().equals(entry_ac);
	}
	private boolean isAnnotationAnInteractionWithANextprotIsoformAsPartner(Annotation annot) {
		return annot.getBioObject().getBioType().equals(BioObject.BioType.PROTEIN_ISOFORM);
	}
	private boolean isAnnotationAnInteractionWithANextprotIsoformAsSubject(Annotation annot) {
		for (AnnotationIsoformSpecificity spec : annot.getTargetingIsoformsMap().values()) {
			if (spec.getSpecificity().equals("SPECIFIC")) return true;
		}
		return false;
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
