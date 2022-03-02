package org.nextprot.api.core.service.impl;

import com.google.common.collect.Sets;
import org.nextprot.api.core.dao.MasterIdentifierDao;
import org.nextprot.api.core.domain.ProteinExistence;
import org.nextprot.api.core.service.GeneService;
import org.nextprot.api.core.service.MasterIdentifierService;
import org.nextprot.api.core.service.OverviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Lazy
@Service
public class MasterIdentifierServiceImpl implements MasterIdentifierService {

	@Autowired
	private MasterIdentifierDao masterIdentifierDao;

	@Autowired
	private GeneService geneService;

	@Autowired
	private OverviewService overviewService;

	@Override
	@Cacheable(value = "master-unique-names-chromosome", sync = true)
	public List<String> findUniqueNamesOfChromosome(String chromosome) {
		return this.masterIdentifierDao.findUniqueNamesOfChromosome(chromosome);
	}

	@Override
	public Set<String> findUniqueNames() {
		return Sets.newTreeSet(this.masterIdentifierDao.findUniqueNames());
	}

	@Override
	@Cacheable(value = "master-unique-name", sync = true)
	public Long findIdByUniqueName(String uniqueName) {
		return this.masterIdentifierDao.findIdByUniqueName(uniqueName);
	}

	@Override
	@Cacheable(value="entry-accession-by-gene-name",key="{  #geneName, #withSynonyms }", sync = true)
	public Set<String> findEntryAccessionByGeneName(String geneName, boolean withSynonyms) {
		return Sets.newTreeSet(this.masterIdentifierDao.findUniqueNamesByGeneName(geneName, withSynonyms));
	}
	
	@Override
	@Cacheable(value="entry-accession-by-protein-existence", sync = true)
	public List<String> findEntryAccessionsByProteinExistence(ProteinExistence proteinExistence) {

		List<String> entries = new ArrayList<>();

		for (String entryAccession : masterIdentifierDao.findUniqueNames()) {

			ProteinExistence pe = overviewService.findOverviewByEntry(entryAccession).getProteinExistence();

			if (pe == proteinExistence) {
				entries.add(entryAccession);
			}
		}

		return entries;
	}

	@Override
	public MapStatus getMapStatusForENSG(String ensg) {
		
		Map<String,List<String>> map = geneService.getEntryENSGMap();
		List<String> entries = map.get(ensg);

		if (entries == null) {
			return new MapStatus(MapStatus.Status.MAPS_NO_ENTRY, new ArrayList<String>());
		} 

		if (entries.size()>1) {
			return new MapStatus(MapStatus.Status.MAPS_MULTIPLE_ENTRIES, entries);				
		} 
		
		String entry = entries.get(0);
		if (map.get(entry).size()>1) {
			return new MapStatus(MapStatus.Status.MAPS_MULTIGENE_ENTRY, entries);		
		} 
		
		return new MapStatus(MapStatus.Status.MAPS_MONOGENE_ENTRY, entries);		
	}
	

	public static class MapStatus {
		public static enum Status {MAPS_NO_ENTRY, MAPS_MONOGENE_ENTRY, MAPS_MULTIGENE_ENTRY, MAPS_MULTIPLE_ENTRIES};
		private List<String> entries;
		private Status status;
		public MapStatus(Status status, List<String> entries) {
			this.status = status;
			this.entries = entries;
		}
		public Status getStatus() { return this.status; }
		public List<String> getEntries() { return this.entries; } 
		public String toString() { 
			StringBuffer buf = new StringBuffer(); 
			buf.append(this.status.toString() + " ");
			for (String e: this.entries) buf.append(e + " ");
			return buf.toString();
		}
	}
	
}
