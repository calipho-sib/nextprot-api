package org.nextprot.api.domain;

import java.io.Serializable;

import org.jsondoc.core.annotation.ApiObject;
import org.jsondoc.core.annotation.ApiObjectField;

@ApiObject(name = "chromosomal-location", description = "The chromosomal location")
public class ChromosomalLocation implements Serializable {

	private static final long serialVersionUID = -582666549875804789L;

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

	// name of the gene in sequence_identifier (not always displayed in fact)
	// see CALIPHOMISC-154
	private String displayName;
	
	// names of the "gene name" synonyms of the master (sometimes displayed) separated with comma
	// see CALIPHOMISC-154
	private String masterGeneNames;
	
	public void setDisplayName(String displayName) {
		this.displayName=displayName;
	}

	public void setMasterGeneNames(String masterGeneNames) {
		this.masterGeneNames=masterGeneNames;
	}
	
	/**
	 * Still buggy in some cases, needs fix from Anne, see CALIPHOMISC-154
	 * - recommended & alternative gene names should be propertly attached to gene
	 * - they are currently attached to the master 
	 * - the display name of the gene sequence identifier is sometimes wrong
	 * @return the recommended gene name
	 */
	public String getRecommendedName() {
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

}
