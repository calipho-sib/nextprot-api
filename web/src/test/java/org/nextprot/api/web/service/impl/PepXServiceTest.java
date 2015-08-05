package org.nextprot.api.web.service.impl;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.mockito.Mockito;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.commons.utils.Pair;
import org.nextprot.api.core.domain.Isoform;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.web.dbunit.base.mvc.WebUnitBaseTest;

public class PepXServiceTest extends WebUnitBaseTest {


	@Test
	public void shouldBuildAnEntryWithVirtualAnnotations() throws Exception {

		String peptide = "GANAP";
		boolean modeIsoleucine = true;
		String entryName = "NX_FAKE_ENTRY";
		
		List<Pair<String, Integer>> isosAndPositions = Arrays.asList(new Pair<String, Integer>("Iso-1", null));
		@SuppressWarnings("unchecked") List<Annotation> annotations = mock(List.class);
		Isoform isoform = mock(Isoform.class);
		when(isoform.getUniqueName()).thenReturn("Iso-1");
		when(isoform.getSequence()).thenReturn("AGANAPA");
		
		List<Isoform> isoforms = Arrays.asList(isoform);
		

		List<Annotation> virtualAnnotations = PepXServiceImpl.buildEntryWithVirtualAnnotations(peptide, modeIsoleucine, entryName, isosAndPositions, annotations, isoforms);
		Annotation annot = virtualAnnotations.get(0);
		assertTrue(annot.getCategory().equals("pepx-virtual-annotation"));
		assertTrue(annot.getVariant() == null);
		assertTrue(annot.getTargetIsoformsMap().keySet().contains("Iso-1"));
		
	}
	
	
	@Test(expected=NextProtException.class)
	public void shouldThrowAnExceptionWhenThePeptideIsNotContainedInTheSequence() throws Exception {

		try {
			String peptide = "GANAP";
			boolean modeIsoleucine = true;
			String entryName = "NX_FAKE_ENTRY";
			
			List<Pair<String, Integer>> isosAndPositions = Arrays.asList(new Pair<String, Integer>("Iso-1", null)); //not positional since there is no position
			List<Annotation> annotations = mock(List.class);
			Isoform isoform = mock(Isoform.class);
			when(isoform.getUniqueName()).thenReturn("Iso-1");
			when(isoform.getSequence()).thenReturn("AAAAAA");//Sequence does not contain the peptide
			
			List<Isoform> isoforms = Arrays.asList(isoform);

			PepXServiceImpl.buildEntryWithVirtualAnnotations(peptide, modeIsoleucine, entryName, isosAndPositions, annotations, isoforms);

		}catch(NextProtException e){
			if(e.getMessage().contains("that is not in the current isoform in neXtProt")){
				throw e; //success tests
			}else fail();
		}
	}
	
	@Test
	public void shouldGiveAnAnnotationWithVariantWhenPresent() throws Exception {

			//Taking example NX_Q9H6T3
			String peptide = "GANAP";
			boolean modeIsoleucine = true;
			String entryName = "NX_Q9H6T3";
			String isoName = "NX_Q9H6T3-3";

			Isoform isoform = mock(Isoform.class);
			when(isoform.getUniqueName()).thenReturn(isoName);
			//https://cdn.rawgit.com/calipho-sib/sequence-viewer/master/examples/simple.html (check that page to format the sequence)
			//GANAL is present instead of GANAP
			when(isoform.getSequence()).thenReturn("MDADPYNPVLPTNRASAYFRLKKFAVAESDCNLAVALNRSYTKAYSRRGAARFALQKLEEAKKDYERVLELEPNNFEATNELRKISQALASKENSYPKEADIVIKSTEGERKQIEAQQNKQQAISEKDRGNGFFKEGKYERAIECYTRGIAADGANALLPANRAMAYLKIQKYEEAEKDCTQAILLDGSYSKAFARRGTARTFLGKLNEAKQDFETVLLLEPGNKQAVTELSKIKKELIEKGHWDDVFLDSTQRQNVVKPIDNPPHPGSTKPLKKVIIEETGNLIQTIDVPDSTTAAAPENNPINLANVIAATGTTSKKNSSQDDLFPTSDTPRAKVLKIEEVSDTSSLQPQASLKQDVCQSYSEKMPIEIEQKPAQFATTVLPPIPANSFQLESDFRQLKSSPDMLYQYLKQIEPSLYPKLFQKNLDPDVFNQIVKILHDFYIEKEKPLLIFEILQRLSELKRFDMAVMFMSETEKKIARALFNHIDKSGLKDSSVEELKKRYGG"); 
			
			List<Pair<String, Integer>> isosAndPositions = Arrays.asList(new Pair<String, Integer>(isoName, 154)); //Position of the begin of peptide
			Annotation variantAnnotation = mock(Annotation.class, Mockito.RETURNS_DEEP_STUBS);
			when(variantAnnotation.getStartPositionForIsoform(isoName)).thenReturn(158);
			when(variantAnnotation.getEndPositionForIsoform(isoName)).thenReturn(158);
			when(variantAnnotation.isAnnotationPositionalForIsoform(isoName)).thenReturn(true);

			when(variantAnnotation.getVariant().getOriginal()).thenReturn("L");
			when(variantAnnotation.getVariant().getVariant()).thenReturn("P");

			List<Annotation> annotations = Arrays.asList(variantAnnotation);
			List<Isoform> isoforms = Arrays.asList(isoform);

			List<Annotation> pepxAnnots = PepXServiceImpl.buildEntryWithVirtualAnnotations(peptide, modeIsoleucine, entryName, isosAndPositions, annotations, isoforms); //empty or null annotations
			assertTrue(pepxAnnots.size() == 1);
			assertTrue(pepxAnnots.get(0).getVariant().getOriginal().equals("L"));
			assertTrue(pepxAnnots.get(0).getVariant().getVariant().equals("P"));
			assertTrue(pepxAnnots.get(0).getStartPositionForIsoform(isoName) == 158);
			assertTrue(pepxAnnots.get(0).getEndPositionForIsoform(isoName) == 158);

	}
	
	
	@Test(expected=NextProtException.class)
	public void shouldThrowAnExceptionWhenThereIsAPositionButPeptideWithVariantNotContainedInTheIsoform() throws Exception {

		try {
			String peptide = "GANAP";
			boolean modeIsoleucine = true;
			String entryName = "NX_FAKE_ENTRY";
			
			List<Pair<String, Integer>> isosAndPositions = Arrays.asList(new Pair<String, Integer>("Iso-1", 30));
			PepXServiceImpl.buildEntryWithVirtualAnnotations(peptide, modeIsoleucine, entryName, isosAndPositions, new ArrayList<Annotation>(), null); //empty or null annotations

		}catch(NextProtException e){
			if(e.getMessage().contains("PepX returned a variant that we do not consider a cosmic variant for isoform")){
				throw e; //success tests
			}else fail();
		}
	}
	
	
	// 
	@Test
	public void shouldReturnAValidAnnotationIfTheVariantIsContainedInThePeptipeAndItIsSpecificToTheIsoform() throws Exception {
		String isoName = "iso-1";
		Annotation a1 = mock(Annotation.class, Mockito.RETURNS_DEEP_STUBS);
		when(a1.getVariant().getVariant()).thenReturn("D");
		when(a1.isAnnotationPositionalForIsoform(isoName)).thenReturn(true);

		List<Annotation> annots = Arrays.asList(a1);
		List<Annotation> resultAnnots = PepXServiceImpl.filterValidVariantAnnotations("ABCD", true, annots, isoName);

		assertTrue(resultAnnots.size() == 1);
	}

	
	@Test
	public void shouldNotReturnAValidAnnotationIfItIsNotSpecificToTheIsoform() throws Exception {
		String isoName = "iso-1";
		Annotation a1 = mock(Annotation.class, Mockito.RETURNS_DEEP_STUBS);
		when(a1.getVariant().getVariant()).thenReturn("D");
		when(a1.isAnnotationPositionalForIsoform(isoName)).thenReturn(true);

		List<Annotation> annots = Arrays.asList(a1);
		List<Annotation> resultAnnots = PepXServiceImpl.filterValidVariantAnnotations("ABCD", true, annots, "another-iso-name");

		assertTrue(resultAnnots.size() == 0);
	}
	
	
	@Test
	public void shouldNotReturnAValidAnnotationIfTheVariantIsNotContainedInThePeptide() throws Exception {
		String isoName = "iso-1";
		Annotation a1 = mock(Annotation.class, Mockito.RETURNS_DEEP_STUBS);
		when(a1.getVariant().getVariant()).thenReturn("F");
		when(a1.isAnnotationPositionalForIsoform(isoName)).thenReturn(true);

		List<Annotation> annots = Arrays.asList(a1);
		List<Annotation> resultAnnots = PepXServiceImpl.filterValidVariantAnnotations("ABCD", true, annots, isoName);

		assertTrue(resultAnnots.size() == 0);
	}

}