package org.nextprot.api.core.service;

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
	 */
	ChromosomeReport reportChromosome(String chromosome);

	/**
	 * @return the list of chromosomes existing in neXtProt
	 */
	default List<String> getChromosomes() {

		List<String> list = IntStream.range(1, 22).boxed()
				.map(String::valueOf)
				.collect(Collectors.toList());

		list.addAll(Arrays.asList("X", "Y", "MT", "unknown"));

		return list;
	}
}
