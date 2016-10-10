package org.nextprot.api.core.utils;

import org.nextprot.api.commons.bio.DescriptorMass;
import org.nextprot.api.commons.bio.DescriptorPI;
import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.commons.constants.PropertyApiModel;
import org.nextprot.api.commons.constants.PropertyWriter;
import org.nextprot.api.core.domain.*;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.utils.annot.AnnotationUtils;
import org.nextprot.api.core.utils.peff.PeffFormatterMaster;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NXVelocityUtils {
	
	private static final PeffFormatterMaster PEFF_FORMATTER = new PeffFormatterMaster();

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
	 * @return a PEFF formatted header
	 */
	public static String buildPeffHeader(Entry entry, Isoform isoform) {

		StringBuilder sb = new StringBuilder().append(isoform.getUniqueName())
				.append(" \\DbUniqueId=").append(isoform.getUniqueName());

		Overview overview = entry.getOverview();

		if (overview.hasMainProteinName())
			sb.append("\\Pname=").append(overview.getMainProteinName());
		if (overview.hasMainGeneName())
			sb.append("\\Gname=").append(overview.getMainGeneName());

		sb.append("\\NcbiTaxId=9606 \\TaxName=Homo Sapiens \\Length=").append(isoform.getSequence().length())
				.append("\\SV=").append(overview.getHistory().getSequenceVersion())
				.append("\\EV=").append(overview.getHistory().getUniprotVersion())
				.append("\\PE=").append(overview.getProteinExistenceLevel());

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
