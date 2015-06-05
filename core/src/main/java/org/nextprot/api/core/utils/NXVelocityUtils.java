package org.nextprot.api.core.utils;

import com.google.common.base.Preconditions;
import org.nextprot.api.commons.bio.DescriptorMass;
import org.nextprot.api.commons.bio.DescriptorPI;
import org.nextprot.api.commons.constants.AnnotationApiModel;
import org.nextprot.api.commons.utils.StringUtils;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.Isoform;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.utils.peff.PeffFormatterMaster;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class NXVelocityUtils {

	private static final PeffFormatterMaster PEFF_FORMATTER = new PeffFormatterMaster();

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

	/**
	 * Format PEFF header of a given isoform as string specified in PEFF developed by the HUPO PSI (PubMed:19132688)
	 *
	 * @param entry the entry to find variant from
	 * @param isoform the isoform to find variant of
	 * @param sv sequence version
	 * @param ev entry version
	 * @param pe protein existence level
	 * @return a PEFF formatted header
	 */
	public static String buildPeffHeader(Entry entry, Isoform isoform, String protName, String geneName, String sv, String ev, String pe) {

		StringBuilder sb = new StringBuilder(">nxp:");

		sb.append(isoform.getUniqueName()).append(" \\DbUniqueId=").append(isoform.getUniqueName());
		if (protName != null) sb.append("\\Pname=").append(protName);
		if (geneName != null) sb.append("\\Gname=").append(geneName);
		sb.append("\\NcbiTaxId=9606 \\TaxName=Homo Sapiens \\Length=").append(isoform.getSequence().length());
		sb.append("\\SV=").append(sv).append("\\EV=").append(ev).append("\\PE=").append(pe);
		sb.append(PEFF_FORMATTER.formatIsoformAnnotations(entry, isoform));

		return sb.toString();
	}
}
