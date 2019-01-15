package org.nextprot.api.core.service;

import org.expasy.mzjava.proteomics.mol.digest.LengthDigestionController;
import org.expasy.mzjava.proteomics.mol.digest.Protease;
import org.expasy.mzjava.proteomics.mol.digest.ProteinDigester;
import org.nextprot.api.commons.bio.variation.prot.digestion.ProteaseAdapter;
import org.nextprot.api.commons.exception.NextProtException;

import java.util.List;
import java.util.Set;

public interface DigestionService {

	Set<String> digestProteins(String entryOrIsoformAccession, ProteinDigesterBuilder builder);
	Set<String> digestAllMatureProteinsWithTrypsin();
	List<String> getProteaseNames();

	class ProteinDigesterBuilder {

		private String proteaseName = Protease.TRYPSIN.name();
		private int minpeplen = 7;
		private int maxpeplen = 77;
		private int missedCleavageCount = 2;
		private boolean maturePartsOnly = true;

		public ProteinDigesterBuilder proteaseName(String proteaseName) {
			this.proteaseName = proteaseName.toUpperCase();
			return this;
		}

		public ProteinDigesterBuilder minpeplen(int minpeplen) {
			this.minpeplen = minpeplen;
			return this;
		}

		public ProteinDigesterBuilder maxpeplen(int maxpeplen) {
			if (maxpeplen <= 0) {
				throw new NextProtException("max peptide length should be greater than 1.");
			}
			this.maxpeplen = maxpeplen;
			return this;
		}

		public ProteinDigesterBuilder missedCleavageCount(int missedCleavageCount) {
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

			return new ProteinDigester.Builder(new ProteaseAdapter().getProtease(proteaseName))
					.controller(new LengthDigestionController(minpeplen, maxpeplen))
					.missedCleavageMax(missedCleavageCount)
					.build();
		}
	}
}
