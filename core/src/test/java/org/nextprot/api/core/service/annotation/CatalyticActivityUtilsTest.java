package org.nextprot.api.core.service.annotation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.core.domain.BioObject.BioType;
import org.nextprot.api.core.domain.BioObject.ResourceType;
import org.nextprot.api.core.domain.DbXref;
import org.nextprot.api.core.domain.Isoform;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.domain.annotation.AnnotationEvidence;
import org.nextprot.api.core.domain.annotation.AnnotationProperty;
import org.nextprot.api.core.service.AnnotationService;
import org.nextprot.api.core.service.EntryBuilderService;
import org.nextprot.api.core.test.base.CoreUnitBaseTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

//@ActiveProfiles({ "dev","cache"})
public class CatalyticActivityUtilsTest extends CoreUnitBaseTest {

	@Autowired
	private EntryBuilderService entryBuilderService;
	@Autowired
	private AnnotationService annotationService;


    @Test
    public void shouldMergeSomeSmiAnnotations()  {
    	String entryName = "NX_12345";
    	List<Isoform> isoforms = new ArrayList<>();
    	isoforms.add(newIsoform(entryName, "1"));
    	List<String>participantPropValues  = Arrays.asList(new String[] {"chemical1 [CHEBI:00001]","chemical2 [CHEBI:00002]", "chemical3 [CHEBI:00003]"});
    	List<String>participantPropValues2 = Arrays.asList(new String[] {"chemical3 [CHEBI:00003]","chemical4 [CHEBI:00004]", "chemical5 [CHEBI:00005]"});
    	String reactionPropValue  = "[RHEA:80001]";
    	String reactionPropValue2 = "[RHEA:80002]";
    	List<DbXref> xrefs = new ArrayList<>();
    	xrefs.add(newXref(1001L, "SomeDb:12345", 	"SomeDb", 	null, null));
    	xrefs.add(newXref(1002L, "CHEBI:00001", 	"ChEBI", 	"name", "chemical1"));
    	xrefs.add(newXref(1003L, "CHEBI:00002", 	"ChEBI", 	"name", "chemical2"));
    	xrefs.add(newXref(1004L, "CHEBI:00003", 	"ChEBI", 	"name", "chemical3"));
    	xrefs.add(newXref(1005L, "RHEA:80001",		"Rhea", 	null, null));
    	xrefs.add(newXref(1006L, "RHEA:80002",		"Rhea", 	null, null));
    	xrefs.add(newXref(1007L, "CHEBI:00004", 	"ChEBI", 	"name", "chemical4"));
    	xrefs.add(newXref(1008L, "CHEBI:00005", 	"ChEBI", 	"name", "chemical5"));

    	// Catalytic annot with 3 participants: CHEBI:00001,2,3
    	Annotation catalyticAnnot1 = newCatalyticAnnot(7000L, isoforms, participantPropValues, reactionPropValue);

    	// Catalytic annot with 3 participants: CHEBI:00003,4,5
    	Annotation catalyticAnnot2 = newCatalyticAnnot(7001L, isoforms, participantPropValues2, reactionPropValue2);
    	
    	// we expect 3 SMI annotations (for CHEBI:00001,2,3)
    	List<Annotation> allSMIs = new ArrayList<>();
    	allSMIs.addAll(CatalyticActivityUtils.createSMIAnnotations(entryName, isoforms, catalyticAnnot1, xrefs));
    	assertEquals(3, allSMIs.size());
    	
    	// we expect 3 more SMI annotations (for CHEBI:00003,4,5)
    	allSMIs.addAll(CatalyticActivityUtils.createSMIAnnotations(entryName, isoforms, catalyticAnnot2, xrefs));
    	assertEquals(6, allSMIs.size());
    	
    	// now run the merge process
    	List<Annotation> mergedSMIs = CatalyticActivityUtils.mergeSmiAnnotations(allSMIs);
    	assertEquals(5, mergedSMIs.size());
    	
    	for (Annotation a: mergedSMIs) {
    		if (a.getBioObject().getAccession().equals("CHEBI:00003")) {
    			// Annotation a should be the result of a merge and 
    			// thus have 2 evidences
    			assertEquals(2, a.getEvidences().size());  
    			for (AnnotationEvidence evi: a.getEvidences()) {
    				assertEquals(a.getAnnotationId(),evi.getAnnotationId());
    			}
    		} else {
    			// annotations not merged with the single original evidence
    			assertEquals(1, a.getEvidences().size());
    		}
    	}
    	
    }
	
	
    @Test
    public void shouldCreate3SMIAnnotations()  {
    	
    	String entryName = "NX_12345";
    	List<Isoform> isoforms = new ArrayList<>();
    	isoforms.add(newIsoform(entryName, "1"));
    	List<String>participantPropValues = Arrays.asList(new String[] {"chemical1 [CHEBI:00001]","chemical2 [CHEBI:00002]", "chemical3 [CHEBI:00003]"});
    	String reactionPropValue = "[RHEA:80001]";
    	List<DbXref> xrefs = new ArrayList<>();
    	xrefs.add(newXref(1001L, "SomeDb:12345", 	"SomeDb", 	null, null));
    	xrefs.add(newXref(1002L, "CHEBI:00001", 	"ChEBI", 	"name", "chemical1"));
    	xrefs.add(newXref(1003L, "CHEBI:00002", 	"ChEBI", 	"name", "chemical2"));
    	xrefs.add(newXref(1004L, "CHEBI:00003", 	"ChEBI", 	"name", "chemical3"));
    	xrefs.add(newXref(1005L, "RHEA:80001",		"Rhea", 	null, null));

    	Annotation catalyticAnnot = newCatalyticAnnot(7000L, isoforms, participantPropValues, reactionPropValue);

    	// check we generate 3 SMI annotations (1 per ChEBI)
    	List<Annotation> smiAnnotations = CatalyticActivityUtils.createSMIAnnotations("NX_12345",isoforms, catalyticAnnot, xrefs);
    	assertEquals(3, smiAnnotations.size());
    	
    	// now check content of fields of one generated SMI annotation
    	Annotation a = smiAnnotations.get(0);
    	assertTrue(a.getAnnotationId()>0);
    	assertEquals(AnnotationCategory.SMALL_MOLECULE_INTERACTION, a.getAPICategory());
    	assertEquals("GOLD", a.getQualityQualifier());
    	assertTrue(a.getUniqueName().startsWith("AN_"));
    	assertEquals(1, a.getTargetingIsoformsMap().size());
    	assertEquals("CHEBI:00001",a.getBioObject().getAccession());
    	assertEquals(BioType.CHEMICAL,a.getBioObject().getBioType());
    	assertEquals("ChEBI",a.getBioObject().getDatabase());
    	assertEquals(1002L,a.getBioObject().getId());
    	assertEquals(ResourceType.EXTERNAL,a.getBioObject().getResourceType());
    	assertEquals("chemical1",a.getBioObject().getPropertyValue("chemical name"));

    	// check the single evidence we expect to find
    	assertEquals(1, a.getEvidences().size());
    	
    	AnnotationEvidence evi = a.getEvidences().get(0);
    	assertEquals(a.getAnnotationId(), evi.getAnnotationId());
    	assertEquals("Uniprot", evi.getAssignedBy());
    	assertEquals("ECO:0000364", evi.getEvidenceCodeAC());
    	assertEquals("evidence based on logical inference from manual annotation used in automatic assertion", evi.getEvidenceCodeName());
    	assertTrue(evi.getEvidenceId()>0);
    	assertEquals("RHEA:80001", evi.getResourceAccession());
    	assertEquals("Rhea", evi.getResourceDb());
    	assertEquals(1005L, evi.getResourceId());
    	assertEquals("database", evi.getResourceType());
    	assertEquals("evidence", evi.getResourceAssociationType());
    
    }
	
    @Test
    public void shouldFindXrefFromPropertyValue()  {
    	
    	List<DbXref> xrefs = new ArrayList<>();
    	xrefs.add(newXref(1001L, "SomeDb:12345", 	"SomeDb", 	null, null));
    	xrefs.add(newXref(1002L, "CHEBI:456216", 	"ChEBI", 	"name", "ADP(3-)"));
    	xrefs.add(newXref(1003L, "RHEA:46608",		"Rhea", 	null, null));
    	xrefs.add(newXref(1004L, "CHEBI:138444", 	"ChEBI", 	"name", null));
    	
    	DbXref x2 = CatalyticActivityUtils.findXrefFromProperty("participant", "ADP(3-) [CHEBI:456216]", xrefs);
    	assertEquals("CHEBI:456216", x2.getAccession());
    	assertEquals("ChEBI", x2.getDatabaseName());
    	assertEquals(new Long(1002L), x2.getDbXrefId());

    	x2 = CatalyticActivityUtils.findXrefFromProperty("reaction", "[RHEA:46608]", xrefs);
    	assertEquals("RHEA:46608", x2.getAccession());
    	assertEquals("Rhea", x2.getDatabaseName());
    	assertEquals(new Long(1003L), x2.getDbXrefId());
    	
		x2 = CatalyticActivityUtils.findXrefFromProperty("reaction", "[RHEA:11111111]", xrefs);
		assertEquals(null,x2);

		try {
    		x2 = CatalyticActivityUtils.findXrefFromProperty("some other property name", "[RHEA:46608]", xrefs);
    		assertTrue(false);
    	} catch (Exception e) {
    		assertTrue(e.getMessage().startsWith("Unexpected catalytic activity property name"));
    	}
    	
		String value = "O(3)-(beta-D-GlcA-(1->3)-poly[beta-D-GalNAc-(1->4)-beta-D-GlcA-(1->3)]-beta-D-GalNAc-(1->4)-beta-D-GlcA-(1->3)-beta-D-Gal-(1->3)-beta-D-Gal-(1->4)-beta-D-Xyl)-L-serine polyanionic residue [CHEBI:138444]";
		x2 = CatalyticActivityUtils.findXrefFromProperty("participant", value, xrefs);
    	assertEquals("CHEBI:138444", x2.getAccession());
    	assertEquals("ChEBI", x2.getDatabaseName());
    	assertEquals(new Long(1004L), x2.getDbXrefId());
    	
    }

    
    private AnnotationProperty newAnnotProp(long annotId, String name, String value) {
    	AnnotationProperty prop = new AnnotationProperty();
    	prop.setAnnotationId(annotId);
    	prop.setName(name);
    	prop.setValue(value);
    	return prop;
    };
    
    private Annotation newCatalyticAnnot(long id, List<Isoform> isoforms, List<String> participantPropValues, String reactionPropValue) {
    	Annotation a = new Annotation();
    	a.setAnnotationId(id);
    	a.setQualityQualifier("GOLD");
    	a.setAnnotationCategory(AnnotationCategory.CATALYTIC_ACTIVITY);
    	a.setDescription("2 H2O + 1 prot = prot-O- + H3O+");
		a.addTargetingIsoforms(AnnotationUtils.newNonPositionalAnnotationIsoformSpecificityList(isoforms, a));
    	a.addProperty(newAnnotProp(id, "reaction", reactionPropValue));
    	for (String value: participantPropValues) a.addProperty(newAnnotProp(id, "participant", value));
    	
    	return a;
    }
    
    private Isoform newIsoform(String entryName, String no) {
    	Isoform iso = new Isoform();
    	iso.setIsoformAccession(entryName + "-" + no);
    	iso.setSwissProtDisplayedIsoform("1".equals(no));
    	return iso;
    }
    
    private DbXref newXref(long id, String ac, String db, String propName, String propValue) {
    	DbXref x = new DbXref();
    	x.setAccession(ac);
    	x.setDatabaseName(db);
    	x.setDbXrefId(id);
    	if (propName !=null) x.addProperty(propName, propValue, id+1000L);
    	x.setDatabaseCategory("someCat");
    	x.setLinkUrl("someUrl");
    	x.setLinkUrl("someLinkUrl");
    	return x;
    }
    
}
