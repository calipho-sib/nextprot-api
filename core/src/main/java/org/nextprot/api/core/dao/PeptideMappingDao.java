package org.nextprot.api.core.dao;

import java.util.List;
import java.util.Map;

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
	
	List<Map<String,Object>> findPeptideMappingAnnotationsByMasterId(Long id, boolean withNatural, boolean withSynthetic);
	Map<String,List<AnnotationEvidence>> findPeptideAnnotationEvidencesMap(List<String> names, boolean withNatural);
	Map<String,List<AnnotationProperty>> findPeptideAnnotationPropertiesMap(List<String> names);
	List<String> findPeptideIsoformMappingsList();
	
}
