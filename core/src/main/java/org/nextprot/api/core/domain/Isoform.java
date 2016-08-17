package org.nextprot.api.core.domain;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nextprot.api.commons.bio.DescriptorMass;
import org.nextprot.api.commons.bio.DescriptorPI;
import org.nextprot.api.commons.utils.NucleotidePositionRange;
import org.nextprot.api.core.dao.EntityName;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.Collection;
import java.util.List;


public class Isoform implements Serializable {

	private static final long serialVersionUID = -4837367264809500204L;

	private final static Log LOGGER = LogFactory.getLog(Isoform.class);

	private String sequence;

	private String md5;
	
	private String isoformAccession;

	private boolean swissProtDisplayedIsoform;

	private EntityName mainEntityName;

	private Collection<EntityName> synonyms;

	private List<NucleotidePositionRange> masterMapping;
	
	
	
	public List<NucleotidePositionRange> getMasterMapping() {
		return masterMapping;
	}

	public void setMasterMapping(List<NucleotidePositionRange> masterMapping) {
		this.masterMapping = masterMapping;
	}

	@Deprecated
	public String getIsoelectricPointAsString() {
		Double d = DescriptorPI.compute(sequence);
		DecimalFormat df = new DecimalFormat("#.##");
		return df.format(d);
	}

	@Deprecated
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

	public String getIsoformAccession() {
		return isoformAccession;
	}
	
	/**
	 * Use isoform accession
	 * @return
	 */
	@Deprecated
	public String getUniqueName() {
		return isoformAccession;
	}

	/**
	 * Use isoform accession
	 */
	@Deprecated
	public void setUniqueName(String uniqueName) {
		this.isoformAccession = uniqueName;
	}

	public void setIsoformAccession(String isoformAccession) {
		this.isoformAccession = isoformAccession;
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
	
	public boolean isCanonicalIsoform() {
		return this.isSwissProtDisplayedIsoform();
	}

	public void setSwissProtDisplayedIsoform(boolean swissProtDisplayedIsoform) {
		this.swissProtDisplayedIsoform = swissProtDisplayedIsoform;
	}

	public EntityName getMainEntityName() {
		return mainEntityName;
	}

	public void setMainEntityName(EntityName mainEntityName) {
		this.mainEntityName = mainEntityName;
	}

	public Collection<EntityName> getSynonyms() {
		return synonyms;
	}

	public void setSynonyms(Collection<EntityName> synonyms) {
		this.synonyms = synonyms;
	}
}
