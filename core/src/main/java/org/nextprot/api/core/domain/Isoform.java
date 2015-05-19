package org.nextprot.api.core.domain;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nextprot.api.commons.bio.DescriptorMass;
import org.nextprot.api.commons.bio.DescriptorPI;
import org.nextprot.api.core.utils.NXVelocityUtils;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.Collection;


public class Isoform implements Serializable {

	private static final long serialVersionUID = -4837367264809500204L;

	private final static Log LOGGER = LogFactory.getLog(Isoform.class);

	private String sequence;

	private String md5;
	
	private String uniqueName;

	private boolean swissProtDisplayedIsoform;

	private IsoformEntityName mainEntityName;

	private Collection<IsoformEntityName> synonyms;
	
	@Deprecated
	public String getIsoelectricPointAsString() {
		Double d = DescriptorPI.compute(sequence);
		DecimalFormat df = new DecimalFormat("#.##");
		return df.format(d);
	}

	@Deprecated
	public String getMassAsString() {
		try {
			Double d = DescriptorMass.compute(sequence);
			return String.valueOf(Math.round(d));
		} catch (Throwable e) {
			LOGGER.error("Error computing molecular mass of isoform " + uniqueName, e);
			return "0";
		}
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

	@Deprecated
	public String formatIsoformId() {

		return NXVelocityUtils.formatIsoformId(this);
	}
}
