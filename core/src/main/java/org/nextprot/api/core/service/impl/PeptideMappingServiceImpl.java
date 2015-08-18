package org.nextprot.api.core.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.nextprot.api.commons.constants.AnnotationApiModel;
import org.nextprot.api.commons.service.MasterIdentifierService;
import org.nextprot.api.core.dao.PeptideMappingDao;
import org.nextprot.api.core.domain.IsoformSpecificity;
import org.nextprot.api.core.domain.PeptideMapping;
import org.nextprot.api.core.domain.PeptideMapping.PeptideEvidence;
import org.nextprot.api.core.domain.PeptideMapping.PeptideProperty;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.domain.annotation.AnnotationIsoformSpecificity;
import org.nextprot.api.core.domain.annotation.AnnotationProperty;
import org.nextprot.api.core.service.PeptideMappingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.google.common.collect.ImmutableList;

@Service
public class PeptideMappingServiceImpl implements PeptideMappingService {

	@Autowired private MasterIdentifierService masterIdentifierService;
	@Autowired private PeptideMappingDao peptideMappingDao;


//TODO see cache definition in xml for this service (changes required)
	
	@Override
	@Cacheable("natural-peptides")
	public List<PeptideMapping> findNaturalPeptideMappingByMasterUniqueName(String uniqueName) {
		
		Long masterId = this.masterIdentifierService.findIdByUniqueName(uniqueName);
		List<PeptideMapping> peps =  findPeptideMappingByMasterId(masterId, true);
		
		//returns a immutable list when the result is cacheable (this prevents modifying the cache, since the cache returns a reference) copy on read and copy on write is too much time consuming
		return new ImmutableList.Builder<PeptideMapping>().addAll(peps).build();



	}
	
	@Override
	@Cacheable("srm-peptides")
	public List<PeptideMapping> findSyntheticPeptideMappingByMasterUniqueName(String uniqueName) {

		Long masterId = this.masterIdentifierService.findIdByUniqueName(uniqueName);
		List<PeptideMapping> peps = findPeptideMappingByMasterId(masterId, false);

		//returns a immutable list when the result is cacheable (this prevents modifying the cache, since the cache returns a reference) copy on read and copy on write is too much time consuming
		return new ImmutableList.Builder<PeptideMapping>().addAll(peps).build();

	}
		
	
	@Override
	@Cacheable("all-peptides-names")
	public List<String> findAllPeptideNamesByMasterId(String uniqueName) {
		Long masterId = this.masterIdentifierService.findIdByUniqueName(uniqueName);
		List<Map<String,Object>> allMapping = this.peptideMappingDao.findAllPeptideMappingAnnotationsByMasterId(masterId);
		Set<String> names = new HashSet<String>(); 
		for (Map<String,Object> map: allMapping) 
			names.add((String)map.get(PeptideMappingDao.KEY_PEP_UNIQUE_NAME));
		//returns a immutable list when the result is cacheable (this prevents modifying the cache, since the cache returns a reference) copy on read and copy on write is too much time consuming
		return new ImmutableList.Builder<String>().addAll(new ArrayList<String>(names)).build();
	}
	


	private List<PeptideMapping> findPeptideMappingByMasterId(Long id, boolean isNatural) {

		List<PeptideMapping> allMapping = isNatural ? 
			this.peptideMappingDao.findNaturalPeptidesByMasterId(id) :
			this.peptideMappingDao.findSyntheticPeptidesByMasterId(id) ;

		// key=peptide,value=mapping with 1-n isospecs, 1-n evidences, 1-n properties
		Map<String, PeptideMapping> mergeMap = new HashMap<String, PeptideMapping>();

		if (allMapping.size() > 0) {
			String key = null;
			List<String> peptideNames = new ArrayList<String>();

			Iterator<IsoformSpecificity> it = null;
			for (PeptideMapping mapping : allMapping) {
				key = mapping.getPeptideUniqueName();

				if (!mergeMap.containsKey(key)) { // not in the map
					peptideNames.add(mapping.getPeptideUniqueName());
					mergeMap.put(key, mapping);
				} else { // already in the map
					it = mapping.getIsoformSpecificity().values().iterator();
					if (it.hasNext())
						mergeMap.get(key).addIsoformSpecificity(it.next());
				}
			}

			// attach evidences to peptide mappings
			List<PeptideEvidence> evidences = isNatural ?
				this.peptideMappingDao.findNaturalPeptideEvidences(peptideNames) :
				this.peptideMappingDao.findSyntheticPeptideEvidences(peptideNames);
			for (PeptideEvidence evidence : evidences)
				mergeMap.get(evidence.getPeptideName()).addEvidence(evidence);

			// attach properties to peptide mappings
			List<PeptideProperty> props = this.peptideMappingDao.findPeptideProperties(peptideNames);
			for (PeptideProperty prop: props) 
				mergeMap.get(prop.getPeptideName()).addProperty(prop);
		}

		return new ArrayList<PeptideMapping>(mergeMap.values());
	}
	

	/*
	 * - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	 * new interface
	 * - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	 */
	
	
	@Override
	@Cacheable("natural-peptide-mapping-annotations")
	public List<Annotation> findNaturalPeptideMappingAnnotationsByMasterUniqueName(String uniqueName) {

		Long masterId = this.masterIdentifierService.findIdByUniqueName(uniqueName);
		List<Annotation> peps = findPeptideMappingAnnotationsByMasterId(masterId, true);

		//returns a immutable list when the result is cache able (this prevents modifying the cache, since the cache returns a reference) copy on read and copy on write is too much time consuming
		return new ImmutableList.Builder<Annotation>().addAll(peps).build();

	}

	@Override
	@Cacheable("srm-peptide-mapping-annotations")
	public List<Annotation> findSyntheticPeptideMappingAnnotationsByMasterUniqueName(String uniqueName) {

		Long masterId = this.masterIdentifierService.findIdByUniqueName(uniqueName);
		List<Annotation> peps = findPeptideMappingAnnotationsByMasterId(masterId, false);

		//returns a immutable list when the result is cacheable (this prevents modifying the cache, since the cache returns a reference) copy on read and copy on write is too much time consuming
		return new ImmutableList.Builder<Annotation>().addAll(peps).build();
	}
	

	
	
	List<Annotation> findPeptideMappingAnnotationsByMasterId(Long id, boolean isNatural) {
		List<Map<String, Object>> records = (isNatural ? 
				this.peptideMappingDao.findNaturalPeptideMappingAnnotationsByMasterId(id) : 
					this.peptideMappingDao.findSyntheticPeptideMappingAnnotationsByMasterId(id)) ;
		Map<Long,Annotation> annotationMap = buildAnnotationMapFromRecords(records, isNatural);
		return new ArrayList<Annotation>(annotationMap.values());
	}
	
	private void attachAnnotationProperties(Collection<Annotation> annotations) {
		// attach other properties to annotations according to their peptide name
		//List<PeptideProperty> props = this.peptideMappingDao.findPeptideProperties(peptideNames);

	}
	
	
	static Map<Long,Annotation> buildAnnotationMapFromRecords(List<Map<String,Object>> records, boolean isNatural) {
		
		Map<Long,Annotation> annotationMap = new HashMap<>();	
		for (Map<String,Object> record: records) {

			// retrieve record field values
			Long annotationId = (Long)record.get(PeptideMappingDao.KEY_ANNOTATION_ID);
			String iso = (String)record.get(PeptideMappingDao.KEY_ISO_UNIQUE_NAME);
			String pep = (String)record.get(PeptideMappingDao.KEY_PEP_UNIQUE_NAME);
			Integer rank = (Integer)record.get(PeptideMappingDao.KEY_RANK);
			Integer firstPos = (Integer)record.get(PeptideMappingDao.KEY_FIRST_POS);
			Integer lastPos = (Integer)record.get(PeptideMappingDao.KEY_LAST_POS);
			String quality = (String)record.get(PeptideMappingDao.KEY_QUALITY_QUALIFIER);
			String category = (isNatural ? 
					AnnotationApiModel.PEPTIDE_MAPPING.getDbAnnotationTypeName() : 
						AnnotationApiModel.SRM_PEPTIDE_MAPPING.getDbAnnotationTypeName());

			// if annot never seen before, put it into the map and initialize it
			if (!annotationMap.containsKey(annotationId)) {
				Annotation annot = new Annotation();
				annotationMap.put(annotationId, annot);
				annot.setAnnotationId(annotationId);
				annot.setCategory(category);
				annot.setQualityQualifier(quality);
				annot.setTargetingIsoforms(new ArrayList<AnnotationIsoformSpecificity>());
				String entry = iso.substring(0, iso.indexOf('-'));
				annot.setUniqueName("AN_" + entry + "_" + pep + "_" + rank);

				// add peptide name property
				List<AnnotationProperty> props = new ArrayList<>(); 
				AnnotationProperty prop = new AnnotationProperty();
				prop.setAnnotationId(annotationId);
				prop.setName(AnnotationProperty.NAME_PEPTIDE_NAME);
				prop.setValue(pep);
				props.add(prop);
				annot.setProperties(props);

			}
			// add isoform specificity
			Annotation annot = annotationMap.get(annotationId);
			AnnotationIsoformSpecificity spec = new AnnotationIsoformSpecificity();
			spec.setAnnotationId(annotationId);
			spec.setFirstPosition(firstPos);
			spec.setLastPosition(lastPos);
			spec.setIsoformName(iso);
			spec.setSpecificity("SPECIFIC");
			annot.getTargetingIsoformsMap().put(iso, spec);

		}
		
		return annotationMap;
		
	}

}
