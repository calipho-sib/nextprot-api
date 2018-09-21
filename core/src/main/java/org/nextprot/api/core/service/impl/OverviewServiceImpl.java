package org.nextprot.api.core.service.impl;

import org.nextprot.api.core.dao.HistoryDao;
import org.nextprot.api.core.domain.EntityName;
import org.nextprot.api.core.domain.Isoform;
import org.nextprot.api.core.domain.Overview;
import org.nextprot.api.core.domain.Overview.EntityNameClass;
import org.nextprot.api.core.domain.Overview.History;
import org.nextprot.api.core.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;


@Service
class OverviewServiceImpl implements OverviewService {

	@Autowired private HistoryDao historyDao;
	@Autowired private EntityNameService entityNameService;
	@Autowired private FamilyService familyService;
	@Autowired private IsoformService isoformService;
	@Autowired private ProteinExistenceService proteinExistenceService;

	@Override
	@Cacheable("overview")
	public Overview findOverviewByEntry(String uniqueName) {
		Overview overview = new Overview();
		List<History> history = this.historyDao.findHistoryByEntry(uniqueName);

		if (history != null && history.size() != 0)
			overview.setHistory(history.get(0));

		Map<EntityNameClass, List<EntityName>> entityNames = entityNameService.findNamesByEntityNameClass(uniqueName);
		setNamesInOverview(entityNames, overview);

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
	
	private void setNamesInOverview(Map<EntityNameClass, List<EntityName>> entityNames, Overview overview){

		for (EntityNameClass en : entityNames.keySet()) {

			switch (en) {
				case PROTEIN_NAMES: {
					overview.setProteinNames(entityNames.get(en));
					break;
				}
				case GENE_NAMES: {
					overview.setGeneNames(entityNames.get(en));
					break;
				}
				case CLEAVED_REGION_NAMES: {
					overview.setCleavedRegionNames(entityNames.get(en));
					break;
				}
				case ADDITIONAL_NAMES: {
					overview.setAdditionalNames(entityNames.get(en));
					break;
				}
				case FUNCTIONAL_REGION_NAMES: {
					overview.setFunctionalRegionNames(entityNames.get(en));
					break;
				}
			}
		}
	}
}
