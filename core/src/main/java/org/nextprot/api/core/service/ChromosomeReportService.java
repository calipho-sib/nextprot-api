package org.nextprot.api.core.service;

import org.nextprot.api.commons.exception.ChromosomeNotFoundException;
import org.nextprot.api.core.domain.ChromosomeReport;

import java.util.List;


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
}
