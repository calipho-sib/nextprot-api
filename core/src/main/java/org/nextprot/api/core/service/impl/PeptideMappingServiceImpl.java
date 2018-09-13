package org.nextprot.api.core.service.impl;

import com.google.common.collect.ImmutableList;
import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.commons.constants.PropertyApiModel;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.core.dao.PeptideMappingDao;
import org.nextprot.api.core.domain.SequenceUnicity;
import org.nextprot.api.core.domain.annotation.*;
import org.nextprot.api.core.service.MasterIdentifierService;
import org.nextprot.api.core.service.PeptideMappingService;
import org.nextprot.api.core.service.PeptideNamesService;
import org.nextprot.api.core.service.SequenceUnicityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PeptideMappingServiceImpl implements PeptideMappingService {

	@Autowired private MasterIdentifierService masterIdentifierService;
	@Autowired private PeptideMappingDao peptideMappingDao;
	@Autowired private PeptideNamesService peptideNamesService;
    @Autowired private SequenceUnicityService sequenceUnicityService;


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
		List<Annotation> annotations = new ArrayList<>(annotationMap.values());
		if (annotations.isEmpty()) return annotations;
		
		attachPeptidePropertiesToAnnotations(annotations, sequenceUnicityService.getPeptideNameUnicityMap());
		
		List<String> pepNames = this.peptideNamesService.findAllPeptideNamesByMasterId(uniqueName);
		Map<String,List<AnnotationEvidence>> evidences = this.peptideMappingDao.findPeptideAnnotationEvidencesMap(pepNames, withNatural); // nat=true,synth=false
		attachPeptideEvidencesToAnnotations(annotations, evidences);	
		
		//returns a immutable list when the result is cacheable (this prevents modifying the cache, 
		//since the cache returns a reference) copy on read and copy on write is too much time consuming
		return new ImmutableList.Builder<Annotation>().addAll(annotations).build();
		
	}

	
	static String getMappingAnnotationPeptideName(Annotation annot) {
		// retrieve pep name from annotation properties
		if (!annot.getPropertiesMap().containsKey(PropertyApiModel.NAME_PEPTIDE_NAME)) {
			throw new NextProtException("Cannot find property name " + PropertyApiModel.NAME_PEPTIDE_NAME);
		}
		Collection<AnnotationProperty> properties = annot.getPropertiesByKey(PropertyApiModel.NAME_PEPTIDE_NAME);
		AnnotationProperty pepNameProperty = properties.iterator().next(); // WARNING: we expect first property in list be the "peptide name" property !
		return pepNameProperty.getValue();
	}
	
	static void attachPeptidePropertiesToAnnotations(List<Annotation> annotations, Map<String, SequenceUnicity> pepNamePuMap) {
		for (Annotation annot: annotations) {
			String pepName = getMappingAnnotationPeptideName(annot);
			SequenceUnicity pu = pepNamePuMap.get(pepName);
			String proteotypicValue = pu.getValue().equals(SequenceUnicity.Value.NOT_UNIQUE) ? "N" : "Y";
			String unicityValue = pu.getValue().name();
			annot.addProperty(createAnnotationProperty(annot.getAnnotationId(), pepName, PropertyApiModel.NAME_PEPTIDE_PROTEOTYPICITY, proteotypicValue));
			annot.addProperty(createAnnotationProperty(annot.getAnnotationId(), pepName, PropertyApiModel.NAME_PEPTIDE_UNICITY, unicityValue));
		}
	}

    static void attachPeptideEvidencesToAnnotations(List<Annotation> annotations, Map<String, List<AnnotationEvidence>> evidences) {

        for (Annotation annot: annotations) {
			String pepName = getMappingAnnotationPeptideName(annot);
            if (!evidences.containsKey(pepName)) {
                throw new NextProtException("Found no evidence for peptide with name:" + pepName);
            }
            List<AnnotationEvidence> clonedList = cloneEvidencesForAnnotation(evidences.get(pepName), annot.getAnnotationId());
            annot.setEvidences(clonedList);
        }
    }
	
	static AnnotationProperty createAnnotationProperty(Long annotationId, String acc, String name, String value) {
		AnnotationProperty prop = new AnnotationProperty();
		prop.setAnnotationId(annotationId);
		prop.setAccession(acc);
		prop.setName(name);
		prop.setValue(value);
		return prop;
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
			annEvi.setQualifierType(pepEvi.getQualifierType());
			annEvi.setQualityQualifier(pepEvi.getQualityQualifier());
			annEvi.setResourceAccession(pepEvi.getResourceAccession());
			annEvi.setResourceAssociationType(pepEvi.getResourceAssociationType());
			annEvi.setResourceDb(pepEvi.getResourceDb());
			annEvi.setResourceDescription(pepEvi.getResourceDescription());
			annEvi.setResourceId(pepEvi.getResourceId());
			annEvi.setResourceType(pepEvi.getResourceType());
			annEvi.setEvidenceCodeOntology(pepEvi.getEvidenceCodeOntology());
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
			Integer firstPos = (Integer)record.get(PeptideMappingDao.KEY_FIRST_POS);
			Integer lastPos = (Integer)record.get(PeptideMappingDao.KEY_LAST_POS);
			String quality = (String)record.get(PeptideMappingDao.KEY_QUALITY_QUALIFIER);
			String category = isNatural ?
					AnnotationCategory.PEPTIDE_MAPPING.getDbAnnotationTypeName() :
					AnnotationCategory.SRM_PEPTIDE_MAPPING.getDbAnnotationTypeName();

			// if annot never seen before, put it into the map and initialize it
			if (!annotationMap.containsKey(annotationId)) {
				Annotation annot = new Annotation();
				annotationMap.put(annotationId, annot);
				annot.setAnnotationId(annotationId);
				annot.setCategory(category);
				annot.setQualityQualifier(quality);
				annot.addTargetingIsoforms(new ArrayList<AnnotationIsoformSpecificity>());
				String entry = iso.substring(0, iso.indexOf('-'));
				String nature = isNatural ? "_NATUR": "_SYNTH";
				annot.setUniqueName("AN_" + entry + "_" + annot.getAnnotationId() + nature);

				// add peptide name property
				List<AnnotationProperty> props = new ArrayList<>(); 
				AnnotationProperty prop = new AnnotationProperty();
				prop.setAnnotationId(annotationId);
				prop.setName(PropertyApiModel.NAME_PEPTIDE_NAME);
				prop.setValue(pep);
				props.add(prop);
				annot.addProperties(props);

			}
			// add isoform specificity
			Annotation annot = annotationMap.get(annotationId);
			AnnotationIsoformSpecificity spec = new AnnotationIsoformSpecificity();
			spec.setAnnotationId(annotationId);
			spec.setFirstPosition(firstPos);
			spec.setLastPosition(lastPos);
			spec.setIsoformAccession(iso);
			spec.setSpecificity("SPECIFIC");
			annot.getTargetingIsoformsMap().put(iso, spec);

		}
		
		return annotationMap;
		
	}
}
