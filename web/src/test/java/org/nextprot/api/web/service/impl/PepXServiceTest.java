package org.nextprot.api.web.service.impl;

import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.core.domain.Isoform;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.web.dbunit.base.mvc.WebUnitBaseTest;
import org.nextprot.api.web.domain.PepXResponse;
import org.nextprot.api.web.domain.PepXResponse.PepXEntryMatch;
import org.nextprot.api.web.domain.PepXResponse.PepXIsoformMatch;
import org.nextprot.api.web.domain.PepXResponse.PepXMatch;
import org.nextprot.api.web.domain.PepxUtils;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PepXServiceTest extends WebUnitBaseTest {

	private static final String ISO_ACCESSION = "NX_P01234-1";
	
	
	@Test
	public void testFilteringOfVariantMatches() throws Exception {
		
		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource("org/nextprot/api/pepx/three-matches-pepx-response.json").getFile());
		Scanner scanner = new Scanner(file, "UTF-8");
		String content = scanner.useDelimiter("\\A").next();
		scanner.close();
		PepXResponse out = PepxUtils.parsePepxResponse(content);
		
		// content before filtering
		assertTrue(out.getParams().get("peplist").equals("MAPGGA,MAPGGP,TGGSTGSS"));
		assertTrue(out.getParams().get("modeIL").equals(1));
		assertTrue(out.getPeptideMatch("MAPGGA").getEntryMatches().size()==3);
		assertTrue(out.getPeptideMatch("MAPGGP").getEntryMatches().size()==3);
		assertTrue(out.getPeptideMatch("MAPGGP").getEntryMatches().get(2).getIsoforms().size()==8); 
		assertTrue(out.getPeptideMatch("TGGSTGSS").getEntryMatches().size()==0);

		// - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
		out = PepxUtils.filterOutVariantMatch(out);
		// - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
		
		// content after filtering		
		assertTrue(out.getParams().get("peplist").equals("MAPGGA,MAPGGP,TGGSTGSS"));
		assertTrue(out.getParams().get("modeIL").equals(1));
		assertTrue(out.getPeptideMatch("MAPGGA").getEntryMatches().size()==1);  // instead of 3 before
		assertTrue(out.getPeptideMatch("MAPGGP").getEntryMatches().size()==3);
		assertTrue(out.getPeptideMatch("TGGSTGSS").getEntryMatches().size()==0);
		
		// check detailed content
		assertTrue(out.getPeptideMatch("MAPGGA").getEntryMatches().get(0).getEntryName().equals("P28799"));
		assertTrue(out.getPeptideMatch("MAPGGA").getEntryMatches().get(0).getIsoforms().size()==1);
		assertTrue(out.getPeptideMatch("MAPGGA").getEntryMatches().get(0).getIsoforms().get(0).getIsoformAccession().equals("NX_P28799-3"));
		assertTrue(out.getPeptideMatch("MAPGGA").getEntryMatches().get(0).getIsoforms().get(0).getPosition()==null);
		
		assertTrue(out.getPeptideMatch("MAPGGP").getEntryMatches().get(0).getEntryName().equals("A0A0J9YX94"));
		assertTrue(out.getPeptideMatch("MAPGGP").getEntryMatches().get(0).getIsoforms().size()==1);
		assertTrue(out.getPeptideMatch("MAPGGP").getEntryMatches().get(0).getIsoforms().get(0).getIsoformAccession().equals("NX_A0A0J9YX94-1"));
		assertTrue(out.getPeptideMatch("MAPGGP").getEntryMatches().get(0).getIsoforms().get(0).getPosition()==null);
		
		assertTrue(out.getPeptideMatch("MAPGGP").getEntryMatches().get(1).getEntryName().equals("Q7L2E3"));
		assertTrue(out.getPeptideMatch("MAPGGP").getEntryMatches().get(1).getIsoforms().size()==1);
		assertTrue(out.getPeptideMatch("MAPGGP").getEntryMatches().get(1).getIsoforms().get(0).getIsoformAccession().equals("NX_Q7L2E3-2"));
		assertTrue(out.getPeptideMatch("MAPGGP").getEntryMatches().get(1).getIsoforms().get(0).getPosition()==null);
		
		assertTrue(out.getPeptideMatch("MAPGGP").getEntryMatches().get(2).getEntryName().equals("Q86UU0"));
		assertTrue(out.getPeptideMatch("MAPGGP").getEntryMatches().get(2).getIsoforms().size()==4);  // instead of 8 before
		assertTrue(out.getPeptideMatch("MAPGGP").getEntryMatches().get(2).getIsoforms().get(0).getIsoformAccession().equals("NX_Q86UU0-1"));
		assertTrue(out.getPeptideMatch("MAPGGP").getEntryMatches().get(2).getIsoforms().get(0).getPosition()==null);
		
		assertTrue(out.getPeptideMatch("MAPGGP").getEntryMatches().get(2).getIsoforms().get(1).getIsoformAccession().equals("NX_Q86UU0-2"));
		assertTrue(out.getPeptideMatch("MAPGGP").getEntryMatches().get(2).getIsoforms().get(1).getPosition()==null);
		
		assertTrue(out.getPeptideMatch("MAPGGP").getEntryMatches().get(2).getIsoforms().get(2).getIsoformAccession().equals("NX_Q86UU0-3"));
		assertTrue(out.getPeptideMatch("MAPGGP").getEntryMatches().get(2).getIsoforms().get(2).getPosition()==null);
		
		assertTrue(out.getPeptideMatch("MAPGGP").getEntryMatches().get(2).getIsoforms().get(3).getIsoformAccession().equals("NX_Q86UU0-4"));
		assertTrue(out.getPeptideMatch("MAPGGP").getEntryMatches().get(2).getIsoforms().get(3).getPosition()==null);
		
	}
	
	
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
		pepXIsoformMatch.setIsoformAccession(ISO_ACCESSION);
		
		@SuppressWarnings("unchecked")
		List<Annotation> annotations = mock(List.class);
		Isoform isoform = mock(Isoform.class);
		when(isoform.getIsoformAccession()).thenReturn(ISO_ACCESSION);
		when(isoform.getSequence()).thenReturn("AGANAPA");

		List<Isoform> isoforms = Arrays.asList(isoform);

		List<Annotation> virtualAnnotations = PepXServiceImpl.buildEntryWithVirtualAnnotations(peptide, modeIsoleucine, Arrays.asList(pepXIsoformMatch), annotations, isoforms);
		Annotation annot = virtualAnnotations.get(0);
		assertTrue(annot.getCategory().equals("pepx-virtual-annotation"));
		assertTrue(annot.getVariant() == null);
		assertTrue(annot.getTargetingIsoformsMap().keySet().contains(ISO_ACCESSION));

	}

	@Test
	public void shouldReturnAnEmptyArrayWhenThePeptideIsNotContainedInTheSequence() throws Exception {

		String peptide = "GANAP";
		boolean modeIsoleucine = true;

		PepXIsoformMatch pepXIsoformMatch = new PepXIsoformMatch(ISO_ACCESSION);
		
		
		@SuppressWarnings("unchecked")
		List<Annotation> annotations = mock(List.class);
		Isoform isoform = mock(Isoform.class);
		when(isoform.getIsoformAccession()).thenReturn(ISO_ACCESSION);
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
	 * when(isoform.getIsoformAccession()).thenReturn("Iso-1");
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
		when(isoform.getIsoformAccession()).thenReturn(isoName);
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
		when(isoform.getIsoformAccession()).thenReturn(isoName);
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
	 * when(isoform.getIsoformAccession()).thenReturn(isoName);
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

	@Ignore // because we have variants in nextprot which do not have original aa is not equal to isoform aa at that variant position (inconsistency)
	@Test(expected = NextProtException.class)
	public void shouldGiveAnExceptionIfTheOriginalIsNotPresentOnTheSequence() throws Exception {
		try {

			// Taking example NX_Q9H6T3
			String peptide = "GANAP";
			boolean modeIsoleucine = true;
			String isoName = "NX_Q9H6T3-3";

			Isoform isoform = mock(Isoform.class);
			when(isoform.getIsoformAccession()).thenReturn(isoName);
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
			when(iso1.getIsoformAccession()).thenReturn("another-iso-name");
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
		List<Annotation> annots = Arrays.asList(getMockedAnnotation("E", "D", 5, ISO_ACCESSION, true));
		List<Annotation> resultAnnots = PepXServiceImpl.filterValidVariantAnnotations("DDF", true, annots, ISO_ACCESSION, "ABCDEFGHI");

		assertTrue(resultAnnots.size() == 1);
	}

	@Test
	public void shouldNotReturnAValidAnnotationIfItIsNotSpecificToTheIsoform() throws Exception {
		List<Annotation> annots = Arrays.asList(getMockedAnnotation("E", "D", 5, ISO_ACCESSION, true));
		List<Annotation> resultAnnots = PepXServiceImpl.filterValidVariantAnnotations("DDF", true, annots, "another-iso-name", "ABCDEFGHI");
		assertTrue(resultAnnots.size() == 0);
	}

	@Test
	public void shouldNotReturnAValidAnnotationIfTheVariantIsNotContainedInThePeptide() throws Exception {
		List<Annotation> annots = Arrays.asList(getMockedAnnotation("E", "Z", 4, ISO_ACCESSION, true));
		List<Annotation> resultAnnots = PepXServiceImpl.filterValidVariantAnnotations("DDF", true, annots, ISO_ACCESSION, "ABCDEFGHI");
		assertTrue(resultAnnots.size() == 0);
	}

	@Ignore // because we have variants in nextprot which do not have original aa is not equal to isoform aa at that variant position (inconsistency)
	@Test(expected = NextProtException.class)
	public void shouldThrowAnExceptionIfTheOriginalAminoAcidIsNotInTheSequenceAtThatPosition() throws Exception {
		try {
			List<Annotation> annots = Arrays.asList(getMockedAnnotation("E", "D", 4, ISO_ACCESSION, true));
			List<Annotation> resultAnnots = PepXServiceImpl.filterValidVariantAnnotations("DDF", true, annots, ISO_ACCESSION, "ABCDEFGHI");
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