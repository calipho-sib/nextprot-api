package org.nextprot.api.core.service;

import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.core.domain.ChromosomeReport;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public interface ChromosomeReportService {

	/**
	 * Report informations about neXtProt entries and genes located on the given chromosome
	 * @param chromosome the chromosome to get report
	 * @return a pojo containing the report
	 * @throws ChromosomeNotFoundException should be thrown if {@code chromosome} was not found in neXtProt
	 */
	ChromosomeReport reportChromosome(String chromosome);

	List<String> findNAcetylatedEntries(String chromosome);

	List<String> findPhosphorylatedEntries(String chromosome);

	List<String> findUnconfirmedMsDataEntries(String chromosome);

	/**
	 * @return the list of chromosomes existing in neXtProt
	 */
	static List<String> getChromosomeNames() {

		List<String> list = IntStream.rangeClosed(1, 22).boxed()
				.map(String::valueOf)
				.collect(Collectors.toList());

		list.addAll(Arrays.asList("X", "Y", "MT", "unknown"));

		return list;
	}

	class ChromosomeNotFoundException extends NextProtException {

		private static final long serialVersionUID = 1L;

		private final String chromosome;

		public ChromosomeNotFoundException(String chromosome, String expectedChromosomes) {

			super("Chromosome '" + chromosome + "' was not found. Give a valid name among "+expectedChromosomes);

			this.chromosome = chromosome;
		}

		public String getChromosome() {

			return chromosome;
		}
	}
}
