package org.nextprot.api.core.domain.exon;

import org.nextprot.api.core.domain.AminoAcid;
import org.nextprot.api.core.domain.GeneRegion;

public interface Exon {

	String getName();

	String getAccession();

	String getTranscriptName();

	GeneRegion getGeneRegion();

	AminoAcid getFirstAminoAcid();

	AminoAcid getLastAminoAcid();

	int getRank();

	int getFirstPositionOnGene();

	int getLastPositionOnGene();
}
