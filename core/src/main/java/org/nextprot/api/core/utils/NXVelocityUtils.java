package org.nextprot.api.core.utils;

import java.util.ArrayList;
import java.util.List;

import org.nextprot.api.commons.constants.AnnotationApiModel;
import org.nextprot.api.commons.utils.StringUtils;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.annotation.Annotation;

public class NXVelocityUtils {

	private static AnnotationUtils annotUtil = new AnnotationUtils();
	//private static StringUtils strUtil = new StringUtils();

	public static AnnotationUtils getAnnot(){
		return annotUtil;
	}

	public static List<Annotation> getAnnotationsByCategory(Entry entry, AnnotationApiModel annotationCategory) {
		List<Annotation> annotations = entry.getAnnotations();
		return AnnotationUtils.filterAnnotationsByCategory(annotations, annotationCategory, false);
	}
	
	public static String getAnnotationCategoryNameForXML(AnnotationApiModel annotationCategory) {
		return StringUtils.decamelizeAndReplaceByHyphen(annotationCategory.getApiTypeName());
	}
	
	public static String getAnnotationCategoryHierachyForXML(AnnotationApiModel annotationCategory) {
		StringBuffer sb = new StringBuffer();
		for (AnnotationApiModel cat: annotationCategory.getAllParentsButRoot()) {
			if (sb.length()>0) sb.append(";");
			sb.append(StringUtils.decamelizeAndReplaceByHyphen(cat.getApiTypeName()));			
		}
		return sb.toString();
	}

	
	public boolean hasInteractions(Entry entry) {
		if (entry.getInteractions().size()>0) return true;
		if (getAnnotationsByCategory(entry, AnnotationApiModel.SMALL_MOLECULE_INTERACTION).size()>0) return true;
		return false;
	}
	
	public boolean hasMappings(Entry entry) {
		if ((entry.getPeptideMappings() != null) && entry.getPeptideMappings().size()>0) return true;
		if ((entry.getSrmPeptideMappings() != null) && entry.getSrmPeptideMappings().size()>0) return true;
		if ((entry.getAntibodyMappings() != null) && entry.getAntibodyMappings().size()>0) return true;
		if ((entry.getAnnotations() != null) && getAnnotationsByCategory(entry, AnnotationApiModel.PDB_MAPPING).size()>0) return true;
		return false;
	}
	
	/**
	 * 
	 * @return the list of LEAF annotation categories except family-name
	 */
	public static List<AnnotationApiModel>  getAnnotationCategories() {
		List<AnnotationApiModel> list = new ArrayList<AnnotationApiModel>();
		AnnotationApiModel[] vals = AnnotationApiModel.values();
		for (int i=0;i<vals.length;i++) {
			if (vals[i].equals(AnnotationApiModel.FAMILY_NAME)) continue;
			list.add(vals[i]);
		}
		//System.out.println("cat size:" + list.size());
		return list;
	}

	
}
