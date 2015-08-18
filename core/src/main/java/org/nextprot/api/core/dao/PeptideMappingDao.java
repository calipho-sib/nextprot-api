package org.nextprot.api.core.dao;

import java.util.List;
import java.util.Map;

import org.nextprot.api.core.domain.PeptideMapping;
import org.nextprot.api.core.domain.PeptideMapping.PeptideEvidence;
import org.nextprot.api.core.domain.PeptideMapping.PeptideProperty;
import org.nextprot.api.core.domain.annotation.AnnotationEvidence;
import org.nextprot.api.core.domain.annotation.AnnotationProperty;

public interface PeptideMappingDao {

	public static final String KEY_ANNOTATION_ID="annotationId";
	public static final String KEY_PEP_UNIQUE_NAME="peptideUniqueName";
	public static final String KEY_ISO_UNIQUE_NAME="isoformUniqueName";
	public static final String KEY_RANK="rank";
	public static final String KEY_FIRST_POS="firstPos";
	public static final String KEY_LAST_POS="lastPos";
	public static final String KEY_QUALITY_QUALIFIER="qualityQualifier";
	
	// as peptide mappings (soon obsolete)
	List<PeptideMapping> findAllPeptidesByMasterId(Long id);
	List<PeptideMapping> findNaturalPeptidesByMasterId(Long id);
	List<PeptideMapping> findSyntheticPeptidesByMasterId(Long id);
	
	List<PeptideEvidence> findAllPeptideEvidences(List<String> names);
	List<PeptideEvidence> findNaturalPeptideEvidences(List<String> names);
	List<PeptideEvidence> findSyntheticPeptideEvidences(List<String> names);

	List<PeptideProperty> findPeptideProperties(List<String> names);

	// as annotations (new implementation)
	List<Map<String,Object>> findAllPeptideMappingAnnotationsByMasterId(Long id);
	List<Map<String,Object>> findNaturalPeptideMappingAnnotationsByMasterId(Long id);
	List<Map<String,Object>> findSyntheticPeptideMappingAnnotationsByMasterId(Long id);
	
	List<AnnotationEvidence> findAllPeptideAnnotationEvidences(List<String> names);
	List<AnnotationEvidence> findNaturalPeptideAnnotationEvidences(List<String> names);
	List<AnnotationEvidence> findSyntheticPeptideAnnotationEvidences(List<String> names);

	List<AnnotationProperty> findPeptideAnnotationProperties(List<String> names);
	
}
