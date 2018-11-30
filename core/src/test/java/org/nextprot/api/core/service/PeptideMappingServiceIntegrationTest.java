package org.nextprot.api.core.service;

import org.junit.Ignore;
import org.junit.Test;
import org.nextprot.api.core.domain.Isoform;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.domain.annotation.AnnotationProperty;
import org.nextprot.api.core.test.base.CoreUnitBaseTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertTrue;

@ActiveProfiles({ "dev","cache" })
public class PeptideMappingServiceIntegrationTest extends CoreUnitBaseTest {

	@Autowired
	private PeptideMappingService pmService;
	@Autowired
	private IsoformService isoService;

	private static final String COLOR_NOT_COVERED = "grey";
	private static final String COLOR_PEP_COVERED = "blue";
	private static final String COLOR_1_TYPIC_COVERED = "lightgreen";
	private static final String COLOR_N_TYPIC_COVERED = "orange";
	
    @Ignore
	@Test
	public void shouldComputeCoverages() {
		
		//List<String>entryNames = Arrays.asList(new String[]{"NX_Q8IYL9", "NX_Q66K66", "NX_P01308"});
		//List<String>entryNames = miService.findUniqueNamesOfChromosome("Y");
		// liste Monique
		List<String>entryNames = Arrays.asList(new String[]{"NX_Q6UWW9", "NX_Q04118", "NX_P48165","NX_P01308","NX_Q13557"});

		String sep = "\t";
		//System.out.println("Isoform" + sep + "Coverage type"  + sep + "isoLength" + sep + "covered" + sep + "rateRounded");

		
		for (String entryName: entryNames) {
			List<Annotation> annotations = this.pmService.findNaturalPeptideMappingAnnotationsByMasterUniqueName(entryName);
			List<Isoform> isoforms = isoService.findIsoformsByEntryName(entryName);
			computeCoverage(isoforms, annotations, false, true);
			computeCoverage(isoforms, annotations, true, true);
		}
		assertTrue(true);
		
	}
	

    @Ignore
    @Test
	public void shouldComputeHTML4Highlight() throws FileNotFoundException {
		
		//List<String>entryNames = Arrays.asList(new String[]{"NX_Q8IYL9", "NX_Q66K66", "NX_P01308"});
		//List<String>entryNames = miService.findUniqueNamesOfChromosome("Y");
		// liste Monique
		List<String>entryNames = Arrays.asList(new String[]{"NX_Q6UWW9", "NX_Q04118", "NX_P48165","NX_P01308","NX_Q13557"});

		StringBuilder sb = new StringBuilder();
		sb.append("<html><body>\n");
		sb.append("<ul>");
//		sb.append("<span style=\"color:" + chunk.code  + "\">");

		sb.append("<li style=\"color:" + COLOR_NOT_COVERED  + "\">"+ COLOR_NOT_COVERED + ": no peptide</li>");
		sb.append("<li style=\"color:" + COLOR_PEP_COVERED  + "\">"+ COLOR_PEP_COVERED + ": peptide</li>");
		sb.append("<li style=\"color:" + COLOR_1_TYPIC_COVERED  + "\">"+ COLOR_1_TYPIC_COVERED + ": single proteotypic</li>");
		sb.append("<li style=\"color:" + COLOR_N_TYPIC_COVERED  + "\">"+ COLOR_N_TYPIC_COVERED + ": multiple proteotypic</li>");
		sb.append("</ul>");
		
		for (String entryName: entryNames) {
			List<Annotation> annotations = this.pmService.findNaturalPeptideMappingAnnotationsByMasterUniqueName(entryName);
			List<Isoform> isoforms = isoService.findIsoformsByEntryName(entryName);

			for (Isoform iso: isoforms) {
				String cpep = computeCoverage(iso, annotations, false, false);
				String ctyp = computeCoverage(iso, annotations, true, false);
				String html = getHighlightHTML(iso, annotations, cpep, ctyp);
				sb.append(html);
			}
		}
		sb.append("</body></html>\n");
		String filename = "./highlight-coverage-peptide-chromosome-y.html";
		PrintWriter out = new PrintWriter(filename);
		out.print(sb.toString());
		out.close();
	    //System.out.println("Wrote result in file " + filename);
	}
	
	
	private String computeCoverage(Isoform iso, List<Annotation> annotations, boolean proteotypic, boolean sysout) {
		String name = iso.getUniqueName();
		int isoLength = iso.getSequenceLength();
		int[] coverage = new int[isoLength];
		for (Annotation annot: annotations) {
			if (proteotypic && ! isAboutProteotypicPeptide(annot)) continue;
			if (! annot.isAnnotationPositionalForIsoform(name)) continue;
			int start = annot.getStartPositionForIsoform(name);
			int end = annot.getEndPositionForIsoform(name);
			for (int i=start;i<=end;i++) coverage[i-1]=1;
		}
		int covered = getCoverageCount(coverage);
		float rate = (float)100.0 * (float)covered / (float)isoLength;
		float rateRounded = Math.round(rate * 100.0f) / 100.0f;
		String sep = "\t";
		String title = proteotypic ? "proteotypic coverage" : "peptide coverage";
		//if (sysout) System.out.println(name + sep + title  + sep + isoLength + sep + covered + sep + rateRounded);
		return (name + " " + title  + " iso-length = " + isoLength + " covered = " + covered + " % : " + rateRounded);
		// System.out.println(getCoverageString(coverage) + "\n");
	}

	
	private void showHighlightString(Isoform iso, List<Annotation> annotations) {
		String name = iso.getUniqueName();
		int isoLength = iso.getSequenceLength();
		int[] coverage = getHighlightCoverage(iso, annotations);
		
		String sep = "\t";
		String title = "Natural highlight";
		//System.out.println(name + sep + title);
		//System.out.println(getHighlightString(coverage) + "\n");
		List<Chunk> chunks = getHighlightChunks(iso.getSequence(),coverage);
//		for (Chunk chunk: chunks) {
//			System.out.println(chunk);
//		}
		String html = getHighlightHTML(name, iso.getSequence(), chunks, "turlu", "chouette");
		//System.out.println(html);
	}

	private String getHighlightHTML(Isoform iso, List<Annotation> annotations, String cpep, String ctyp) {
		String name = iso.getUniqueName();
		int[] coverage = getHighlightCoverage(iso, annotations);
		
		List<Chunk> chunks = getHighlightChunks(iso.getSequence(),coverage);
		String html = getHighlightHTML(name, iso.getSequence(), chunks, cpep, ctyp);
		return html;
	}

	private int[] getHighlightCoverage(Isoform iso, List<Annotation> annotations) {
		String name = iso.getUniqueName();
		int isoLength = iso.getSequenceLength();
		int[] coverage = new int[isoLength];
		// first loop sets aa coverage to 1 if there is a natural peptide on it
		for (Annotation annot: annotations) {
			if (! annot.isAnnotationPositionalForIsoform(name)) continue;
			int start = annot.getStartPositionForIsoform(name);
			int end = annot.getEndPositionForIsoform(name);
			//System.out.println("first loop: " + start + " " + end);
			for (int i=start;i<=end;i++) coverage[i-1]=1;
		}
		// second loop  increments aa coverage if peptide is natural and proteotypic
		for (Annotation annot: annotations) {
			if (! annot.isAnnotationPositionalForIsoform(name)) continue;
			if (isAboutProteotypicPeptide(annot)) {
				int start = annot.getStartPositionForIsoform(name);
				int end = annot.getEndPositionForIsoform(name);
				//System.out.println("second loop: " + start + " " + end);
				for (int i=start;i<=end;i++) coverage[i-1]=coverage[i-1]+1;
			}
		}
		return coverage;
	}

	
	private boolean isAboutProteotypicPeptide(Annotation annot) {
		boolean flag = false;
		for (AnnotationProperty p:annot.getProperties()) {
			if (p.getName().equals("is proteotypic") && p.getValue().equals("Y")) flag = true;
		}
		return flag;
	}
	
	
	private void computeCoverage(List<Isoform> isoforms, List<Annotation> annotations, boolean proteotypic, boolean sysout) {
		for (Isoform iso: isoforms) {
			computeCoverage( iso, annotations, proteotypic, sysout);
		}
	}
	
	private void computeHighlights(List<Isoform> isoforms, List<Annotation> annotations) {
		for (Isoform iso: isoforms) {
			showHighlightString(iso, annotations);
		}
	}
	
	private String getHighlightString(int[] coverage) {
		StringBuilder sb = new StringBuilder();
		for (int i=0;i<coverage.length;i++) {
			if (i>0 && i % 10 ==0) sb.append(" ");
			if (i>0 && i % 100 ==0) sb.append("\n");
			int val = coverage[i];
			String code = "?";
			if (val==0) {
				code = ".";
			} else if (val==1) {
				code = "+" ;
			} else if (val==2) {
				code = "1" ;
			} else if (val>2) { 
				code = "N";
			}
			sb.append(code);
		}
		return sb.toString();
	}

	private String getHighlightHTML(String iso, String seq, List<Chunk> chunks,String cpep, String ctyp) {
		StringBuilder sb = new StringBuilder();
		String entry = iso.split("-")[0];
		sb.append("<h3><a target=\"_blank\" href=\"http://alpha-search.nextprot.org/entry/" + entry + "/view/peptides\">" + iso + "</a></h3>\n");
		sb.append("<p>"+ cpep + "</p>");
		sb.append("<p>"+ ctyp + "</p>");
		int oldLine=-1;
		for (Chunk chunk: chunks) {
			if (oldLine<chunk.line) {
				if (oldLine!=-1) sb.append("</div>\n");
				sb.append("<div style=\"font-family: monospace;\">\n");
				oldLine=chunk.line;
			}
			sb.append("<span style=\"color:" + chunk.code  + "\">");
			sb.append(chunk.str);
			sb.append("</span>");
		}
		sb.append("</div>\n");
		return sb.toString();
		
	}
	
	private List<Chunk> getHighlightChunks(String seq, int[] coverage) {
		List<Chunk> chunks = new ArrayList<>();
		StringBuilder sb = new StringBuilder();
		String oldCode="init";
		String code = "?";
		int line=0;
		
		for (int i=0;i<coverage.length;i++) {
			
			// compute code value
			
			int val = coverage[i];
			if (val==0) {
				code = COLOR_NOT_COVERED;
			} else if (val==1) {
				code = COLOR_PEP_COVERED ;
			} else if (val==2) {
				code = COLOR_1_TYPIC_COVERED ;
			} else if (val>2) { 
				code = COLOR_N_TYPIC_COVERED;
			}
			
			// when code changes
			
			if (! oldCode.equals(code)) {
				
				// save previous chunk
				
				if (! oldCode.equals("init")) {
					chunks.add(new Chunk(sb.toString(), oldCode, line));
				}
				
				// reinit buffer and update oldCode
				
				sb = new StringBuilder();
				oldCode = code;
			}
			
			// append content to buffer
			
			if (i>0 && i % 10 ==0) sb.append(" ");
			if (i>0 && i % 100 ==0) {
				// force end of chunk at for end of line
				chunks.add(new Chunk(sb.toString(), code, line));
				sb=new StringBuilder();
				line++;
			}
			sb.append(seq.charAt(i));

		}
		if (sb.length()>0) {
			chunks.add(new Chunk(sb.toString(), code, line));			
		}
		
		return chunks;
	}

	private String getCoverageString(int[] coverage) {
		StringBuilder sb = new StringBuilder();
		for (int i=0;i<coverage.length;i++) {
			if (i>0 && i % 10 ==0) sb.append(" ");
			if (i>0 && i % 100 ==0) sb.append("\n");
			sb.append(coverage[i]==0 ? "0" : "1");
		}
		return sb.toString();
	}

	
	private int getCoverageCount(int[] coverage) {
		int count=0;
		for (int i=0;i<coverage.length;i++) {
			if (coverage[i]==1) count++;	
		}
		return count;
	}
	
	private static class Chunk {
		String str;
		String code;
		int line;
		public Chunk(String str, String code, int line) {
			this.str=str;
			this.code=code;
			this.line=line;
		}
		public String toString() {
			return line + "\t" + code + "\t" + str;
		}
	}
	
}
