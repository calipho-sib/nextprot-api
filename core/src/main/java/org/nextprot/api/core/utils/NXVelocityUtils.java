package org.nextprot.api.core.utils;

import org.nextprot.api.commons.bio.DescriptorMass;
import org.nextprot.api.commons.bio.DescriptorPI;
import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.commons.constants.PropertyApiModel;
import org.nextprot.api.commons.constants.PropertyWriter;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.Family;
import org.nextprot.api.core.domain.Isoform;
import org.nextprot.api.core.domain.Proteoform;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.service.annotation.AnnotationUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class NXVelocityUtils {
	
    private NXVelocityUtils() {
        throw new AssertionError("should not be instanciable");
    }

    public static Map<String,Annotation> getUniqueNameAnnotationMap(Entry entry) {
    	return EntryUtils.getUniqueNameAnnotationMap(entry);
    }
    public static Map<String,Annotation> getHashAnnotationMap(Entry entry) {
    	return EntryUtils.getHashAnnotationMap(entry);
    }
    
    public static Map<Proteoform,List<Annotation>> getProteoformAnnotationsMap(Entry entry, String isoformAc) {
		return EntryUtils.getProteoformAnnotationsMap(entry, isoformAc);
	}
        
	public static List<Annotation> getAnnotationsByCategory(Entry entry, AnnotationCategory annotationCategory) {
		return AnnotationUtils.filterAnnotationsByCategory(entry, annotationCategory, false);
	}
	
	public static boolean hasInteractions(Entry entry) {
		if (!entry.getInteractions().isEmpty()) return true;
		if (!getAnnotationsByCategory(entry, AnnotationCategory.SMALL_MOLECULE_INTERACTION).isEmpty()) return true;
		return false;
	}
	
	public static List<AnnotationCategory> getAnnotationCategories() {
		return AnnotationCategory.getSortedCategories();
	}

	/**
	 * Compute isoelectric point of given isoform
	 * @param isoform isoform
	 * @return isoelectric point String
	 */
	public static String getIsoelectricPointAsString(Isoform isoform) {

		Double d = DescriptorPI.compute(isoform.getSequence());
		DecimalFormat df = new DecimalFormat("#.##");

		return df.format(d);
	}

	/**
	 * Compute molecular mass of given isoform
	 * @param isoform isoform
	 * @return molecular mass String
	 */
	public static String getMassAsString(Isoform isoform) {

		Double d = DescriptorMass.compute(isoform.getSequence());
		return String.valueOf(Math.round(d));
	}

	public static PropertyWriter getXMLPropertyWriter(AnnotationCategory aModel, String propertyDbName) {
		return PropertyApiModel.getXMLWriter(aModel, propertyDbName);
	}
	public static PropertyWriter getTtlPropertyWriter(AnnotationCategory aModel, String propertyDbName) {
		return PropertyApiModel.getTtlWriter(aModel, propertyDbName);
	}

	public static boolean isDisulfideBond(Annotation annotation) {

		return annotation.getAPICategory() == AnnotationCategory.DISULFIDE_BOND;
	}

	public static boolean isCrossLink(Annotation annotation) {

		return annotation.getAPICategory() == AnnotationCategory.CROSS_LINK;
	}

	/**
	 * @return a list of Family instances from root family to this family
	 */
	public static List<Family> getFamilyHierarchyFromRoot(Family family) {

		List<Family> hierarchy = new ArrayList<>();

		hierarchy.add(family);

		Family directParent = family.getParent();

		while (directParent != null) {

			hierarchy.add(0, directParent);

			directParent = directParent.getParent();
		}

		return hierarchy;
	}

	public static Set<String> clonedSetWithoutElement(Set<String> originalSet, String el) {
    	Set<String> result = new HashSet<>(originalSet);	
		result.remove(el);
    	return result;	
	}
	
}
