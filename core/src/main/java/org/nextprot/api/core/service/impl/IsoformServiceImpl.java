package org.nextprot.api.core.service.impl;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import org.nextprot.api.commons.utils.NucleotidePositionRange;
import org.nextprot.api.core.dao.IsoformDAO;
import org.nextprot.api.core.dao.MasterIsoformMappingDao;
import org.nextprot.api.core.domain.EntityName;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.Isoform;
import org.nextprot.api.core.domain.IsoformPEFFHeader;
import org.nextprot.api.core.domain.Overview;
import org.nextprot.api.core.domain.SlimIsoform;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.service.AnnotationService;
import org.nextprot.api.core.service.EntityNameService;
import org.nextprot.api.core.service.EntryBuilderService;
import org.nextprot.api.core.service.IsoformService;
import org.nextprot.api.core.service.OverviewService;
import org.nextprot.api.core.service.TerminologyService;
import org.nextprot.api.core.service.fluent.EntryConfig;
import org.nextprot.api.core.service.impl.peff.IsoformPEFFHeaderBuilder;
import org.nextprot.api.core.utils.IsoformUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.nextprot.api.core.utils.IsoformUtils.findEntryAccessionFromIsoformAccession;

@Service
class IsoformServiceImpl implements IsoformService {

	@Autowired
	private IsoformDAO isoformDAO;

	@Autowired
	private MasterIsoformMappingDao masterIsoformMappingDAO;

	@Autowired
	private EntityNameService entityNameService;

	@Autowired
	private TerminologyService terminologyService;

	@Autowired
	private AnnotationService annotationService;

	@Autowired
    private OverviewService overviewService;

    @Autowired
    private EntryBuilderService entryBuilderService;

	@Override
	@Cacheable(value = "isoforms", sync = true)
	public List<Isoform> findIsoformsByEntryName(String entryName) {
		List<Isoform> isoforms = isoformDAO.findIsoformsByEntryName(entryName);
		List<EntityName> synonyms = isoformDAO.findIsoformsSynonymsByEntryName(entryName);
		Map<String,List<NucleotidePositionRange>> isoMasterNuPosRanges = masterIsoformMappingDAO.findMasterIsoformMapping(entryName);
				
		//Groups the synonyms by their main isoform
		Multimap<String, EntityName> synonymsMultiMap = Multimaps.index(synonyms, new SynonymFunction());
		for (Isoform isoform : isoforms) {
			isoform.setSynonyms(synonymsMultiMap.get(isoform.getIsoformAccession()));
		}

		//Attach master mapping to each isoform
		for (Isoform isoform : isoforms) {
			if (isoMasterNuPosRanges.containsKey(isoform.getIsoformAccession())) {
				isoform.setMasterMapping(isoMasterNuPosRanges.get(isoform.getIsoformAccession()));
			} else {
				isoform.setMasterMapping(new ArrayList<>());
			}
		}

		isoforms.sort((i1, i2) -> new IsoformUtils.IsoformComparator().compare(i1, i2));

		//returns a immutable list when the result is cacheable (this prevents modifying the cache, since the cache returns a reference) copy on read and copy on write is too much time consuming
		return new ImmutableList.Builder<Isoform>().addAll(isoforms).build();
	}

	@Override
	public Isoform findIsoformByName(String entryName, String name) {

		List<Isoform> isoforms = findIsoformsByEntryName(entryName);

		for (Isoform isoform : isoforms) {

			if (isoform.getIsoformAccession().equals(name)) {
				return isoform;
			}
			else if (entityNameService.hasNameIgnoreCase(isoform.getMainEntityName(), name)) {
				return isoform;
			}
		}

		return null;
	}

    @Override
    public List<Isoform> getOtherIsoforms(String isoformUniqueName) {

        if (!isoformUniqueName.contains("-")) {

            return new ArrayList<>();
        }

        Entry entry = entryBuilderService.build(EntryConfig.newConfig(isoformUniqueName.split("-")[0])
                .withTargetIsoforms());

        return IsoformUtils.getOtherIsoforms(entry, isoformUniqueName);
    }

    @Override
	@Cacheable(value = "peff-by-isoform", sync = true)
	public IsoformPEFFHeader formatPEFFHeader(String isoformAccession) {

	    String entryAccession = findEntryAccessionFromIsoformAccession(isoformAccession);

        List<Annotation> isoformAnnotations = annotationService.findAnnotations(entryAccession).stream()
                .filter(annotation -> annotation.isSpecificForIsoform(isoformAccession))
                .collect(Collectors.toList());

        Overview overview = overviewService.findOverviewByEntry(entryAccession);

		return new IsoformPEFFHeaderBuilder(findIsoform(isoformAccession), isoformAnnotations, overview,
                terminologyService::findPsiModAccession, terminologyService::findPsiModName)
                .withEverything()
                .build();
	}

	private class SynonymFunction implements Function<EntityName, String> {
		public String apply(EntityName isoformSynonym) {
			return isoformSynonym.getMainEntityName();
		}
	}

	@Override
	@Cacheable(value = "equivalent-isoforms", sync = true)
	public List<Set<String>> getSetsOfEquivalentIsoforms() {
		return isoformDAO.findSetsOfEquivalentIsoforms();
	}

	@Override
	@Cacheable(value = "entries-having-equivalent-isoforms", sync = true)
	public List<Set<String>> getSetsOfEntriesHavingAnEquivalentIsoform() {
		return isoformDAO.findSetsOfEntriesHavingAnEquivalentIsoform();
	}

	@Override
	@Cacheable(value = "isoforms-md5", sync = true)
	public List<SlimIsoform> findListOfIsoformAcMd5Sequence() {
		return isoformDAO.findOrderedListOfIsoformAcMd5SequenceFieldMap();
	}

    @Override
    public Isoform getIsoformByNameOrCanonical(String entryNameOrIsoformName) {

        Entry entry = entryBuilderService.build(EntryConfig.newConfig(entryNameOrIsoformName).withTargetIsoforms());

        if (!entryNameOrIsoformName.contains("-")) {

            return IsoformUtils.getCanonicalIsoform(entry);
        }

        return IsoformUtils.getIsoformByName(entry, entryNameOrIsoformName);
    }
}
