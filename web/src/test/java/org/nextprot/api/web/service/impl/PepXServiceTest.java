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
import org.nextprot.api.core.utils.PeptideUtils;
import org.nextprot.api.web.dbunit.base.mvc.WebUnitBaseTest;
import org.nextprot.api.web.domain.PepXResponse;
import org.nextprot.api.web.domain.PepXResponse.PepXEntryMatch;
import org.nextprot.api.web.domain.PepXResponse.PepXIsoformMatch;
import org.nextprot.api.web.domain.PepXResponse.PepXMatch;
import org.nextprot.api.web.domain.PepxUtils;

public class PepXServiceTest extends WebUnitBaseTest {

	private static final String ISO_NAME = "NX_P01234-1";
	
	@Test
	public void shouldParsePep() throws Exception {

		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource("org/nextprot/api/pepx/pepxResponse.json").getFile());

		Scanner scanner = new Scanner(file, "UTF-8");
		String content = scanner.useDelimiter("\\A").next();
		scanner.close();

		PepXResponse pepXResponse = PepxUtils.parsePepxResponse(content);
	
		//System.out.println(pepXResponse.getEntriesNames().size());
		//assertTrue(pepXResponse.getEntriesNames().size() == 20);

		assertTrue(((Integer) pepXResponse.getParams().get("modeIL")) == 1);
		assertTrue((pepXResponse.getParams().get("peplist")).equals("TKMGLYYSYFK,TKMGL"));

		assertTrue((pepXResponse.getPeptideMatch("TKMGLYYSYFK").getEntryMatches().size() == 3));
		assertTrue((pepXResponse.getPeptideMatch("TKMGL").getEntryMatches().size() == 17));

		PepXMatch pepXMatch = pepXResponse.getPeptideMatch("TKMGL");

		// Test entry names
		List<String> names = pepXMatch.getEntryNamesMatches();
		assertTrue(names.size() == 17);

		assertTrue(names.contains("Q6NUT2"));
		assertTrue(names.contains("Q2PZI1"));
		assertTrue(names.contains("Q9UKT4"));
		assertTrue(names.contains("Q6NXN4"));

		{
			PepXEntryMatch pepXEntryMatch = pepXMatch.getPepxMatchesForEntry("Q6NUT2");
			assertTrue(pepXEntryMatch.getIsoforms().get(0).getPosition().equals(148));
		}

		{
			PepXEntryMatch pepXEntryMatch2 = pepXMatch.getPepxMatchesForEntry("O00327");
			assertTrue(pepXEntryMatch2.getIsoforms().get(1).getPosition() == null);
		}

	}

	@Test
	public void shouldBuildAnEntryWithVirtualAnnotations() throws Exception {

		String peptide = "GANAP";
		boolean modeIsoleucine = true;
		
		PepXIsoformMatch pepXIsoformMatch = new PepXIsoformMatch();
		pepXIsoformMatch.setIsoformName(ISO_NAME);
		
		@SuppressWarnings("unchecked")
		List<Annotation> annotations = mock(List.class);
		Isoform isoform = mock(Isoform.class);
		when(isoform.getUniqueName()).thenReturn(ISO_NAME);
		when(isoform.getSequence()).thenReturn("AGANAPA");

		List<Isoform> isoforms = Arrays.asList(isoform);

		List<Annotation> virtualAnnotations = PepXServiceImpl.buildEntryWithVirtualAnnotations(peptide, modeIsoleucine, Arrays.asList(pepXIsoformMatch), annotations, isoforms);
		Annotation annot = virtualAnnotations.get(0);
		assertTrue(annot.getCategory().equals("pepx-virtual-annotation"));
		assertTrue(annot.getVariant() == null);
		assertTrue(annot.getTargetingIsoformsMap().keySet().contains(ISO_NAME));

	}

	@Test
	public void shouldReturnAnEmptyArrayWhenThePeptideIsNotContainedInTheSequence() throws Exception {

		String peptide = "GANAP";
		boolean modeIsoleucine = true;

		PepXIsoformMatch pepXIsoformMatch = new PepXIsoformMatch(ISO_NAME);
		
		
		@SuppressWarnings("unchecked")
		List<Annotation> annotations = mock(List.class);
		Isoform isoform = mock(Isoform.class);
		when(isoform.getUniqueName()).thenReturn(ISO_NAME);
		when(isoform.getSequence()).thenReturn("AAAAAA");// Sequence does not
															// contain the
															// peptide

		List<Isoform> isoforms = Arrays.asList(isoform);

		List<Annotation> result = PepXServiceImpl.buildEntryWithVirtualAnnotations(peptide, modeIsoleucine, Arrays.asList(pepXIsoformMatch), annotations, isoforms);
		assertTrue(result.isEmpty());

	}

	/*
	 * Specification have changed now it should be empty look at:
	 * #shouldReturnAnEmptyArrayWhenThePeptideIsNotContainedInTheSequence
	 * 
	 * @Test(expected=NextProtException.class) public void
	 * shouldThrowAnExceptionWhenThePeptideIsNotContainedInTheSequence() throws
	 * Exception {
	 * 
	 * try { String peptide = "GANAP"; boolean modeIsoleucine = true;
	 * 
	 * List<Pair<String, Integer>> isosAndPositions = Arrays.asList(new
	 * Pair<String, Integer>("Iso-1", null)); //not positional since there is no
	 * position
	 * 
	 * @SuppressWarnings("unchecked") List<Annotation> annotations =
	 * mock(List.class); Isoform isoform = mock(Isoform.class);
	 * when(isoform.getUniqueName()).thenReturn("Iso-1");
	 * when(isoform.getSequence()).thenReturn("AAAAAA");//Sequence does not
	 * contain the peptide
	 * 
	 * List<Isoform> isoforms = Arrays.asList(isoform);
	 * 
	 * PepXServiceImpl.buildEntryWithVirtualAnnotations(peptide, modeIsoleucine,
	 * isosAndPositions, annotations, isoforms);
	 * 
	 * }catch(NextProtException e){ if(e.getMessage().contains(
	 * "that is not in the current isoform in neXtProt")){ throw e; //success
	 * tests }else fail(); } }
	 */

	@Test
	public void shouldGiveAnAnnotationWithVariantWhenPresent() throws Exception {

		// Taking example NX_Q9H6T3
		String peptide = "GANAP";
		boolean modeIsoleucine = true;
		String isoName = "NX_Q9H6T3-3";

		Isoform isoform = mock(Isoform.class);
		when(isoform.getUniqueName()).thenReturn(isoName);
		// https://cdn.rawgit.com/calipho-sib/sequence-viewer/master/examples/simple.html
		// (check that page to format the sequence)
		// GANAL is present instead of GANAP
		when(isoform.getSequence()).thenReturn(
				"MDADPYNPVLPTNRASAYFRLKKFAVAESDCNLAVALNRSYTKAYSRRGAARFALQKLEEAKKDYERVLELEPNNFEATNELRKISQALASKENSYPKEADIVIKSTEGERKQIEAQQNKQQAISEKDRGNGFFKEGKYERAIECYTRGIAADGANALLPANRAMAYLKIQKYEEAEKDCTQAILLDGSYSKAFARRGTARTFLGKLNEAKQDFETVLLLEPGNKQAVTELSKIKKELIEKGHWDDVFLDSTQRQNVVKPIDNPPHPGSTKPLKKVIIEETGNLIQTIDVPDSTTAAAPENNPINLANVIAATGTTSKKNSSQDDLFPTSDTPRAKVLKIEEVSDTSSLQPQASLKQDVCQSYSEKMPIEIEQKPAQFATTVLPPIPANSFQLESDFRQLKSSPDMLYQYLKQIEPSLYPKLFQKNLDPDVFNQIVKILHDFYIEKEKPLLIFEILQRLSELKRFDMAVMFMSETEKKIARALFNHIDKSGLKDSSVEELKKRYGG");


		PepXIsoformMatch pepXIsoformMatch = new PepXIsoformMatch(isoName, 154);

		List<Annotation> annots = Arrays.asList(getMockedAnnotation("L", "P", 158, isoName, true));
		List<Isoform> isoforms = Arrays.asList(isoform);

		List<Annotation> pepxAnnots = PepXServiceImpl.buildEntryWithVirtualAnnotations(peptide, modeIsoleucine, Arrays.asList(pepXIsoformMatch), annots, isoforms); // empty
																																						// or
																																						// null
																																						// annotations
		assertTrue(pepxAnnots.size() == 1);
		assertTrue(pepxAnnots.get(0).getVariant().getOriginal().equals("L"));
		assertTrue(pepxAnnots.get(0).getVariant().getVariant().equals("P"));
		assertTrue(pepxAnnots.get(0).getStartPositionForIsoform(isoName) == 158);
		assertTrue(pepxAnnots.get(0).getEndPositionForIsoform(isoName) == 158);
	}

	@Test
	public void shouldReturnAnEmptryListIfTheVariantIsNotConaintedInThePeptide() throws Exception {

		// Taking example NX_Q9H6T3
		String peptide = "GANAP";
		boolean modeIsoleucine = true;
		String isoName = "NX_Q9H6T3-3";

		Isoform isoform = mock(Isoform.class);
		when(isoform.getUniqueName()).thenReturn(isoName);
		// https://cdn.rawgit.com/calipho-sib/sequence-viewer/master/examples/simple.html
		// (check that page to format the sequence)
		// GANAL is present instead of GANAP
		when(isoform.getSequence()).thenReturn(
				"MDADPYNPVLPTNRASAYFRLKKFAVAESDCNLAVALNRSYTKAYSRRGAARFALQKLEEAKKDYERVLELEPNNFEATNELRKISQALASKENSYPKEADIVIKSTEGERKQIEAQQNKQQAISEKDRGNGFFKEGKYERAIECYTRGIAADGANALLPANRAMAYLKIQKYEEAEKDCTQAILLDGSYSKAFARRGTARTFLGKLNEAKQDFETVLLLEPGNKQAVTELSKIKKELIEKGHWDDVFLDSTQRQNVVKPIDNPPHPGSTKPLKKVIIEETGNLIQTIDVPDSTTAAAPENNPINLANVIAATGTTSKKNSSQDDLFPTSDTPRAKVLKIEEVSDTSSLQPQASLKQDVCQSYSEKMPIEIEQKPAQFATTVLPPIPANSFQLESDFRQLKSSPDMLYQYLKQIEPSLYPKLFQKNLDPDVFNQIVKILHDFYIEKEKPLLIFEILQRLSELKRFDMAVMFMSETEKKIARALFNHIDKSGLKDSSVEELKKRYGG");

		PepXIsoformMatch pepXIsoformMatch = new PepXIsoformMatch(isoName, 154);

		List<Annotation> annots = Arrays.asList(getMockedAnnotation("L", "Z", 158, isoName, true));
		List<Isoform> isoforms = Arrays.asList(isoform);

		List<Annotation> result = PepXServiceImpl.buildEntryWithVirtualAnnotations(peptide, modeIsoleucine, Arrays.asList(pepXIsoformMatch), annots, isoforms); // empty
																																					// or
																																					// null
																																					// annotations
		assertTrue(result.isEmpty());

	}

	/*
	 * Specification has changed look at:
	 * shouldReturnAnEmptryListIfTheVariantIsNotConaintedInThePeptide
	 * 
	 * @Test(expected=NextProtException.class) public void
	 * shouldGiveAnExceptionIfTheVariantIsNotConaintedInThePeptide() throws
	 * Exception { try {
	 * 
	 * //Taking example NX_Q9H6T3 String peptide = "GANAP"; boolean
	 * modeIsoleucine = true; String isoName = "NX_Q9H6T3-3";
	 * 
	 * Isoform isoform = mock(Isoform.class);
	 * when(isoform.getUniqueName()).thenReturn(isoName);
	 * //https://cdn.rawgit.com/calipho-sib/sequence-viewer/master/examples/
	 * simple.html (check that page to format the sequence) //GANAL is present
	 * instead of GANAP when(isoform.getSequence()).thenReturn(
	 * "MDADPYNPVLPTNRASAYFRLKKFAVAESDCNLAVALNRSYTKAYSRRGAARFALQKLEEAKKDYERVLELEPNNFEATNELRKISQALASKENSYPKEADIVIKSTEGERKQIEAQQNKQQAISEKDRGNGFFKEGKYERAIECYTRGIAADGANALLPANRAMAYLKIQKYEEAEKDCTQAILLDGSYSKAFARRGTARTFLGKLNEAKQDFETVLLLEPGNKQAVTELSKIKKELIEKGHWDDVFLDSTQRQNVVKPIDNPPHPGSTKPLKKVIIEETGNLIQTIDVPDSTTAAAPENNPINLANVIAATGTTSKKNSSQDDLFPTSDTPRAKVLKIEEVSDTSSLQPQASLKQDVCQSYSEKMPIEIEQKPAQFATTVLPPIPANSFQLESDFRQLKSSPDMLYQYLKQIEPSLYPKLFQKNLDPDVFNQIVKILHDFYIEKEKPLLIFEILQRLSELKRFDMAVMFMSETEKKIARALFNHIDKSGLKDSSVEELKKRYGG"
	 * );
	 * 
	 * List<Pair<String, Integer>> isosAndPositions = Arrays.asList(new
	 * Pair<String, Integer>(isoName, 154)); //Position of the begin of peptide
	 * List<Annotation> annots = Arrays.asList(getMockedAnnotation("L", "Z",
	 * 158, isoName, true)); List<Isoform> isoforms = Arrays.asList(isoform);
	 * 
	 * PepXServiceImpl.buildEntryWithVirtualAnnotations(peptide, modeIsoleucine,
	 * isosAndPositions, annots, isoforms); //empty or null annotations
	 * }catch(NextProtException e){ if(e.getMessage().contains(
	 * "No valid variants found for isoform ")){ throw e; //success tests }else
	 * fail(); }
	 * 
	 * }
	 */

	@Test(expected = NextProtException.class)
	public void shouldGiveAnExceptionIfTheOriginalIsNotPresentOnTheSequence() throws Exception {
		try {

			// Taking example NX_Q9H6T3
			String peptide = "GANAP";
			boolean modeIsoleucine = true;
			String isoName = "NX_Q9H6T3-3";

			Isoform isoform = mock(Isoform.class);
			when(isoform.getUniqueName()).thenReturn(isoName);
			// https://cdn.rawgit.com/calipho-sib/sequence-viewer/master/examples/simple.html
			// (check that page to format the sequence)
			// GANAL is present instead of GANAP
			when(isoform.getSequence()).thenReturn(
					"MDADPYNPVLPTNRASAYFRLKKFAVAESDCNLAVALNRSYTKAYSRRGAARFALQKLEEAKKDYERVLELEPNNFEATNELRKISQALASKENSYPKEADIVIKSTEGERKQIEAQQNKQQAISEKDRGNGFFKEGKYERAIECYTRGIAADGANALLPANRAMAYLKIQKYEEAEKDCTQAILLDGSYSKAFARRGTARTFLGKLNEAKQDFETVLLLEPGNKQAVTELSKIKKELIEKGHWDDVFLDSTQRQNVVKPIDNPPHPGSTKPLKKVIIEETGNLIQTIDVPDSTTAAAPENNPINLANVIAATGTTSKKNSSQDDLFPTSDTPRAKVLKIEEVSDTSSLQPQASLKQDVCQSYSEKMPIEIEQKPAQFATTVLPPIPANSFQLESDFRQLKSSPDMLYQYLKQIEPSLYPKLFQKNLDPDVFNQIVKILHDFYIEKEKPLLIFEILQRLSELKRFDMAVMFMSETEKKIARALFNHIDKSGLKDSSVEELKKRYGG");

			PepXIsoformMatch pepXIsoformMatch = new PepXIsoformMatch(isoName, 154);

			
			// Original is not contained in the sequence, should be a L L->P
			// (GANAL)
			List<Annotation> annots = Arrays.asList(getMockedAnnotation("O", "P", 158, isoName, true));
			List<Isoform> isoforms = Arrays.asList(isoform);

			PepXServiceImpl.buildEntryWithVirtualAnnotations(peptide, modeIsoleucine, Arrays.asList(pepXIsoformMatch), annots, isoforms); // empty
																															// or
																															// null
																															// annotations
		} catch (NextProtException e) {
			if (e.getMessage().contains("The amino acid")) {
				throw e; // success tests
			} else
				fail();
		}

	}

	@Test(expected = NextProtException.class)
	public void shouldThrowAnExceptionWhenPepXGivesAVariantNotSpecificToTheIsoform() throws Exception {

		try {

			// Taking example NX_Q9H6T3
			String peptide = "GANAP";
			boolean modeIsoleucine = true;
			String isoName = "NX_Q9H6T3-3";

			Isoform iso1 = mock(Isoform.class);
			when(iso1.getUniqueName()).thenReturn("another-iso-name");
			when(iso1.getSequence()).thenReturn(
					"MDADPYNPVLPTNRASAYFRLKKFAVAESDCNLAVALNRSYTKAYSRRGAARFALQKLEEAKKDYERVLELEPNNFEATNELRKISQALASKENSYPKEADIVIKSTEGERKQIEAQQNKQQAISEKDRGNGFFKEGKYERAIECYTRGIAADGANALLPANRAMAYLKIQKYEEAEKDCTQAILLDGSYSKAFARRGTARTFLGKLNEAKQDFETVLLLEPGNKQAVTELSKIKKELIEKGHWDDVFLDSTQRQNVVKPIDNPPHPGSTKPLKKVIIEETGNLIQTIDVPDSTTAAAPENNPINLANVIAATGTTSKKNSSQDDLFPTSDTPRAKVLKIEEVSDTSSLQPQASLKQDVCQSYSEKMPIEIEQKPAQFATTVLPPIPANSFQLESDFRQLKSSPDMLYQYLKQIEPSLYPKLFQKNLDPDVFNQIVKILHDFYIEKEKPLLIFEILQRLSELKRFDMAVMFMSETEKKIARALFNHIDKSGLKDSSVEELKKRYGG");

			PepXIsoformMatch pepXIsoformMatch = new PepXIsoformMatch(isoName, 154);

			List<Annotation> annots = Arrays.asList(getMockedAnnotation("L", "P", 158, isoName, true));
			List<Isoform> isoforms = Arrays.asList(iso1);

			PepXServiceImpl.buildEntryWithVirtualAnnotations(peptide, modeIsoleucine, Arrays.asList(pepXIsoformMatch), annots, isoforms); // empty
																															// or
																															// null
																															// annotations

		} catch (NextProtException e) {
			if (e.getMessage().contains("is not specific for this isoform")) {
				throw e; // success tests
			} else
				fail();
		}
	}

	@Test
	public void shouldReturnAValidAnnotationIfTheVariantIsContainedInThePeptipeAndItIsSpecificToTheIsoform() throws Exception {
		List<Annotation> annots = Arrays.asList(getMockedAnnotation("E", "D", 5, ISO_NAME, true));
		List<Annotation> resultAnnots = PepXServiceImpl.filterValidVariantAnnotations("DDF", true, annots, ISO_NAME, "ABCDEFGHI");

		assertTrue(resultAnnots.size() == 1);
	}

	@Test
	public void shouldNotReturnAValidAnnotationIfItIsNotSpecificToTheIsoform() throws Exception {
		List<Annotation> annots = Arrays.asList(getMockedAnnotation("E", "D", 5, ISO_NAME, true));
		List<Annotation> resultAnnots = PepXServiceImpl.filterValidVariantAnnotations("DDF", true, annots, "another-iso-name", "ABCDEFGHI");
		assertTrue(resultAnnots.size() == 0);
	}

	@Test
	public void shouldNotReturnAValidAnnotationIfTheVariantIsNotContainedInThePeptide() throws Exception {
		List<Annotation> annots = Arrays.asList(getMockedAnnotation("E", "Z", 4, ISO_NAME, true));
		List<Annotation> resultAnnots = PepXServiceImpl.filterValidVariantAnnotations("DDF", true, annots, ISO_NAME, "ABCDEFGHI");
		assertTrue(resultAnnots.size() == 0);
	}

	@Test(expected = NextProtException.class)
	public void shouldThrowAnExceptionIfTheOriginalAminoAcidIsNotInTheSequenceAtThatPosition() throws Exception {
		try {
			List<Annotation> annots = Arrays.asList(getMockedAnnotation("E", "D", 4, ISO_NAME, true));
			List<Annotation> resultAnnots = PepXServiceImpl.filterValidVariantAnnotations("DDF", true, annots, ISO_NAME, "ABCDEFGHI");
			assertTrue(resultAnnots.size() == 0);

		} catch (NextProtException e) {
			if (e.getMessage().contains("The amino acid ")) {
				throw e; // success tests
			} else
				fail();
		}
	}

	private Annotation getMockedAnnotation(String original, String variant, int position, String isoName, boolean isAnnotationPositionalForIso) {

		Annotation a1 = mock(Annotation.class, Mockito.RETURNS_DEEP_STUBS);
		when(a1.getVariant().getOriginal()).thenReturn(original);
		when(a1.getVariant().getVariant()).thenReturn(variant);
		when(a1.getStartPositionForIsoform(isoName)).thenReturn(position);
		when(a1.getEndPositionForIsoform(isoName)).thenReturn(position);
		when(a1.isAnnotationPositionalForIsoform(isoName)).thenReturn(isAnnotationPositionalForIso);
		return a1;

	}

}