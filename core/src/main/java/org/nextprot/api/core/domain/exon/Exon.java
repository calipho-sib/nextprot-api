package org.nextprot.api.core.domain.exon;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.nextprot.api.core.domain.AminoAcid;
import org.nextprot.api.core.domain.GeneRegion;

import java.io.Serializable;

public interface Exon extends Serializable {

	String getName();

	String getAccession();

	String getTranscriptName();

	@JsonIgnore
	String getIsoformName();

	GeneRegion getGeneRegion();

	AminoAcid getFirstAminoAcid();

	AminoAcid getLastAminoAcid();

	int getRank();

	int getFirstPositionOnGene();

	int getLastPositionOnGene();
}
