package org.nextprot.api.core.service.impl;

import org.nextprot.api.core.dao.ProteinExistenceDao;
import org.nextprot.api.core.domain.ProteinExistence;
import org.nextprot.api.core.service.ProteinExistenceCalcService;
import org.nextprot.api.core.service.ProteinExistenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
class ProteinExistenceServiceImpl implements ProteinExistenceService {

	@Autowired
	private ProteinExistenceDao proteinExistenceDao;

	@Autowired
	private ProteinExistenceCalcService proteinExistenceCalcService;

	@Override
	public ProteinExistence getProteinExistence(String entryAccession) {

		return getProteinExistence(entryAccession, ProteinExistence.Source.PROTEIN_EXISTENCE_NEXTPROT2);
	}

	@Override
	public ProteinExistence getProteinExistence(String entryAccession, ProteinExistence.Source source) {

		if (source == ProteinExistence.Source.PROTEIN_EXISTENCE_NEXTPROT2) {

			return proteinExistenceCalcService.calcProteinExistence(entryAccession).getProteinExistence();
		}

		return proteinExistenceDao.findProteinExistenceUniprot(entryAccession, source);
	}
}
