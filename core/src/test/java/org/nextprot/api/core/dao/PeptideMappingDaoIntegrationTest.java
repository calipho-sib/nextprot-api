package org.nextprot.api.core.dao;

import org.junit.Ignore;
import org.junit.Test;
import org.nextprot.api.commons.constants.IdentifierOffset;
import org.nextprot.api.core.domain.annotation.AnnotationEvidence;
import org.nextprot.api.core.service.MasterIdentifierService;
import org.nextprot.api.core.test.base.CoreUnitBaseTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static org.junit.Assert.assertTrue;

@ActiveProfiles({ "dev" })
public class PeptideMappingDaoIntegrationTest extends CoreUnitBaseTest {

	@Autowired private PeptideMappingDao peptideMappingDao;
	@Autowired private MasterIdentifierService masterIdentifierService;

	
	@Test
	public void shouldReturn_Some_Expected_Mappings() {
		String entryName = "NX_Q9UGM3";
		Long masterId = this.masterIdentifierService.findIdByUniqueName(entryName);
 		List<Map<String,Object>> mappings = peptideMappingDao.findPeptideMappingAnnotationsByMasterId(masterId, true, false);
		Map<String,Integer> annotPepIsoRecCount = new TreeMap<>();
 		
 		for (Map<String,Object> mapping: mappings) {
 			Long annotationId = (Long) mapping.get(PeptideMappingDao.KEY_ANNOTATION_ID);
 			String qual = (String) mapping.get(PeptideMappingDao.KEY_QUALITY_QUALIFIER);
 			String iso = (String) mapping.get(PeptideMappingDao.KEY_ISO_UNIQUE_NAME);
 			String pep = (String) mapping.get(PeptideMappingDao.KEY_PEP_UNIQUE_NAME);
 			Integer p1 = (Integer) mapping.get(PeptideMappingDao.KEY_FIRST_POS);
 			Integer p2 = (Integer) mapping.get(PeptideMappingDao.KEY_LAST_POS);
 			Integer rank = (Integer) mapping.get(PeptideMappingDao.KEY_RANK);
 			
 			// basic check of the data returned by the DAO 
 			
 			assertTrue(annotationId > IdentifierOffset.PEPTIDE_MAPPING_ANNOTATION_OFFSET  );
 			assertTrue(qual.equals("GOLD") || qual.equals("SILVER"));
 			assertTrue(iso.startsWith(entryName));
 			assertTrue(pep.startsWith("NX_"));
 			assertTrue(p1>0);
 			assertTrue(p2>p1);
 			assertTrue(rank>0);
 			
 			// check that we NEVER have more than 1 mapping record for each peptide/isoform/annotation key
 			
			String key = "" + annotationId + "/" + pep + "/"+ iso;
			if (!annotPepIsoRecCount.containsKey(key)) annotPepIsoRecCount.put(key, new Integer(0));
			annotPepIsoRecCount.put(key, new Integer(annotPepIsoRecCount.get(key) + 1));
 		}
 		assertTrue(annotPepIsoRecCount.size()>0);
		for (Map.Entry<String, Integer> data: annotPepIsoRecCount.entrySet()) {
			//System.out.println(data.getKey() + " => " + data.getValue());
			assertTrue(data.getValue()==1);
		}
 	}	

 	@Ignore
    // TODO: check with pam
	@Test
	public void shouldReturn_Some_Expected_Evidences() {
		List<String> names = new ArrayList<>();
		String pepName1 = "NX_PEPT01967984";
		names.add(pepName1);
		Map<String,List<AnnotationEvidence>> evMap = this.peptideMappingDao.findPeptideAnnotationEvidencesMap(names, true);
		assertTrue(evMap.size()==1);                        // 1 peptide name key excpected
		assertTrue(evMap.get(pepName1).size()==1); 			// 1 PeptideAtlas evidence expected
		AnnotationEvidence ev = evMap.get(pepName1).get(0);
		assertTrue(ev.getAnnotationId()==0L);               // setup later by service !
		assertTrue(ev.getEvidenceId()> IdentifierOffset.PEPTIDE_MAPPING_ANNOTATION_EVIDENCE_OFFSET);
		assertTrue(ev.getAssignedBy().startsWith("PeptideAtlas"));
		assertTrue(ev.getEvidenceCodeAC().startsWith("ECO"));
		assertTrue(ev.getEvidenceCodeName().length()>0);
		assertTrue(ev.getQualityQualifier().length()>0);
		assertTrue(ev.getResourceAccession().equals("PAp05117553"));
		assertTrue(ev.getResourceType().equals("database"));
		
	}
	
	
}
