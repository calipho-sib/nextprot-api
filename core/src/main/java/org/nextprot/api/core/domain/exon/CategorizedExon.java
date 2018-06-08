package org.nextprot.api.core.domain.exon;

import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.core.domain.AminoAcid;
import org.nextprot.api.core.domain.GeneRegion;

public abstract class CategorizedExon implements Exon {

	private Exon exon;
	private ExonCategory exonCategory;

	public CategorizedExon(Exon exon, ExonCategory exonCategory) {

		this.exon = exon;
		this.exonCategory = exonCategory;
	}

	public static CategorizedExon valueOf(ExonCategory exonCategory, Exon exon, int startPositionIsoformOnGene, int endPositionIsoformOnGene) {

        switch (exonCategory) {

            case START:
                return new ExonStart(exon, startPositionIsoformOnGene);
            case STOP:
                return new ExonStop(exon, endPositionIsoformOnGene);
            case CODING:
                return new CodingExon(exon);
            case MONO:
                return new ExonMono(exon, startPositionIsoformOnGene, endPositionIsoformOnGene);
            case NOT_CODING:
            case STOP_ONLY:
                return new NotCodingExon(exon, exonCategory);
            default:
                throw new NextProtException("unknown exon category "+exonCategory);
        }
    }

	public ExonCategory getExonCategory() {
		return exonCategory;
	}

	@Override
	public String getName() {
		return exon.getName();
	}

	@Override
	public String getAccession() {
		return exon.getAccession();
	}

	@Override
	public String getTranscriptName() {
		return exon.getTranscriptName();
	}

    @Override
    public String getIsoformName() {
        return exon.getIsoformName();
    }

    @Override
	public GeneRegion getGeneRegion() {
		return exon.getGeneRegion();
	}

    /**
     * @return the coding gene region or null if non coding
     */
    abstract public GeneRegion getCodingGeneRegion();

    public boolean isCodingExon() {

        return exonCategory.isCoding();
    }

    @Override
	public AminoAcid getFirstAminoAcid() {
		return exon.getFirstAminoAcid();
	}

	@Override
	public AminoAcid getLastAminoAcid() {
		return exon.getLastAminoAcid();
	}

	@Override
	public int getRank() {
		return exon.getRank();
	}

	@Override
	public int getFirstPositionOnGene() {
		return exon.getFirstPositionOnGene();
	}

	@Override
	public int getLastPositionOnGene() {
		return exon.getLastPositionOnGene();
	}
}
