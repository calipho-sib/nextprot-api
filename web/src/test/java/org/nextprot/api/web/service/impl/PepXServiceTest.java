package org.nextprot.api.web.service.impl;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import org.junit.Test;
import org.mockito.Mockito;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.commons.utils.Pair;
import org.nextprot.api.core.domain.Isoform;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.web.dbunit.base.mvc.WebUnitBaseTest;
import org.nextprot.api.web.domain.PepXResponse;
import org.nextprot.api.web.domain.PepxUtils;

public class PepXServiceTest extends WebUnitBaseTest {

	
	@Test
	public void shouldParsePep() throws Exception {

		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource("org/nextprot/api/pepx/pepxResponse.json").getFile());
		
		Scanner scanner = new Scanner(file, "UTF-8" );
		String content = scanner.useDelimiter("\\A").next();
		scanner.close();
		
		PepXResponse pepXResponse = PepxUtils.parsePepxResponse(content);
		
		assertTrue(((Boolean)pepXResponse.getParams().get("modeIL")) == true);
		assertTrue((pepXResponse.getParams().get("pep")).equals("TKMGLYYSYFK,TKMGL"));
			
		assertTrue((pepXResponse.getPeptideMatch("TKMGLYYSYFK").getEntryMatches().size() == 2));
		assertTrue((pepXResponse.getPeptideMatch("TKMGL").getEntryMatches().size() == 4));
		
	}
	

	@Test
	public void shouldBuildAnEntryWithVirtualAnnotations() throws Exception {

		String peptide = "GANAP";
		boolean modeIsoleucine = true;

		List<Pair<String, Integer>> isosAndPositions = Arrays.asList(new Pair<String, Integer>("Iso-1", null));
		@SuppressWarnings("unchecked") List<Annotation> annotations = mock(List.class);
		Isoform isoform = mock(Isoform.class);
		when(isoform.getUniqueName()).thenReturn("Iso-1");
		when(isoform.getSequence()).thenReturn("AGANAPA");
		
		List<Isoform> isoforms = Arrays.asList(isoform);

		List<Annotation> virtualAnnotations = PepXServiceImpl.buildEntryWithVirtualAnnotations(peptide, modeIsoleucine, isosAndPositions, annotations, isoforms);
		Annotation annot = virtualAnnotations.get(0);
		assertTrue(annot.getCategory().equals("pepx-virtual-annotation"));
		assertTrue(annot.getVariant() == null);
		assertTrue(annot.getTargetingIsoformsMap().keySet().contains("Iso-1"));
		
	}
	
	
	@Test
	public void shouldReturnAnEmptyArrayWhenThePeptideIsNotContainedInTheSequence() throws Exception {

			String peptide = "GANAP";
			boolean modeIsoleucine = true;

			List<Pair<String, Integer>> isosAndPositions = Arrays.asList(new Pair<String, Integer>("Iso-1", null)); //not positional since there is no position
			@SuppressWarnings("unchecked")
			List<Annotation> annotations = mock(List.class);
			Isoform isoform = mock(Isoform.class);
			when(isoform.getUniqueName()).thenReturn("Iso-1");
			when(isoform.getSequence()).thenReturn("AAAAAA");//Sequence does not contain the peptide
			
			List<Isoform> isoforms = Arrays.asList(isoform);

			List<Annotation> result = PepXServiceImpl.buildEntryWithVirtualAnnotations(peptide, modeIsoleucine, isosAndPositions, annotations, isoforms);
			assertTrue(result.isEmpty());

	}
	
	/* Specification have changed now it should be empty look at: #shouldReturnAnEmptyArrayWhenThePeptideIsNotContainedInTheSequence
	@Test(expected=NextProtException.class)
	public void shouldThrowAnExceptionWhenThePeptideIsNotContainedInTheSequence() throws Exception {

		try {
			String peptide = "GANAP";
			boolean modeIsoleucine = true;

			List<Pair<String, Integer>> isosAndPositions = Arrays.asList(new Pair<String, Integer>("Iso-1", null)); //not positional since there is no position
			@SuppressWarnings("unchecked")
			List<Annotation> annotations = mock(List.class);
			Isoform isoform = mock(Isoform.class);
			when(isoform.getUniqueName()).thenReturn("Iso-1");
			when(isoform.getSequence()).thenReturn("AAAAAA");//Sequence does not contain the peptide
			
			List<Isoform> isoforms = Arrays.asList(isoform);

			PepXServiceImpl.buildEntryWithVirtualAnnotations(peptide, modeIsoleucine, isosAndPositions, annotations, isoforms);

		}catch(NextProtException e){
			if(e.getMessage().contains("that is not in the current isoform in neXtProt")){
				throw e; //success tests
			}else fail();
		}
	}*/
	
	@Test
	public void shouldGiveAnAnnotationWithVariantWhenPresent() throws Exception {

			//Taking example NX_Q9H6T3
			String peptide = "GANAP";
			boolean modeIsoleucine = true;
			String isoName = "NX_Q9H6T3-3";

			Isoform isoform = mock(Isoform.class);
			when(isoform.getUniqueName()).thenReturn(isoName);
			//https://cdn.rawgit.com/calipho-sib/sequence-viewer/master/examples/simple.html (check that page to format the sequence)
			//GANAL is present instead of GANAP
			when(isoform.getSequence()).thenReturn("MDADPYNPVLPTNRASAYFRLKKFAVAESDCNLAVALNRSYTKAYSRRGAARFALQKLEEAKKDYERVLELEPNNFEATNELRKISQALASKENSYPKEADIVIKSTEGERKQIEAQQNKQQAISEKDRGNGFFKEGKYERAIECYTRGIAADGANALLPANRAMAYLKIQKYEEAEKDCTQAILLDGSYSKAFARRGTARTFLGKLNEAKQDFETVLLLEPGNKQAVTELSKIKKELIEKGHWDDVFLDSTQRQNVVKPIDNPPHPGSTKPLKKVIIEETGNLIQTIDVPDSTTAAAPENNPINLANVIAATGTTSKKNSSQDDLFPTSDTPRAKVLKIEEVSDTSSLQPQASLKQDVCQSYSEKMPIEIEQKPAQFATTVLPPIPANSFQLESDFRQLKSSPDMLYQYLKQIEPSLYPKLFQKNLDPDVFNQIVKILHDFYIEKEKPLLIFEILQRLSELKRFDMAVMFMSETEKKIARALFNHIDKSGLKDSSVEELKKRYGG"); 
			
			List<Pair<String, Integer>> isosAndPositions = Arrays.asList(new Pair<String, Integer>(isoName, 154)); //Position of the begin of peptide
			List<Annotation> annots = Arrays.asList(getMockedAnnotation("L", "P", 158, isoName, true));
			List<Isoform> isoforms = Arrays.asList(isoform);

			List<Annotation> pepxAnnots = PepXServiceImpl.buildEntryWithVirtualAnnotations(peptide, modeIsoleucine, isosAndPositions, annots, isoforms); //empty or null annotations
			assertTrue(pepxAnnots.size() == 1);
			assertTrue(pepxAnnots.get(0).getVariant().getOriginal().equals("L"));
			assertTrue(pepxAnnots.get(0).getVariant().getVariant().equals("P"));
			assertTrue(pepxAnnots.get(0).getStartPositionForIsoform(isoName) == 158);
			assertTrue(pepxAnnots.get(0).getEndPositionForIsoform(isoName) == 158);
	}


	@Test
	public void shouldReturnAnEmptryListIfTheVariantIsNotConaintedInThePeptide() throws Exception {

			//Taking example NX_Q9H6T3
			String peptide = "GANAP";
			boolean modeIsoleucine = true;
			String isoName = "NX_Q9H6T3-3";

			Isoform isoform = mock(Isoform.class);
			when(isoform.getUniqueName()).thenReturn(isoName);
			//https://cdn.rawgit.com/calipho-sib/sequence-viewer/master/examples/simple.html (check that page to format the sequence)
			//GANAL is present instead of GANAP
			when(isoform.getSequence()).thenReturn("MDADPYNPVLPTNRASAYFRLKKFAVAESDCNLAVALNRSYTKAYSRRGAARFALQKLEEAKKDYERVLELEPNNFEATNELRKISQALASKENSYPKEADIVIKSTEGERKQIEAQQNKQQAISEKDRGNGFFKEGKYERAIECYTRGIAADGANALLPANRAMAYLKIQKYEEAEKDCTQAILLDGSYSKAFARRGTARTFLGKLNEAKQDFETVLLLEPGNKQAVTELSKIKKELIEKGHWDDVFLDSTQRQNVVKPIDNPPHPGSTKPLKKVIIEETGNLIQTIDVPDSTTAAAPENNPINLANVIAATGTTSKKNSSQDDLFPTSDTPRAKVLKIEEVSDTSSLQPQASLKQDVCQSYSEKMPIEIEQKPAQFATTVLPPIPANSFQLESDFRQLKSSPDMLYQYLKQIEPSLYPKLFQKNLDPDVFNQIVKILHDFYIEKEKPLLIFEILQRLSELKRFDMAVMFMSETEKKIARALFNHIDKSGLKDSSVEELKKRYGG"); 
			
			List<Pair<String, Integer>> isosAndPositions = Arrays.asList(new Pair<String, Integer>(isoName, 154)); //Position of the begin of peptide
			List<Annotation> annots = Arrays.asList(getMockedAnnotation("L", "Z", 158, isoName, true));
			List<Isoform> isoforms = Arrays.asList(isoform);

			List<Annotation> result = PepXServiceImpl.buildEntryWithVirtualAnnotations(peptide, modeIsoleucine, isosAndPositions, annots, isoforms); //empty or null annotations
			assertTrue(result.isEmpty());

	}

	/*Specification has changed look at: shouldReturnAnEmptryListIfTheVariantIsNotConaintedInThePeptide
	@Test(expected=NextProtException.class)
	public void shouldGiveAnExceptionIfTheVariantIsNotConaintedInThePeptide() throws Exception {
		try {

			//Taking example NX_Q9H6T3
			String peptide = "GANAP";
			boolean modeIsoleucine = true;
			String isoName = "NX_Q9H6T3-3";

			Isoform isoform = mock(Isoform.class);
			when(isoform.getUniqueName()).thenReturn(isoName);
			//https://cdn.rawgit.com/calipho-sib/sequence-viewer/master/examples/simple.html (check that page to format the sequence)
			//GANAL is present instead of GANAP
			when(isoform.getSequence()).thenReturn("MDADPYNPVLPTNRASAYFRLKKFAVAESDCNLAVALNRSYTKAYSRRGAARFALQKLEEAKKDYERVLELEPNNFEATNELRKISQALASKENSYPKEADIVIKSTEGERKQIEAQQNKQQAISEKDRGNGFFKEGKYERAIECYTRGIAADGANALLPANRAMAYLKIQKYEEAEKDCTQAILLDGSYSKAFARRGTARTFLGKLNEAKQDFETVLLLEPGNKQAVTELSKIKKELIEKGHWDDVFLDSTQRQNVVKPIDNPPHPGSTKPLKKVIIEETGNLIQTIDVPDSTTAAAPENNPINLANVIAATGTTSKKNSSQDDLFPTSDTPRAKVLKIEEVSDTSSLQPQASLKQDVCQSYSEKMPIEIEQKPAQFATTVLPPIPANSFQLESDFRQLKSSPDMLYQYLKQIEPSLYPKLFQKNLDPDVFNQIVKILHDFYIEKEKPLLIFEILQRLSELKRFDMAVMFMSETEKKIARALFNHIDKSGLKDSSVEELKKRYGG"); 
			
			List<Pair<String, Integer>> isosAndPositions = Arrays.asList(new Pair<String, Integer>(isoName, 154)); //Position of the begin of peptide
			List<Annotation> annots = Arrays.asList(getMockedAnnotation("L", "Z", 158, isoName, true));
			List<Isoform> isoforms = Arrays.asList(isoform);

			PepXServiceImpl.buildEntryWithVirtualAnnotations(peptide, modeIsoleucine, isosAndPositions, annots, isoforms); //empty or null annotations
		}catch(NextProtException e){
			if(e.getMessage().contains("No valid variants found for isoform ")){
				throw e; //success tests
			}else fail();
		}

	}*/

	
	@Test(expected=NextProtException.class)
	public void shouldGiveAnExceptionIfTheOriginalIsNotPresentOnTheSequence() throws Exception {
		try {

			//Taking example NX_Q9H6T3
			String peptide = "GANAP";
			boolean modeIsoleucine = true;
			String isoName = "NX_Q9H6T3-3";

			Isoform isoform = mock(Isoform.class);
			when(isoform.getUniqueName()).thenReturn(isoName);
			//https://cdn.rawgit.com/calipho-sib/sequence-viewer/master/examples/simple.html (check that page to format the sequence)
			//GANAL is present instead of GANAP
			when(isoform.getSequence()).thenReturn("MDADPYNPVLPTNRASAYFRLKKFAVAESDCNLAVALNRSYTKAYSRRGAARFALQKLEEAKKDYERVLELEPNNFEATNELRKISQALASKENSYPKEADIVIKSTEGERKQIEAQQNKQQAISEKDRGNGFFKEGKYERAIECYTRGIAADGANALLPANRAMAYLKIQKYEEAEKDCTQAILLDGSYSKAFARRGTARTFLGKLNEAKQDFETVLLLEPGNKQAVTELSKIKKELIEKGHWDDVFLDSTQRQNVVKPIDNPPHPGSTKPLKKVIIEETGNLIQTIDVPDSTTAAAPENNPINLANVIAATGTTSKKNSSQDDLFPTSDTPRAKVLKIEEVSDTSSLQPQASLKQDVCQSYSEKMPIEIEQKPAQFATTVLPPIPANSFQLESDFRQLKSSPDMLYQYLKQIEPSLYPKLFQKNLDPDVFNQIVKILHDFYIEKEKPLLIFEILQRLSELKRFDMAVMFMSETEKKIARALFNHIDKSGLKDSSVEELKKRYGG"); 
			
			List<Pair<String, Integer>> isosAndPositions = Arrays.asList(new Pair<String, Integer>(isoName, 154)); //Position of the begin of peptide
			//Original is not contained in the sequence, should be a L L->P (GANAL)
			List<Annotation> annots = Arrays.asList(getMockedAnnotation("O", "P", 158, isoName, true));
			List<Isoform> isoforms = Arrays.asList(isoform);

			PepXServiceImpl.buildEntryWithVirtualAnnotations(peptide, modeIsoleucine, isosAndPositions, annots, isoforms); //empty or null annotations
		}catch(NextProtException e){
			if(e.getMessage().contains("The amino acid")){
				throw e; //success tests
			}else fail();
		}
	

	}

	
	@Test(expected=NextProtException.class)
	public void shouldThrowAnExceptionWhenPepXGivesAVariantNotSpecificToTheIsoform() throws Exception {

		try {

			//Taking example NX_Q9H6T3
			String peptide = "GANAP";
			boolean modeIsoleucine = true;
			String isoName = "NX_Q9H6T3-3";

			Isoform iso1 = mock(Isoform.class);
			when(iso1.getUniqueName()).thenReturn("another-iso-name");
			when(iso1.getSequence()).thenReturn("MDADPYNPVLPTNRASAYFRLKKFAVAESDCNLAVALNRSYTKAYSRRGAARFALQKLEEAKKDYERVLELEPNNFEATNELRKISQALASKENSYPKEADIVIKSTEGERKQIEAQQNKQQAISEKDRGNGFFKEGKYERAIECYTRGIAADGANALLPANRAMAYLKIQKYEEAEKDCTQAILLDGSYSKAFARRGTARTFLGKLNEAKQDFETVLLLEPGNKQAVTELSKIKKELIEKGHWDDVFLDSTQRQNVVKPIDNPPHPGSTKPLKKVIIEETGNLIQTIDVPDSTTAAAPENNPINLANVIAATGTTSKKNSSQDDLFPTSDTPRAKVLKIEEVSDTSSLQPQASLKQDVCQSYSEKMPIEIEQKPAQFATTVLPPIPANSFQLESDFRQLKSSPDMLYQYLKQIEPSLYPKLFQKNLDPDVFNQIVKILHDFYIEKEKPLLIFEILQRLSELKRFDMAVMFMSETEKKIARALFNHIDKSGLKDSSVEELKKRYGG"); 
			
			List<Pair<String, Integer>> isosAndPositions = Arrays.asList(new Pair<String, Integer>(isoName, 154)); //Position of the begin of peptide
			List<Annotation> annots = Arrays.asList(getMockedAnnotation("L", "P", 158, isoName, true));
			List<Isoform> isoforms = Arrays.asList(iso1);

			PepXServiceImpl.buildEntryWithVirtualAnnotations(peptide, modeIsoleucine, isosAndPositions, annots, isoforms); //empty or null annotations


		}catch(NextProtException e){
			if(e.getMessage().contains("is not specific for this isoform")){
				throw e; //success tests
			}else fail();
		}
	}
	
	
	@Test
	public void shouldReturnAValidAnnotationIfTheVariantIsContainedInThePeptipeAndItIsSpecificToTheIsoform() throws Exception {
		String isoName = "iso-1";
		List<Annotation> annots = Arrays.asList(getMockedAnnotation("E", "D", 5, isoName, true));
		List<Annotation> resultAnnots = PepXServiceImpl.filterValidVariantAnnotations("DDF", true, annots, isoName, "ABCDEFGHI");

		assertTrue(resultAnnots.size() == 1);
	}

	
	@Test
	public void shouldNotReturnAValidAnnotationIfItIsNotSpecificToTheIsoform() throws Exception {
		String isoName = "iso-1";
		List<Annotation> annots = Arrays.asList(getMockedAnnotation("E", "D", 5, isoName, true));
		List<Annotation> resultAnnots = PepXServiceImpl.filterValidVariantAnnotations("DDF", true, annots, "another-iso-name", "ABCDEFGHI");
		assertTrue(resultAnnots.size() == 0);
	}
	
	
	@Test
	public void shouldNotReturnAValidAnnotationIfTheVariantIsNotContainedInThePeptide() throws Exception {
		String isoName = "iso-1";
		List<Annotation> annots = Arrays.asList(getMockedAnnotation("E", "Z", 4, isoName, true));
		List<Annotation> resultAnnots = PepXServiceImpl.filterValidVariantAnnotations("DDF", true, annots, isoName, "ABCDEFGHI");
		assertTrue(resultAnnots.size() == 0);
	}
	
	@Test(expected = NextProtException.class)
	public void shouldThrowAnExceptionIfTheOriginalAminoAcidIsNotInTheSequenceAtThatPosition() throws Exception {
		try {
				String isoName = "iso-1";
				List<Annotation> annots = Arrays.asList(getMockedAnnotation("E", "D", 4, isoName, true));
				List<Annotation> resultAnnots = PepXServiceImpl.filterValidVariantAnnotations("DDF", true, annots, isoName, "ABCDEFGHI");
				assertTrue(resultAnnots.size() == 0);
		
			}catch(NextProtException e){
				if(e.getMessage().contains("The amino acid ")){
					throw e; //success tests
				}else fail();
			}
	}
	
	
	private Annotation getMockedAnnotation(String original, String variant, int position, String isoName, boolean isAnnotationPositionalForIso){
		
		Annotation a1 = mock(Annotation.class, Mockito.RETURNS_DEEP_STUBS);
		when(a1.getVariant().getOriginal()).thenReturn(original);
		when(a1.getVariant().getVariant()).thenReturn(variant);
		when(a1.getStartPositionForIsoform(isoName)).thenReturn(position);
		when(a1.getEndPositionForIsoform(isoName)).thenReturn(position);
		when(a1.isAnnotationPositionalForIsoform(isoName)).thenReturn(isAnnotationPositionalForIso);
		return a1;

	}

}