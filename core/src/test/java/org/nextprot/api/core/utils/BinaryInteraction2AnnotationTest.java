package org.nextprot.api.core.utils;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.core.domain.BioObject;
import org.nextprot.api.core.domain.Interactant;
import org.nextprot.api.core.domain.MainNames;
import org.nextprot.api.core.service.MainNamesService;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class BinaryInteraction2AnnotationTest {


	// mock for MainNamesService (not standard, I know, sorry)
	private class MyMainNamesService implements MainNamesService {

		private Map<String, MainNames> map;
		
		public MyMainNamesService() {
			map = new HashMap<>();
			MainNames names1 = new MainNames();
			names1.setAccession("NX_P61626");
			names1.setGeneNameList(Arrays.asList(new String[]{"fakegene1"}));
			names1.setName("fakename1");
			map.put("NX_P61626", names1);
			MainNames names2 = new MainNames();
			names2.setAccession("NX_P61626-1");
			names2.setGeneNameList(Arrays.asList(new String[]{"fakegene1"}));
			names2.setName("fakename1-1");
			map.put("NX_P61626-1", names2);
			MainNames names3 = new MainNames();
			names3.setAccession("Q81LD0");
			names3.setGeneNameList(Arrays.asList(new String[]{"fakegene2"}));
			names3.setName("fakename2");
			map.put("Q81LD0", names3);
		}
		
		@Override
		public Map<String, MainNames> findIsoformOrEntryMainName() {
			return map;
		}
		
	}

	MainNamesService mainNamesService = new MyMainNamesService();
	
	
	
    @Test
    public void testConvertEvidenceToBioEntry() throws BinaryInteraction2Annotation.MissingInteractantEntryException {
    	
    	

        Interactant interactant = new Interactant();

        interactant.setAccession("NX_P61626");
        interactant.setDatabase("nextProt");
        interactant.setNextprot(true);
        interactant.setXrefId(123L);

        BioObject bo = BinaryInteraction2Annotation.newBioObject(interactant,mainNamesService);
        Assert.assertEquals("NX_P61626", bo.getAccession());
        Assert.assertEquals("neXtProt", bo.getDatabase());
        Assert.assertEquals(123L, bo.getId());
        Assert.assertEquals(BioObject.BioType.PROTEIN, bo.getBioType());
    }

    @Test
    public void testConvertEvidenceToBioIsoform() throws BinaryInteraction2Annotation.MissingInteractantEntryException {

        Interactant interactant = new Interactant();

        interactant.setAccession("NX_P61626-1");
        interactant.setDatabase("nextProt");
        interactant.setNextprot(true);
        interactant.setXrefId(123L);

        BioObject bo = BinaryInteraction2Annotation.newBioObject(interactant,mainNamesService);
        Assert.assertEquals("NX_P61626-1", bo.getAccession());
        Assert.assertEquals("neXtProt", bo.getDatabase());
        Assert.assertEquals(123L, bo.getId());
        Assert.assertEquals(BioObject.BioType.PROTEIN_ISOFORM, bo.getBioType());
    }

    @Test
    public void testConvertEvidenceToExternalBioEntry() throws BinaryInteraction2Annotation.MissingInteractantEntryException {

        Interactant interactant = new Interactant();

        interactant.setAccession("Q81LD0");
        interactant.setDatabase("UniProt");
        interactant.setNextprot(false);
        interactant.setXrefId(15642964L);

        BioObject bo = BinaryInteraction2Annotation.newBioObject(interactant,mainNamesService);
        Assert.assertEquals("Q81LD0", bo.getAccession());
        Assert.assertEquals("UniProt", bo.getDatabase());
        Assert.assertEquals(15642964L, bo.getId());
        Assert.assertEquals(BioObject.BioType.PROTEIN, bo.getBioType());
    }
}