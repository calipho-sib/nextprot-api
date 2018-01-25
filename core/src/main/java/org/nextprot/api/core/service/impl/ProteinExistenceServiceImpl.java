package org.nextprot.api.core.service.impl;

import org.nextprot.api.core.dao.ProteinExistenceDao;
import org.nextprot.api.core.domain.ProteinExistence;
import org.nextprot.api.core.domain.ProteinExistences;
import org.nextprot.api.core.service.ProteinExistenceInferenceService;
import org.nextprot.api.core.service.ProteinExistenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
class ProteinExistenceServiceImpl implements ProteinExistenceService {

	@Autowired
	private ProteinExistenceDao proteinExistenceDao;

	@Autowired
	private ProteinExistenceInferenceService proteinExistenceInferenceService;

	@Override
	@Cacheable("protein-existences")
	public ProteinExistences getProteinExistences(String entryAccession) {

		ProteinExistences proteinExistences = new ProteinExistences();

		proteinExistences.setEntryAccession(entryAccession);

		proteinExistences.setProteinExistenceInferred(proteinExistenceInferenceService.inferProteinExistence(entryAccession));
		proteinExistences.setOtherProteinExistenceNexprot1(proteinExistenceDao.findProteinExistenceUniprot(entryAccession,
				ProteinExistence.Source.PROTEIN_EXISTENCE_NEXTPROT1));
		proteinExistences.setOtherProteinExistenceUniprot(proteinExistenceDao.findProteinExistenceUniprot(entryAccession,
				ProteinExistence.Source.PROTEIN_EXISTENCE_UNIPROT));

		return proteinExistences;
	}
}
