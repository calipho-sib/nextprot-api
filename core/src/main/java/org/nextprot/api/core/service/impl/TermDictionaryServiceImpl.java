package org.nextprot.api.core.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nextprot.api.core.dao.TerminologyDao;
import org.nextprot.api.core.domain.CvTerm;
import org.nextprot.api.core.service.TermDictionaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class TermDictionaryServiceImpl implements TermDictionaryService {

    @Autowired
	private TerminologyDao terminologyDao;

	@Override
	@Cacheable(value = "term-map-by-ontology", sync = true)
	public Map<String, CvTerm> getTermDictionary(String ontology) {

		Map<String, CvTerm> dic = new HashMap<>();
		List<CvTerm> terms = terminologyDao.findTerminologyByOntology(ontology);
		for (CvTerm t: terms) dic.put(t.getAccession(), t);
		return dic;
	}

}
