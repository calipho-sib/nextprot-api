package org.nextprot.api.core.domain;

import org.jsondoc.core.annotation.ApiObject;
import org.jsondoc.core.annotation.ApiObjectField;
import org.nextprot.api.core.utils.ChromosomalLocationComparator;

import java.io.Serializable;
import java.text.ParseException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@ApiObject(name = "chromosomal-location", description = "The chromosomal location")
public class ChromosomalLocation implements Serializable {

	private static final long serialVersionUID = 2L;
	private static final Pattern CHROMOSOMAL_POSITION_PATTERN = Pattern.compile("^([^qp]+)([pq].*)?$");

	@ApiObjectField(description = "The chromosome identifier")
	private String chromosome;

	@ApiObjectField(description = "The band")
	private String band;

	@ApiObjectField(description = "The strand")
	private int strand;

	@ApiObjectField(description = "The accession code")
	private String accession;

	@ApiObjectField(description = "The first position on the gene")
	private int firstPosition;

	@ApiObjectField(description = "The last position on the gene")
	private int lastPosition;

	// name of the gene in sequence_identifier given by ENSEMBL 
	// most of the time (when gene_identifier.unique_name like 'NX_ENSG...'
	// see CALIPHOMISC-154
	private String displayName;
	
	// UniProt recommended name of genes associated to master separated with space
	// see CALIPHOMISC-154
	private String masterGeneNames;

	// UniProt recommended name of genes associated to genes separated with space
	private String geneGeneNames;

	
	// quality of the nextprot alignment gene - isoform (null=GOLD, SILVER)
	private boolean goldMapping;

	
	private boolean isBestGeneLocation;
	
	private String recommendedName;

	public void setGeneGeneNames(String geneGeneNames) {
		
		this.geneGeneNames =sortNames(geneGeneNames);
		
	}

	public boolean isGoldMapping() {
		return goldMapping;
	}

	public void setMappingQuality(String quality) {
		if (null==quality) quality = "GOLD";
		this.goldMapping = "GOLD".equals(quality);
	}
	
	
	public boolean isBestGeneLocation() {
		return isBestGeneLocation;
	}

	public void setBestGeneLocation(boolean isBestGeneLocation) {
		this.isBestGeneLocation = isBestGeneLocation;
	}

	public void setDisplayName(String displayName) {
		this.displayName=displayName;
	}

	public void setMasterGeneNames(String masterGeneNames) {
		
		this.masterGeneNames=sortNames(masterGeneNames);
		
	}

	private String sortNames(String names) {

		if (names==null) return null;
		
		return Arrays.stream(names.split(" "))
			.sorted()
			.collect(Collectors.joining(" "));
		
	}
	
	/**
	 * Still buggy in some cases, needs fix from Anne, see CALIPHOMISC-154
	 * - recommended & alternative gene names should be propertly attached to gene
	 * - they are currently attached to the master 
	 * - the display name of the gene sequence identifier is sometimes wrong
	 * @return the recommended gene name
	 */
	private String getRecommendedNameOld() {
		if (displayName==null && masterGeneNames==null) return "unknown"; // (about 300 cases)
		if (masterGeneNames==null) return displayName; // (0 cases but... who knows)
		String[] mgnList=masterGeneNames.split(" "); // 
		// if no display name, choose arbitrarily first master gene name
		if (displayName==null) return mgnList[0]; // (about 230 cases)
		// if master gene names contains several elements, choose the one matching the display name 
		for (String gn:mgnList) if (gn.equals(displayName)) return displayName; // (about 18900 cases)
		// it no match is found choose arbitrarily the fist master gene name 
		return mgnList[0]; 
	}
	
	/**
	 * Inspired from code in NP1 - same heuristics
	 * @return
	 */
	public String getRecommendedName() {
		
		if (this.recommendedName==null) this.recommendedName = calcRecommendedName();
		return this.recommendedName; 
				
	}

	public void setRecommendedName(String recommendedName) {
		this.recommendedName = recommendedName;
	}

	@Deprecated
	public String getRecommendedName(String version) {
		
		return "old".equals(version) ? getRecommendedNameOld() : getRecommendedName();
				
	}
	
	
	private String calcRecommendedName() {

		// case 1 - no UniProt recommended gene name in entry => return unknown ! 
		if (masterGeneNames==null) return "unknown";

		// case 2 - 1 UniProt recommended gene name in entry => return it
		String[] mgNames = masterGeneNames.split(" ");
		if (mgNames.length==1) return mgNames[0];
		
		// case 3 - multiple UniProt recommended gene names in entry => find intersection with mapped gene recommended names
		String[] ggNames = geneGeneNames==null ? new String[]{} : geneGeneNames.split(" ");
		for (String mgName: mgNames) {
			for (String ggName: ggNames) {
				if (mgName.equals(ggName)) return mgName;
			}
		}
		
		// case 4 - otherwise choose first recommended gene name in entry
		return mgNames[0];
		
	}

	
	public String getChromosome() {
		return chromosome;
	}

	public void setChromosome(String chromosome) {
		this.chromosome = chromosome;
	}

	public String getBand() {
		return band;
	}

	public void setBand(String band) {
		this.band = band;
	}

	public int getStrand() {
		return strand;
	}

	public void setStrand(int strand) {
		this.strand = strand;
	}

	public String getAccession() {
		return accession;
	}

	public void setAccession(String accession) {
		if (accession.startsWith("NX_")) { // TODO review this NX_
			this.accession = accession.substring(3);
		} else
			this.accession = accession;
	}

	public int getFirstPosition() {
		return firstPosition;
	}

	public void setFirstPosition(int firstPosition) {
		this.firstPosition = firstPosition;
	}

	public int getLastPosition() {
		return lastPosition;
	}

	public int getLength() {
		return lastPosition-firstPosition;
	}
	
	public void setLastPosition(int lastPosition) {
		this.lastPosition = lastPosition;
	}

	public static String toString(List<ChromosomalLocation> locations) {

		if (locations != null) {

            Set<ChromosomalLocation> chromosomalLocations = new TreeSet<>(new ChromosomalLocationComparator());
            chromosomalLocations.addAll(locations);

            return chromosomalLocations.stream()
					.map(cl -> toString(cl))
					.collect(Collectors.joining(", "));
		}

		return "";
	}

	public String getPrettyLocation() {

		return toString(this);
	}

	public static String toString(ChromosomalLocation chromosomalLocation) {

		StringBuilder sb = new StringBuilder();

		String chromosome = chromosomalLocation.getChromosome();
		String band = chromosomalLocation.getBand();

		if (chromosome != null && !"unknown".equals(chromosome)) {
			sb.append(chromosome);
		}
		if (band != null && !"unknown".equals(band)) {
			sb.append(band);
		}

		if (sb.length() == 0) {
			sb.append("unknown");
		}

		return sb.toString();
	}

	public static ChromosomalLocation fromString(String chromosomalPosition) throws ParseException {

		// 1q21.1 or -
		ChromosomalLocation chromosomalLocation = new ChromosomalLocation();

		if (chromosomalPosition != null && !"-".equals(chromosomalPosition) && !"unknown".equals(chromosomalPosition)) {

			Matcher matcher = CHROMOSOMAL_POSITION_PATTERN.matcher(chromosomalPosition);

			if (matcher.find()) {

				chromosomalLocation.setChromosome(matcher.group(1));
				chromosomalLocation.setBand((matcher.group(2) != null) ? matcher.group(2) : "unknown");
			}
			else {
				throw new ParseException("cannot parse chromosomal position "+chromosomalPosition, -1);
			}
		}
		else {
			chromosomalLocation.setChromosome("unknown");
			chromosomalLocation.setBand("unknown");
		}

		return chromosomalLocation;
	}

	public static class ByChromosomalBandLocationComparator implements Comparator<ChromosomalLocation> {

		@Override
		public int compare(ChromosomalLocation cl1, ChromosomalLocation cl2) {

			String band1 = cl1.getBand();
			String band2 = cl2.getBand();

			return comparePosition(band1, band2, "unknown",
					new DefinedBandComparator());
		}

		private static class DefinedBandComparator implements Comparator<String> {

			@Override
			public int compare(String band1, String band2) {

				// arm: (p)etit or (q)ueue
				char arm1 = band1.charAt(0);
				char arm2 = band2.charAt(0);

				int cmp = arm1-arm2;

				if (cmp == 0) {

					// compare the first band for interval
					if (band1.contains("-")) {
						band1 = band1.split("-")[0];
					}
					if (band2.contains("-")) {
						band2 = band2.split("-")[0];
					}

					cmp = Comparator.comparingDouble(Double::parseDouble).compare(band1.substring(1), band2.substring(1));

					if (arm1 == 'p') {
						cmp = -cmp;
					}
				}

				return cmp;
			}
		}
	}

	static int comparePosition(String pos1, String pos2, String undefinedValue, Comparator<String> comparator) {

		boolean pos1IsUndefined = undefinedValue.equals(pos1);
		boolean pos2IsDefined = undefinedValue.equals(pos2);

		if (pos1IsUndefined && pos2IsDefined) {
			return 0;
		}
		else if (pos1IsUndefined) {
			return 1;
		}
		else if (pos2IsDefined) {
			return -1;
		}

		return comparator.compare(pos1, pos2);
	}
}
