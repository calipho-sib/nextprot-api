package org.nextprot.api.core.utils;

import org.nextprot.api.commons.bio.DescriptorMass;
import org.nextprot.api.commons.bio.DescriptorPI;
import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.commons.constants.PropertyApiModel;
import org.nextprot.api.commons.constants.PropertyWriter;
import org.nextprot.api.core.domain.EntityName;
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
	
	private static Map<String, String> name2RdfClass = new HashMap<>();
	static {
		// clazz.name | type | qualifier 
		name2RdfClass.put("PROTEIN_NAMES | name | full",									"ProteinName");
		name2RdfClass.put("PROTEIN_NAMES | name | short", 									"ShortName");
		name2RdfClass.put("PROTEIN_NAMES | enzyme name | EC", 								"EnzymeName");
		name2RdfClass.put("GENE_NAMES | gene name | null", 									"GeneName");
		name2RdfClass.put("GENE_NAMES | open reading frame | null", 						"ORFName");
		name2RdfClass.put("ADDITIONAL_NAMES | CD antigen | CD antigen", 					"CDAntigenName");
		name2RdfClass.put("ADDITIONAL_NAMES | allergen | allergen",							"AllergenName");
		name2RdfClass.put("ADDITIONAL_NAMES | International Nonproprietary Names | INN",	"InternationalNonproprietaryName");
		name2RdfClass.put("CLEAVED_REGION_NAMES | name | full",								"CleavedRegionName");		
		name2RdfClass.put("CLEAVED_REGION_NAMES | name | short", 							"ShortName");
		name2RdfClass.put("CLEAVED_REGION_NAMES | enzyme name | EC", 						"EnzymeName");
		name2RdfClass.put("FUNCTIONAL_REGION_NAMES | region name | short",					"ShortName");
		name2RdfClass.put("FUNCTIONAL_REGION_NAMES | region name | full",					"FunctionalRegionName");
	}

	
    private NXVelocityUtils() {
        throw new AssertionError("should not be instanciable");
    }

    public static Map<String,String> parseLigandLikeProperty(String propName, String propValue) {
    	
    	// example of propValue: 
    	// CHEBI:58088 ! a 1,2-diacyl-sn-glycero-3-phospho-(1D-myo-inositol-3-phosphate)

    	Map<String,String> map = new HashMap<>();
    	if (propName.equals("ligand")) {
    		map.put("property", ":interactant");
    	} else if (propName.equals("ligandPart")) {
        	map.put("property", ":interactant");
    	} else {
    		return null;
    	}
    	
    	int pos = propValue.indexOf("!");
    	String dbac;
    	if (pos==-1) {
    		map.put("label", null);
    		dbac = propValue;
    	} else { 
    		map.put("label", propValue.substring(pos+1).trim());
        	dbac = propValue.substring(0,pos);
    	}
    	String[] elements = dbac.split(":");
    	if (elements.length==1) return null;
    	String db = elements[0].trim();
    	String ac = elements[1].trim();		
    	map.put("db", db);
    	map.put("ac", ac);
    	return map;
    }
    
	public static String getRdfClass(EntityName name) {
		String key = name.getClazz().name() + " | " + name.getType() + " | " + name.getQualifier();
		return name2RdfClass.getOrDefault(key, "Name");
	}

    public static String getRdfPropertyToEntry(EntityName name) { //, EntityName parentName) {
    	
    	final String REC = "recommendedName";
    	final String ALT = "alternativeName";
    	final String ORF = "orfName";
    	final String NAM = "name";
    	
    	String rdfClass = getRdfClass(name);
    	
    	// algorithm depends upon Name class:
		//    	protname: f(is_main)
		//    	cleaved: f(is_main)
		//    	gene:f(is_main)
		//    	enzyme: f(is_main)
		//    	allergen,f(is_main) // CDAntigen, INN, Allergen (always alternative)
		//    	short: f(parent)    OR 'alternative' => to be discussed
		//    	funct: f(has_parent)
		//    	orf: orfName

    	// most frequent class first to be a bit more efficient
    	if (rdfClass.equals("ProteinName")) return name.isMain() ? REC : ALT;
    	// --- to be discussed, pam 03.06.2022 ---
    	//if (rdfClass.equals("ShortName")) return getRdfPropertyToEntry(parentName, null);
    	if (rdfClass.equals("ShortName")) return ALT;
    	// --- end to be discussed
    	if (rdfClass.equals("CleavedRegionName")) return name.isMain() ? REC : ALT;
    	if (rdfClass.equals("GeneName")) return name.isMain() ? REC : ALT;
    	if (rdfClass.equals("EnzymeName")) return name.isMain() ? REC : ALT;
    	if (rdfClass.equals("FunctionalRegionName")) return null==name.getParentId() ? REC : ALT; 
    	if (rdfClass.equals("ORFName")) return ORF;
    	if (rdfClass.equals("CDAntigenName")) return ALT;
    	if (rdfClass.equals("AllergenName")) return ALT;
    	if (rdfClass.equals("InternationalNonproprietaryName")) return ALT;    		
    	return NAM;  // should not occur but...
    	
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
