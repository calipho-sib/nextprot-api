package org.nextprot.api.core.utils;

import com.google.common.base.Preconditions;
import org.nextprot.api.commons.bio.DescriptorMass;
import org.nextprot.api.commons.bio.DescriptorPI;
import org.nextprot.api.commons.constants.AnnotationApiModel;
import org.nextprot.api.commons.utils.StringUtils;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.Isoform;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.utils.peff.IsoformPTM;
import org.nextprot.api.core.utils.peff.IsoformProcessingProduct;
import org.nextprot.api.core.utils.peff.IsoformVariation;

import java.text.DecimalFormat;
import java.util.ArrayList;
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
		return StringUtils.camelToKebabCase(annotationCategory.getApiTypeName());
	}
	
	public static String getAnnotationCategoryHierachyForXML(AnnotationApiModel annotationCategory) {
		StringBuffer sb = new StringBuffer();
		for (AnnotationApiModel cat: annotationCategory.getAllParentsButRoot()) {
			if (sb.length()>0) sb.append(";");
			sb.append(StringUtils.camelToKebabCase(cat.getApiTypeName()));
		}
		return sb.toString();
	}

	
	public static boolean hasInteractions(Entry entry) {
		if (!entry.getInteractions().isEmpty()) return true;
		if (!getAnnotationsByCategory(entry, AnnotationApiModel.SMALL_MOLECULE_INTERACTION).isEmpty()) return true;
		return false;
	}
	
	public static boolean hasMappings(Entry entry) {
		if ((entry.getPeptideMappings() != null) && !entry.getPeptideMappings().isEmpty()) return true;
		if ((entry.getSrmPeptideMappings() != null) && !entry.getSrmPeptideMappings().isEmpty()) return true;
		if ((entry.getAntibodyMappings() != null) && !entry.getAntibodyMappings().isEmpty()) return true;
		if ((entry.getAnnotations() != null) && !getAnnotationsByCategory(entry, AnnotationApiModel.PDB_MAPPING).isEmpty()) return true;
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

	public static String getEntrySequenceVersion(Entry entry) {

		Preconditions.checkNotNull(entry);

		return null;
	}

	public static String getEntryVersion(Entry entry) {

		Preconditions.checkNotNull(entry);

		return null;
	}

	public static int getEntryProteinExistence(Entry entry) {

		Preconditions.checkNotNull(entry);

		return 1;
	}

	public static String getProcessingProductsAsPeffString(Entry entry, Isoform isoform) {

		return IsoformProcessingProduct.getProductsAsPeffString(entry, isoform);
	}

	/**
	 * Get all variants of a given isoform as string specified in PEFF developed by the HUPO PSI (PubMed:19132688)
	 *
	 * @param entry the entry to find variant from
	 * @param isoform the isoform to find variant of
	 * @return a list of Annotation of type VARIANT as PEFF format
	 */
	public static String getVariantsAsPeffString(Entry entry, Isoform isoform) {

		return IsoformVariation.getVariantsAsPeffString(entry, isoform);
	}

	/**
	 * Get all modifications of a given isoform as string specified in PEFF developed by the HUPO PSI (PubMed:19132688)
	 *
	 * @param entry the entry to find modified residues from
	 * @param isoform the isoform to find modification
	 * @return a list of Annotation of type MODIFICATIONS as PEFF format
	 */
	public static String getPsiPTMsAsPeffString(Entry entry, Isoform isoform) {

		return IsoformPTM.getPsiPTMsAsPeffString(entry, isoform);
	}

	public static String getNoPsiPTMsAsPeffString(Entry entry, Isoform isoform) {

		return IsoformPTM.getNoPsiPTMsAsPeffString(entry, isoform);
	}
}
