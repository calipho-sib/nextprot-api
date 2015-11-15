package org.nextprot.api.core.utils;

import org.nextprot.api.commons.bio.DescriptorMass;
import org.nextprot.api.commons.bio.DescriptorPI;
import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.commons.constants.PropertyApiModel;
import org.nextprot.api.commons.constants.PropertyWriter;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.Family;
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

	public static List<Annotation> getAnnotationsByCategory(Entry entry, AnnotationCategory annotationCategory) {
		return AnnotationUtils.filterAnnotationsByCategory(entry, annotationCategory, false);
	}
	
	public static boolean hasInteractions(Entry entry) {
		if (!entry.getInteractions().isEmpty()) return true;
		if (!getAnnotationsByCategory(entry, AnnotationCategory.SMALL_MOLECULE_INTERACTION).isEmpty()) return true;
		return false;
	}
	
	public static boolean hasMappings(Entry entry) {
		if ((entry.getPeptideMappings() != null) && !entry.getPeptideMappings().isEmpty()) return true;
		if ((entry.getSrmPeptideMappings() != null) && !entry.getSrmPeptideMappings().isEmpty()) return true;
		if ((entry.getAntibodyMappings() != null) && !entry.getAntibodyMappings().isEmpty()) return true;
		if ((entry.getAnnotations() != null) && !getAnnotationsByCategory(entry, AnnotationCategory.PDB_MAPPING).isEmpty()) return true;
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
}
