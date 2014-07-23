package org.nextprot.api.core.domain;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.Collection;

import org.nextprot.api.commons.bio.DescriptorMass;
import org.nextprot.api.commons.bio.DescriptorPI;


public class Isoform implements Serializable {

	private static final long serialVersionUID = -4837367264809500204L;

	private String sequence;

	private String md5;
	
	private String uniqueName;

	private boolean swissProtDisplayedIsoform;

	private IsoformEntityName mainEntityName;

	private Collection<IsoformEntityName> synonyms;
	
	
	public String getIsoelectricPointAsString() {
		Double d = DescriptorPI.compute(sequence);
		DecimalFormat df = new DecimalFormat("#.##");
		return df.format(d);
	}
	
	public String getMassAsString() {
		Double d = DescriptorMass.compute(sequence);
		return String.valueOf(Math.round(d));		
	}
	
	public String getMd5() {
		return md5;
	}

	public void setMd5(String md5) {
		this.md5 = md5;
	}

	public String getUniqueName() {
		return uniqueName;
	}

	public void setUniqueName(String uniqueName) {
		this.uniqueName = uniqueName;
	}

	public String getSequence() {
		return sequence;
	}

	public int getSequenceLength() {
		return sequence.length();
	}

	public void setSequence(String sequence) {
		this.sequence = sequence;
	}

	public boolean isSwissProtDisplayedIsoform() {
		return swissProtDisplayedIsoform;
	}

	public void setSwissProtDisplayedIsoform(boolean swissProtDisplayedIsoform) {
		this.swissProtDisplayedIsoform = swissProtDisplayedIsoform;
	}

	public IsoformEntityName getMainEntityName() {
		return mainEntityName;
	}

	public void setMainEntityName(IsoformEntityName mainEntityName) {
		this.mainEntityName = mainEntityName;
	}

	public Collection<IsoformEntityName> getSynonyms() {
		return synonyms;
	}

	public void setSynonyms(Collection<IsoformEntityName> synonyms) {
		this.synonyms = synonyms;
	}

}
