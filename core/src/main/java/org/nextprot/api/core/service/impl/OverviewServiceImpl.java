package org.nextprot.api.core.service.impl;

import org.nextprot.api.core.dao.HistoryDao;
import org.nextprot.api.core.domain.EntityName;
import org.nextprot.api.core.domain.Isoform;
import org.nextprot.api.core.domain.Overview;
import org.nextprot.api.core.domain.Overview.History;
import org.nextprot.api.core.service.EntityNameService;
import org.nextprot.api.core.service.FamilyService;
import org.nextprot.api.core.service.IsoformService;
import org.nextprot.api.core.service.OverviewService;
import org.nextprot.api.core.service.ProteinExistenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.nextprot.api.core.domain.Overview.EntityNameClass.*;


@Service
class OverviewServiceImpl implements OverviewService {

	@Autowired private HistoryDao historyDao;
	@Autowired private EntityNameService entityNameService;
	@Autowired private FamilyService familyService;
	@Autowired private IsoformService isoformService;
	@Autowired private ProteinExistenceService proteinExistenceService;

	@Override
	@Cacheable(value = "overview", sync = true)
	public Overview findOverviewByEntry(String uniqueName) {
		Overview overview = new Overview();
		List<History> history = this.historyDao.findHistoryByEntry(uniqueName);

		if (history != null && history.size() != 0)
			overview.setHistory(history.get(0));

		overview.setProteinNames(entityNameService.findNamesByEntityNameClass(uniqueName, PROTEIN_NAMES));
        overview.setGeneNames(entityNameService.findNamesByEntityNameClass(uniqueName, GENE_NAMES));
        overview.setCleavedRegionNames(entityNameService.findNamesByEntityNameClass(uniqueName, CLEAVED_REGION_NAMES));
        overview.setAdditionalNames(entityNameService.findNamesByEntityNameClass(uniqueName, ADDITIONAL_NAMES));
        overview.setFunctionalRegionNames(entityNameService.findNamesByEntityNameClass(uniqueName, FUNCTIONAL_REGION_NAMES));
		overview.setFamilies(this.familyService.findFamilies(uniqueName));
		overview.setIsoformNames(convertIsoNamestoOverviewName(isoformService.findIsoformsByEntryName(uniqueName)));
		overview.setProteinExistences(proteinExistenceService.getProteinExistences(uniqueName));

		return overview;
	}
	
	private static List<EntityName> convertIsoNamestoOverviewName(List<Isoform> isoforms){
		
		List<EntityName> isoNames = new ArrayList<>();
		for(Isoform isoform : isoforms){
			
			EntityName name = new EntityName();
			name.setMain(true);
			name.setName(isoform.getMainEntityName().getValue());
			name.setType(isoform.getMainEntityName().getType());
			name.setQualifier(isoform.getMainEntityName().getQualifier());

			for(EntityName syn : isoform.getSynonyms()){

				EntityName s = new EntityName();
				s.setMain(false);
				s.setName(syn.getValue());
				name.setType(syn.getType());
				name.setQualifier(syn.getQualifier());
				
				name.getSynonyms().add(s);
			}
			name.getSynonyms().sort(Comparator.comparing(EntityName::getName));
			
			isoNames.add(name);
		}
		
		return isoNames;
	}
}
