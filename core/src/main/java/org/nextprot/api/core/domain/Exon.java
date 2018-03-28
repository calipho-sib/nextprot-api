package org.nextprot.api.core.domain;

import org.nextprot.api.core.utils.exon.ExonCategory;

public interface Exon {

	String getName();

	String getAccession();

	String getTranscriptName();

	GeneRegion getGeneRegion();

	AminoAcid getFirstAminoAcid();

	AminoAcid getLastAminoAcid();

	int getRank();

	ExonCategory getExonCategory();

	int getFirstPositionOnGene();

	int getLastPositionOnGene();

}
