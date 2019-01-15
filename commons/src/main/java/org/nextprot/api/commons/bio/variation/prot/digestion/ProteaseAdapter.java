package org.nextprot.api.commons.bio.variation.prot.digestion;

import org.expasy.mzjava.proteomics.mol.digest.Protease;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * An adapter to mzJava Protease with fixed typos
 */
public class ProteaseAdapter {

	public Protease getProtease(String proteaseName) {

		if ("PEPSIN_PH_1_3".equals(proteaseName)) {
			return Protease.PEPSINE_PH_1_3;
		}
		else if ("PEPSIN_PH_GT_2".equals(proteaseName)) {
			return Protease.PEPSINE_PH_GT_2;
		}
		else if ("THERMOLYSIN".equals(proteaseName)) {
			return Protease.THERMOLYSINE;
		}
		return Protease.valueOf(proteaseName);
	}

	public String getProteaseName(Protease protease) {

		if (protease == Protease.PEPSINE_PH_1_3) {
			return "PEPSIN_PH_1_3";
		}
		else if (protease == Protease.PEPSINE_PH_GT_2) {
			return "PEPSIN_PH_GT_2";
		}
		else if (protease == Protease.THERMOLYSINE) {
			return "THERMOLYSIN";
		}
		return protease.name();
	}

	public List<String> getProteaseNames() {

		return Stream.of(Protease.values())
				.map(protease -> new ProteaseAdapter().getProteaseName(protease))
				.sorted()
				.collect(Collectors.toList());
	}
}
