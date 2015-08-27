package org.nextprot.api.core.service.impl;

import com.google.common.collect.ImmutableList;
import org.nextprot.api.commons.constants.AnnotationApiModel;
import org.nextprot.api.commons.service.MasterIdentifierService;
import org.nextprot.api.core.dao.PeptideMappingDao;
import org.nextprot.api.core.domain.IsoformSpecificity;
import org.nextprot.api.core.domain.PeptideMapping;
import org.nextprot.api.core.domain.PeptideMapping.PeptideEvidence;
import org.nextprot.api.core.domain.PeptideMapping.PeptideProperty;
import org.nextprot.api.core.domain.annotation.*;
import org.nextprot.api.core.service.PeptideMappingService;
import org.nextprot.api.core.service.PeptideNamesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PeptideMappingServiceImpl implements PeptideMappingService {

	@Autowired private MasterIdentifierService masterIdentifierService;
	@Autowired private PeptideMappingDao peptideMappingDao;
	@Autowired private PeptideNamesService peptideNamesService;

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
		
		return findPeptideMappingAnnotationsByMasterUniqueName(uniqueName,true);
	}

	
	@Override
	@Cacheable("srm-peptide-mapping-annotations")
	public List<Annotation> findSyntheticPeptideMappingAnnotationsByMasterUniqueName(String uniqueName) {
		
		return findPeptideMappingAnnotationsByMasterUniqueName(uniqueName,false);
	}

	private List<Annotation> findPeptideMappingAnnotationsByMasterUniqueName(String uniqueName, boolean withNatural) {
	
		Long masterId = this.masterIdentifierService.findIdByUniqueName(uniqueName);		
		boolean withSynthetic = ! withNatural;
		List<Map<String, Object>> records = this.peptideMappingDao.findPeptideMappingAnnotationsByMasterId(masterId, withNatural, withSynthetic);
		Map<Long,Annotation> annotationMap = buildAnnotationMapFromRecords(records, withNatural);		
		List<Annotation> annotations = new ArrayList<Annotation>(annotationMap.values());
		if (annotations.size()==0) return annotations;
		
		List<String> pepNames = this.peptideNamesService.findAllPeptideNamesByMasterId(uniqueName);
		
		Map<String,List<AnnotationProperty>> props = this.peptideMappingDao.findPeptideAnnotationPropertiesMap(pepNames);
		attachPeptidePropertiesToAnnotations(annotations, props);	
		
		Map<String,List<AnnotationEvidence>> evidences = this.peptideMappingDao.findPeptideAnnotationEvidencesMap(pepNames, withNatural); // nat=true,synth=false
		attachPeptideEvidencesToAnnotations(annotations, evidences);	
		
		//returns a immutable list when the result is cacheable (this prevents modifying the cache, 
		//since the cache returns a reference) copy on read and copy on write is too much time consuming
		return new ImmutableList.Builder<Annotation>().addAll(annotations).build();
		
	}

	static void attachPeptidePropertiesToAnnotations(List<Annotation> annotations, Map<String, List<AnnotationProperty>> propMap) {
		for (Annotation annot: annotations) {
			AnnotationProperty pepNameProperty = annot.getProperties().get(0); // WARNING: we expect first property in list be the "peptide name" property !
			if (!pepNameProperty.getName().equals(AnnotationProperty.NAME_PEPTIDE_NAME)) {
				throw new RuntimeException("Found unexpected property name:" + pepNameProperty.getName());
			}
			String pepName = pepNameProperty.getValue();
			if (!propMap.containsKey(pepName)) {
				throw new RuntimeException("Found no props for peptide with name:" + pepName);
			}
			List<AnnotationProperty> props = cloneUsefulPropertiesForAnnotation(propMap.get(pepName), annot.getAnnotationId());
			annot.getProperties().addAll(props);
		}
	}
	
	/*
	 * clone the property 'is proteotypic' and sets its annotation id
	 * and return a list containing this "cloned" property
	 */
	static List<AnnotationProperty> cloneUsefulPropertiesForAnnotation(List<AnnotationProperty> peptideProperties, Long annotationId) {
		List<AnnotationProperty> result = new ArrayList<>();
		for (AnnotationProperty pp: peptideProperties) {
			if (pp.getName().equals(AnnotationProperty.NAME_PEPTIDE_PROTEOTYPICITY)) {
				AnnotationProperty ap = new AnnotationProperty();
				ap.setAnnotationId(annotationId);
				ap.setAccession(pp.getAccession());
				ap.setName(pp.getName());
				ap.setValue(pp.getValue());
				ap.setValueType(pp.getValueType());
				result.add(ap);
			}
		}
		return result;
	}
	
	static void attachPeptideEvidencesToAnnotations(List<Annotation> annotations, Map<String, List<AnnotationEvidence>> evidences) {
		for (Annotation annot: annotations) {
			AnnotationProperty pepNameProperty = annot.getProperties().get(0);  // WARNING: we expect first property in list be the "peptide name" property !
			if (!pepNameProperty.getName().equals(AnnotationProperty.NAME_PEPTIDE_NAME)) {
				throw new RuntimeException("Found unexpected property name:" + pepNameProperty.getName());
			}
			String pepName = pepNameProperty.getValue();
			if (!evidences.containsKey(pepName)) {
				throw new RuntimeException("Found no evidence for peptide with name:" + pepName);
			}
			List<AnnotationEvidence> peptideEvidences = evidences.get(pepName); 
			List<AnnotationEvidence> clonedList = cloneEvidencesForAnnotation(peptideEvidences, annot.getAnnotationId());
			annot.setEvidences(clonedList);
		}
	}
	
	static List<AnnotationEvidence> cloneEvidencesForAnnotation(List<AnnotationEvidence> peptideEvidences, Long annotationId) {
		
		List<AnnotationEvidence> result = new ArrayList<>();
		for (AnnotationEvidence pepEvi: peptideEvidences) {
			AnnotationEvidence annEvi = new AnnotationEvidence(); 
			annEvi.setAnnotationId(annotationId);
			annEvi.setAssignedBy(pepEvi.getAssignedBy());
			annEvi.setAssignmentMethod(pepEvi.getAssignmentMethod());
			annEvi.setEvidenceCodeAC(pepEvi.getEvidenceCodeAC());
			annEvi.setEvidenceCodeName(pepEvi.getEvidenceCodeName());
			annEvi.setEvidenceId(pepEvi.getEvidenceId());
			annEvi.setExperimentalContextId(pepEvi.getExperimentalContextId());
			annEvi.setNegativeEvidence(pepEvi.isNegativeEvidence());
			annEvi.setPublicationMD5(pepEvi.getPublicationMD5());
			annEvi.setQualifierType(pepEvi.getQualifierType());
			annEvi.setQualityQualifier(pepEvi.getQualityQualifier());
			annEvi.setResourceAccession(pepEvi.getResourceAccession());
			annEvi.setResourceAssociationType(pepEvi.getResourceAssociationType());
			annEvi.setResourceDb(pepEvi.getResourceDb());
			annEvi.setResourceDescription(pepEvi.getResourceDescription());
			annEvi.setResourceId(pepEvi.getResourceId());
			annEvi.setResourceType(pepEvi.getResourceType());
			if (pepEvi.getPropertiesNames()!=null) {
				List<AnnotationEvidenceProperty> props = new ArrayList<>();
				for (String n: pepEvi.getPropertiesNames()) {
					String v = pepEvi.getPropertyValue(n);
					AnnotationEvidenceProperty prop = new AnnotationEvidenceProperty();
					prop.setEvidenceId(pepEvi.getEvidenceId());
					prop.setPropertyName(n);
					prop.setPropertyValue(v);
					props.add(prop);
				}
				annEvi.setProperties(props);
			}
			result.add(annEvi);
		}
		return result;
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
				String nature = isNatural ? "_NATUR": "_SYNTH";
				annot.setUniqueName("AN_" + entry + "_" + annot.getAnnotationId() + nature);

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
