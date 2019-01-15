package org.nextprot.api.commons.bio.variation.prot.digestion;

import org.expasy.mzjava.proteomics.mol.digest.LengthDigestionController;
import org.expasy.mzjava.proteomics.mol.digest.Protease;
import org.expasy.mzjava.proteomics.mol.digest.ProteinDigester;
import org.nextprot.api.commons.exception.NextProtException;

public class ProteinDigesterBuilder {

	private Protease protease = Protease.TRYPSIN;
	private int minpeplen = 7;
	private int maxpeplen = 77;
	private int missedCleavageCount = 2;
	private boolean maturePartsOnly = true;

	public ProteinDigesterBuilder proteaseName(String proteaseName) {
		try {
			this.protease = new ProteaseAdapter().getProtease(proteaseName.toUpperCase());
		}
		catch(IllegalArgumentException e) {

			throw new NextProtException("unknown protease name: "+proteaseName + " (available proteases="+new ProteaseAdapter().getProteaseNames()+")");
		}
		return this;
	}

	public ProteinDigesterBuilder minPepLen(int minpeplen) {
		this.minpeplen = minpeplen;
		return this;
	}

	public ProteinDigesterBuilder maxPepLen(int maxpeplen) {
		if (maxpeplen <= 0) {
			throw new NextProtException("max peptide length should be greater than 1.");
		}
		this.maxpeplen = maxpeplen;
		return this;
	}

	public ProteinDigesterBuilder maxMissedCleavageCount(int missedCleavageCount) {
		if (missedCleavageCount < 0) {
			throw new NextProtException("number of missed cleavages should be positive.");
		}
		if (missedCleavageCount > 2) {
			throw new NextProtException(missedCleavageCount+" missed cleavages is too high: cannot configure digestion with more than 2 missed cleavages.");
		}
		this.missedCleavageCount = missedCleavageCount;
		return this;
	}

	public ProteinDigesterBuilder withMaturePartsOnly(boolean maturePartsOnly) {
		this.maturePartsOnly = maturePartsOnly;
		return this;
	}

	public boolean withMaturePartsOnly() {
		return maturePartsOnly;
	}

	public ProteinDigester build() {

		return new ProteinDigester.Builder(protease)
				.controller(new LengthDigestionController(minpeplen, maxpeplen))
				.missedCleavageMax(missedCleavageCount)
				.build();
	}
}
