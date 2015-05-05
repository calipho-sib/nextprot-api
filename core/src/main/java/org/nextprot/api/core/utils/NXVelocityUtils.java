package org.nextprot.api.core.utils;

import com.google.common.base.Preconditions;
import org.nextprot.api.commons.bio.DescriptorMass;
import org.nextprot.api.commons.bio.DescriptorPI;
import org.nextprot.api.commons.constants.AnnotationApiModel;
import org.nextprot.api.commons.utils.StringUtils;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.Isoform;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.utils.peff.Modification;
import org.nextprot.api.core.utils.peff.Variation;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NXVelocityUtils {

    private NXVelocityUtils() {
        throw new AssertionError();
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

	
	public static boolean hasInteractions(Entry entry) {
		if (entry.getInteractions().size()>0) return true;
		if (getAnnotationsByCategory(entry, AnnotationApiModel.SMALL_MOLECULE_INTERACTION).size()>0) return true;
		return false;
	}
	
	public static boolean hasMappings(Entry entry) {
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
	public static List<AnnotationApiModel> getAnnotationCategories() {
		List<AnnotationApiModel> list = new ArrayList<AnnotationApiModel>();
		AnnotationApiModel[] vals = AnnotationApiModel.values();
		for (int i=0;i<vals.length;i++) {
			if (vals[i].equals(AnnotationApiModel.FAMILY_NAME)) continue;
			list.add(vals[i]);
		}
		//System.out.println("cat size:" + list.size());
		return list;
	}

	public static String formatIsoformId(Isoform isoform) {

		Preconditions.checkNotNull(isoform);

		String value = "Iso 1";

		if (isoform.getMainEntityName() != null) {

			value = isoform.getMainEntityName().getValue();

			if (value.matches("\\d+"))
				value = "Iso " + value;
		}

		return value;
	}

	/**
	 * Compute isoelectric point of given isoform
	 * @param isoform isoform
	 * @return isoelectric point String
	 */
	public String getIsoelectricPointAsString(Isoform isoform) {

		Double d = DescriptorPI.compute(isoform.getSequence());
		DecimalFormat df = new DecimalFormat("#.##");

		return df.format(d);
	}

	/**
	 * Compute molecular mass of given isoform
	 * @param isoform isoform
	 * @return molecular mass String
	 */
	public String getMassAsString(Isoform isoform) {

		Double d = DescriptorMass.compute(isoform.getSequence());
		return String.valueOf(Math.round(d));
	}

	public static String getNcbiTaxonomyId(Entry entry) {

		Preconditions.checkNotNull(entry);

		return null;
	}

	public static String getNcbiTaxonomyName(Entry entry) {

		Preconditions.checkNotNull(entry);

		return null;
	}

	public static String getEntrySV(Entry entry) {

		Preconditions.checkNotNull(entry);

		return null;
	}

	public static String getEntryEV(Entry entry) {

		Preconditions.checkNotNull(entry);

		return null;
	}

	public static String getEntryProteinEvidence(Entry entry) {

		Preconditions.checkNotNull(entry);

		return null;
	}

	public static List<String> getListModificationsAsPeff(Isoform isoform) {

		Preconditions.checkNotNull(isoform);

		List<String> mods = new ArrayList<>();

		return mods;
	}

	/**
	 * Get all variants of a given isoform as string specified in PEFF developed by the HUPO PSI (PubMed:19132688)
	 *
	 * @param entry the entry to find variant from
	 * @param isoform the isoform to find variant of
	 * @return a list of Annotation of type VARIANT as PEFF format
	 */
	public static String getVariantsAsPeffString(Entry entry, Isoform isoform) {

		Preconditions.checkNotNull(entry);

		StringBuilder sb = new StringBuilder();

		for (Variation variation : getListVariant(entry, isoform)) {

			sb.append(variation.asPeff());
		}

		return sb.toString();
	}

	/**
	 * Get all modifications of a given isoform as string specified in PEFF developed by the HUPO PSI (PubMed:19132688)
	 *
	 * @param entry the entry to find modified residues from
	 * @param isoform the isoform to find modification
	 * @return a list of Annotation of type MODIFICATIONS as PEFF format
	 */
	public static String getGenericPTMsAsPeffString(Entry entry, Isoform isoform) {

		Preconditions.checkNotNull(entry);

		StringBuilder sb = new StringBuilder();

		for (Modification modif : getListGenericPTM(entry, isoform)) {

			sb.append(modif.asPeff());
		}

		return sb.toString();
	}

	static List<Variation> getListVariant(Entry entry, Isoform isoform) {

		Preconditions.checkNotNull(entry);

		List<Variation> variations = new ArrayList<>();

		for (Annotation annotation : entry.getAnnotationsByIsoform(isoform.getUniqueName())) {

			if (annotation.getAPICategory() == AnnotationApiModel.VARIANT)
				variations.add(new Variation(isoform.getUniqueName(), annotation));
		}

		Collections.sort(variations);

		return variations;
	}

	/**
	 * Get all modifications of given isoform (Kind considered are SELENOCYSTEINE, LIPIDATION_SITE, GLYCOSYLATION_SITE,
	 * CROSS_LINK. DISULFIDE_BOND, MODIFIED_RESIDUE and PTM_INFO)
	 *
	 * @param entry
	 * @param isoform
	 * @return
	 */
	static List<Modification> getListGenericPTM(Entry entry, Isoform isoform) {

		Preconditions.checkNotNull(entry);

		List<Modification> variations = new ArrayList<>();

		for (Annotation annotation : entry.getAnnotationsByIsoform(isoform.getUniqueName())) {

			if (annotation.getAPICategory().isChildOf(AnnotationApiModel.GENERIC_PTM))
				variations.add(Modification.valueOf(isoform.getUniqueName(), annotation));
		}

		Collections.sort(variations);

		return variations;
	}

}
