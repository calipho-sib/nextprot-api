package org.nextprot.api.commons.constants;

import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.commons.utils.StringUtils;

import java.io.Serializable;
import java.util.*;

/**
 * Description: <br> * 
 * @author Pam inspired from Oliv's OWLAnnotationCategoryOld version <br>
 */

public enum AnnotationApiModel implements Serializable {

	//Special node for the root
	ROOT (0, "Root", "Root","",null),

	/*
	 * ENUMs with a negative dbId are virtual annotation types. Virtual means that there is NO annotation in our data of this type
	 * ENUMs with a positive dbId are annotation types attached to at least one annotation in our data  
	 */

	// names 
	NAME(-100, "Name", "name","Name", ROOT),
	// ENZYME_CLASSIFICATION and FAMILY_NAME temporarily appear in the entry overview via another mechanism
		FAMILY_NAME(1059,"family name", "familyName", "family name", NAME), 

	// generic categories for annotations
	
		POSITIONAL_ANNOTATION(-3, "PositionalAnnotation", "positionalAnnotation", "Positional annotation", ROOT),
			PROCESSING_PRODUCT(-4, "ProcessingProduct", "processingProduct","Processing product", POSITIONAL_ANNOTATION ),
			TOPOLOGY(-5, "Topology", "topology","Topology", POSITIONAL_ANNOTATION ),
			REGION(-6, "Region", "region","Region", POSITIONAL_ANNOTATION ),
			GENERIC_SITE(-7, "GenericSite", "site","Site", POSITIONAL_ANNOTATION ),
			GENERIC_PTM(-8, "GenericPtm", "ptm","PTM", POSITIONAL_ANNOTATION ),
			SECONDARY_STRUCTURE(-9, "secondary structure", "secondaryStructure","Secondary structure",  POSITIONAL_ANNOTATION ),
			MAPPING(-91, "GenericMapping", "mapping","Mapping", POSITIONAL_ANNOTATION ),
			
		GENERAL_ANNOTATION(-2, "GeneralAnnotation", "generalAnnotation","General Annotation", ROOT ),
			GENERIC_FUNCTION(-10, "GenericFunction", "function","Function",  GENERAL_ANNOTATION ),
			GENERIC_INTERACTION(-11, "GenericInteraction", "interaction","Interaction",  GENERAL_ANNOTATION ),
			CELLULAR_COMPONENT(-12, "GenericLocation", "cellularComponent","Cellular component",  GENERAL_ANNOTATION ),
			GENERIC_EXPRESSION(-15, "GenericExpression", "expression","Expression",  GENERAL_ANNOTATION ),
			MEDICAL(-13, "Medical", "medical","Medical",  GENERAL_ANNOTATION ),
			KEYWORD(-14, "Keyword", "keyword","Keywords",  GENERAL_ANNOTATION ),

			//TEST1(-1111,"test1","test1","test1", new OWLAnnotationCategory[]{POSITIONAL_ANNOTATION, GENERAL_ANNOTATION}),
			
			// ENZYME_CLASSIFICATION and FAMILY_NAME temporarily appear in the entry overview via another mechanism
			ENZYME_CLASSIFICATION(1065,"enzyme classification", "enzymeClassification", "enzyme classification", GENERAL_ANNOTATION ),
    
	
	// instantiated annotation categories with real cv_term id and data existing for them			

    // instances of positional annotations
			
    /*
     * Transformations done on loading annotations from db:
     * OK - 1/ dbId=1002 "transit peptide": split annotations into 2 new types "mitochondrial transit peptide" and "peroxysome trasit peptide"
     * OK - 2/ dbId=1005 "transmembrane region": move annotations to new type "intramembrane region" if annotation.cv_term_id=51748 "In membrane"
     * OK - 3/ dbId=1050 "biotechnology": move all annotations to existing type dbId=1052 "Miscellaneous"
     */
			
	PDB_MAPPING(116892,"3D structure", "pdbMapping", "PDB mapping", MAPPING ),
			
	NON_CONSECUTIVE_RESIDUE(1031,"non-consecutive residues", "nonConsecutiveResidue", "Non-consecutive residue",  POSITIONAL_ANNOTATION ),
	NON_TERMINAL_RESIDUE(1032,"non-terminal residue", "nonTerminalResidue", "Non-terminal residue",  POSITIONAL_ANNOTATION ),
	DOMAIN_INFO(1043,"domain information", "domainInfo", "Domain information",  POSITIONAL_ANNOTATION ),
		
	INITIATOR_METHIONINE(1000,"initiator methionine", "initiatorMethionine", "Initiator methionine", PROCESSING_PRODUCT ),
	SIGNAL_PEPTIDE(1001,"signal peptide", "signalPeptide", "signal peptide", PROCESSING_PRODUCT ),
	//TRANSIT_PEPTIDE(1002,"transit peptide", "transitPeptide", "transit peptide", new OWLAnnotationCategory[]{PROCESSING_PRODUCT }), // split into mitochondrial & peroxisome // db annotation split into types of 2 next lines
	PEROXISOME_TRANSIT_PEPTIDE(-10021,"peroxisome transit peptide", "peroxisomeTransitPeptide", "Peroxisome transit peptide", PROCESSING_PRODUCT ),
	MITOCHONDRIAL_TRANSIT_PEPTIDE(-10022,"mitochondrial transit peptide", "mitochondrialTransitPeptide", "Mitochondrial transit peptide", PROCESSING_PRODUCT ),
	MATURATION_PEPTIDE(1003,"maturation peptide", "propeptide", "maturation peptide", PROCESSING_PRODUCT ),
	MATURE_PROTEIN(1004,"mature protein", "matureProtein", "mature protein", PROCESSING_PRODUCT ),
	
	TRANSMEMBRANE_REGION(1005,"transmembrane region", "transmembraneRegion", "transmembrane region",  TOPOLOGY ),
	INTRAMEMBRANE_REGION(-10051,"intramembrane region", "intramembraneRegion", "intramembrane region",  TOPOLOGY ), // Note: this annotation type does not exist in db, it is considered a transmembrane region but is linked to the cv_term = "In membrane"
	TOPOLOGICAL_DOMAIN(1015,"topological domain", "topologicalDomain", "topological domain", TOPOLOGY ),
	
	DOMAIN(1006,"domain", "domain", "domain", REGION ),
	REPEAT(1007,"repeat", "repeat", "repeat",  REGION ),
	CALCIUM_BINDING_REGION(1008,"calcium-binding region", "calciumBindingRegion", "calcium-binding region",  REGION ),
	ZINC_FINGER_REGION(1009,"zinc finger region", "zincFingerRegion", "zinc finger region",  REGION ),
	DNA_BINDING_REGION(1010,"DNA-binding region", "dnaBindingRegion", "DNA-binding region",  REGION ),
	NUCLEOTIDE_PHOSPHATE_BINDING_REGION(1011,"nucleotide phosphate-binding region", "nucleotidePhosphateBindingRegion", "nucleotide phosphate-binding region",  REGION ),
	COILED_COIL_REGION(1012,"coiled-coil region", "coiledCoilRegion", "coiled-coil region",  REGION ),
	SHORT_SEQUENCE_MOTIF(1013,"short sequence motif", "shortSequenceMotif", "short sequence motif",  REGION ),
	COMPOSITIONALLY_BIASED_REGION(1014,"compositionally biased region", "compositionallyBiasedRegion", "compositionally biased region",  REGION ),
	MISCELLANEOUS_REGION(11,"region of interest", "miscellaneousRegion", "miscellaneous region", REGION ),
	INTERACTING_REGION(1068,"interacting region", "interactingRegion", "interacting region", REGION ),
	
	ACTIVE_SITE(1016,"active site", "activeSite", "active site", GENERIC_SITE ),
	METAL_BINDING_SITE(1017,"metal ion-binding site", "metalBindingSite", "metal binding site", GENERIC_SITE ),
	BINDING_SITE(1018,"binding site", "bindingSite", "binding site", GENERIC_SITE ),
	CLEAVAGE_SITE(1067,"cleavage site", "cleavageSite", "cleavage site", GENERIC_SITE ),
	MISCELLANEOUS_SITE(12,"site", "miscellaneousSite", "miscellaneous site", GENERIC_SITE ),
		
	SELENOCYSTEINE(1019,"non-standard amino acid", "selenocysteine", "selenocysteine", GENERIC_PTM ),
	LIPIDATION_SITE(1020,"lipid moiety-binding region", "lipidationSite", "lipid moiety-binding region", GENERIC_PTM ),
	GLYCOSYLATION_SITE(1021,"glycosylation site", "glycosylationSite", "glycosylation site", GENERIC_PTM ),
	CROSS_LINK(1023,"cross-link", "crossLink", "cross-link", GENERIC_PTM ),
	DISULFIDE_BOND(1022,"disulfide bond", "disulfideBond", "disulfide bond", GENERIC_PTM ),
	MODIFIED_RESIDUE(13,"amino acid modification", "modifiedResidue", "modified residue", GENERIC_PTM ),
	PTM_INFO(1044,"PTM", "ptmInfo", "PTM info", GENERIC_PTM ),

	HELIX(1024,"helix", "helix", "helix", SECONDARY_STRUCTURE ),
	TURN(1025,"turn", "turn", "turn", SECONDARY_STRUCTURE ),
	BETA_STRAND(1026,"beta strand", "betaStrand", "beta strand", SECONDARY_STRUCTURE ),
	
	VARIANT(1027,"sequence variant", "variant", "variant", POSITIONAL_ANNOTATION ),
	VARIANT_INFO(1045,"polymorphism", "variantInfo", "VariantInfo", POSITIONAL_ANNOTATION ),

	MUTAGENESIS(1028,"mutagenesis site", "mutagenesis", "mutagenesis", POSITIONAL_ANNOTATION ),
	SEQUENCE_CONFLICT(1029,"sequence conflict", "sequenceConflict", "sequence conflict", POSITIONAL_ANNOTATION ),
	
	// instances of general annotations
	
	INDUCTION(1042,"induction", "induction", "induction", GENERAL_ANNOTATION ),
	//BIOTECHNOLOGY(1050,"biotechnology", "biotechnology", "biotechnology", new OWLAnnotationCategory[]{GENERAL_ANNOTATION }),  // OK: only 5 annotations exist, so moved to miscellaneous
	MISCELLANEOUS(1052,"miscellaneous", "miscellaneous", "miscellaneous", GENERAL_ANNOTATION ),
	CAUTION(1054,"caution", "caution", "caution", GENERAL_ANNOTATION ),
	SEQUENCE_CAUTION(1056,"sequence caution", "sequenceCaution", "sequence caution", GENERAL_ANNOTATION ),
	UNIPROT_KEYWORD(1064,"uniprot keyword", "uniprotKeyword", "uniprot keyword", KEYWORD ),
	
	FUNCTION_INFO(1033,"function", "functionInfo", "function info",  GENERIC_FUNCTION ),
	CATALYTIC_ACTIVITY(1034,"catalytic activity", "catalyticActivity", "catalytic activity", GENERIC_FUNCTION ),
	COFACTOR(1035,"cofactor", "cofactor", "cofactor", GENERIC_INTERACTION ),
	ENZYME_REGULATION(1036,"enzyme regulation", "enzymeRegulation", "enzyme regulation", GENERIC_INTERACTION),
	PATHWAY(1038,"pathway", "pathway", "pathway", GENERIC_FUNCTION ),
	GO_MOLECULAR_FUNCTION(1061,"go molecular function", "goMolecularFunction", "go molecular function", GENERIC_FUNCTION ),
	GO_BIOLOGICAL_PROCESS(1062,"go biological process", "goBiologicalProcess", "go biological process", GENERIC_FUNCTION ),
	
	SMALL_MOLECULE_INTERACTION(-112,"SmallMoleculeInteraction", "smallMoleculeInteraction", "Small molecule interaction", GENERIC_INTERACTION ),
	INTERACTION_INFO(1037,"subunit", "interactionInfo", "interaction info", GENERIC_INTERACTION ),
	BINARY_INTERACTION(-111,"BinaryInteraction", "binaryInteraction", "binary interaction", GENERIC_INTERACTION ), // placeholder for data coming from intact in table db partnership
	
	
	SUBCELLULAR_LOCATION(1039,"subcellular location", "subcellularLocation", "subcellular location", CELLULAR_COMPONENT ),
	SUBCELLULAR_LOCATION_NOTE(63868,"subcellular location info", "subcellularLocationNote", "subcellular location info", CELLULAR_COMPONENT ),
	GO_CELLULAR_COMPONENT(1063,"go cellular component", "goCellularComponent", "go cellular component", CELLULAR_COMPONENT ),

	DEVELOPMENTAL_STAGE(1041,"developmental stage", "developmentalStageInfo", "developmental stage", GENERIC_EXPRESSION ),
	EPRESSION_INFO(1055,"expression info", "expressionInfo", "expression info", GENERIC_EXPRESSION ),
	EPRESSION_PROFILE(1040,"tissue specificity", "expressionProfile", "expression profile (tissue specificity)", GENERIC_EXPRESSION ),

	DISEASE(1046,"disease", "disease", "disease", MEDICAL ),
	ALLERGEN(1048,"allergen", "allergen", "allergen", MEDICAL ),
	PHARMACEUTICAL(1051,"pharmaceutical", "pharmaceutical", "pharmaceutical", MEDICAL ),

	;

	private final Integer dbId; // if positive, identifies a real record of the table nextprot.cv_terms (category annotation_type)
	private final String dbAnnotationTypeName; // if dbId is positive, dbAnnotationTypeName is an exact match of the corresponding record in nextprot.cv_terms 
	private final String apiName; // a string from which an rdf predicate and an rdfs:type name is derived 
	private final String rdfLabel; // a human readable label for the rdf:type
	private String description=null; // may be set later from reading values in the db 
	
	private final AnnotationApiModel parent;
	
	/** Category of control vocabulary that may be used to define the annotation */
	AnnotationApiModel(
			final Integer dbId,
			final String dbAnnotationTypeName,
			final String rdfName,
			final String rdfLabel,
			final AnnotationApiModel parent) {
		
		this.dbId = dbId;
		this.dbAnnotationTypeName = dbAnnotationTypeName;
		this.apiName = rdfName;
		this.rdfLabel = rdfLabel;
		this.parent = parent;
	}

	// *************** STATIC PRIVATE FINAL CONSTANTS initialized for performance reasons ********************************** ///////////////////
	
	// Fill the cache
	private static final Map<String,AnnotationApiModel> MAP_TYPES =new HashMap<>();
	static {for (AnnotationApiModel category : AnnotationApiModel.values()) {MAP_TYPES.put(category.getDbAnnotationTypeName(), category);}}
	
	// Fill the cache decamelized
	private static Map<String,AnnotationApiModel> MAP_DECAMELIZED_TYPES=new HashMap<>();
	static {for (AnnotationApiModel category : AnnotationApiModel.values()) {MAP_DECAMELIZED_TYPES.put(StringUtils.camelToKebabCase(category.getApiTypeName()), category);}	}

	private static String HIERARCHY_STRING = null;
	static {StringBuilder sb = new StringBuilder();getAnnotationHierarchy(AnnotationApiModel.ROOT, sb, 0);HIERARCHY_STRING = sb.toString(); }
	private static void getAnnotationHierarchy(AnnotationApiModel a, StringBuilder sb, int inc) {
		if(inc > 0) sb.append(new String(new char[inc]).replace('\0', '-') + StringUtils.camelToKebabCase(a.getApiTypeName()) + "  " + a.getHierarchy() + "\n");
		int nextInc = inc + 1;
		for (AnnotationApiModel c : a.getChildren()) {
				getAnnotationHierarchy(c, sb, nextInc);
		}
	}

	private static List<AnnotationApiModel> SORTED_CATEGORIES = null;
	static { SORTED_CATEGORIES = sortAnnotationCategories(); }

	/**
	 * Sort categories (generic parent > direct parent annotation > annotation category name
	 * @return the list of LEAF annotation categories except family-name
	 */
	private static List<AnnotationApiModel> sortAnnotationCategories() {

		List<AnnotationApiModel> list = new ArrayList<>();
		AnnotationApiModel[] vals = AnnotationApiModel.values();

		for (AnnotationApiModel value : vals) {

			if (!value.equals(AnnotationApiModel.FAMILY_NAME)) list.add(value);
		}

		Collections.sort(list, new Comparator<AnnotationApiModel>() {

			@Override
			public int compare(AnnotationApiModel m1, AnnotationApiModel m2) {

				int cmp = m1.getHierarchy().compareTo(m2.getHierarchy());

				if (cmp == 0)
					return m1.getApiTypeName().compareTo(m2.getApiTypeName());

				return cmp;
			}
		});

		return list;
	}

	public static List<AnnotationApiModel> getSortedCategories() {

		return SORTED_CATEGORIES;
	}

	public static Set<AnnotationApiModel> getInstantiatedCategories() {
		Set<AnnotationApiModel> set = new HashSet<>();
		for (AnnotationApiModel cat: AnnotationApiModel.values()) {
			if (cat.isInstantiated()) set.add(cat);
		}
		return set;
	}

	public static AnnotationApiModel getByDbAnnotationTypeName(String typeName) {
		if (MAP_TYPES.containsKey(typeName)) {
			return MAP_TYPES.get(typeName);
		}else throw new NextProtException("\nCould not find annotation category for: "+ typeName + "\nPossible types: \n" + MAP_TYPES.keySet());
	}


	public static AnnotationApiModel getDecamelizedAnnotationTypeName(String typeName) {
		String typeNameInLowerCase = typeName.toLowerCase();
		if (MAP_DECAMELIZED_TYPES.containsKey(typeNameInLowerCase)) {
			return MAP_DECAMELIZED_TYPES.get(typeNameInLowerCase);
		} else {
			throw new NextProtException("\nCould not find annotation category for: " + typeName + "\nPossible types: \n" + HIERARCHY_STRING);
		}
	}
	
		
	/**
	 * Tells if this annotation category is used in the field cv_annotation_type_id of an annotations record
	 * @return true if at least one annotation record has this dbAnnotationTypeName otherwise false 
	 */
	public boolean isInstantiated() {
		return (dbId>0);
	}	
	
	public Integer getDbId() {
		return dbId;
	}
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String descr) {
		this.description=descr;
	}

	public String getDbAnnotationTypeName() {
		return dbAnnotationTypeName;
	}
	
	public String getRdfPredicate() {
		return StringUtils.lowerFirstChar(this.apiName);
	}
	
	@Deprecated //TODO change this for something more generic like #getApiTypeName, since it may be highly used in RDF templates I leave it for now
	public String getRdfTypeName() {
		return getApiTypeName();
	}
	
	public String getApiTypeName() {
		return StringUtils.upperFirstChar(this.apiName);
	}
		
	@Deprecated
	public String getRdfLabel() {
		return StringUtils.upperFirstChar(this.rdfLabel);
	}
	
	public AnnotationApiModel getParent() {
		return parent;
	}
	
	public Set<AnnotationApiModel> getChildren() {
		Set<AnnotationApiModel> children = new HashSet<>();
		for (AnnotationApiModel cat : AnnotationApiModel.values()) {
			if (cat.parent == this) children.add(cat);
		}
		return children;
	}
	
	public Set<AnnotationApiModel> getAllChildren() {
		Set<AnnotationApiModel> mine = getChildren();
		Set<AnnotationApiModel> all = new HashSet<>(mine);
		for (AnnotationApiModel child : mine) all.addAll(child.getAllChildren());
		return all;
	}
	
	public Set<AnnotationApiModel> getAllParents() {
		Set<AnnotationApiModel> all = new HashSet<>();

		if (parent != null) {
			all.add(parent);
			all.addAll(parent.getAllParents());
		}
		return all;
	}
	
	public Set<AnnotationApiModel> getAllParentsButRoot() {
		Set<AnnotationApiModel> all = getAllParents();
		all.remove(AnnotationApiModel.ROOT);
		return all;
	}
	
	public boolean isChildOf(AnnotationApiModel aam) {
		return aam.getAllChildren().contains(this);
	}
	
	public String getHierarchy() {

		return getPathToRoot(':');
	}

	public String getPathToRoot(char delimitor) {

		StringBuilder sb = new StringBuilder();
		getPathToRoot(sb, delimitor);

		if (sb.length() > 0) sb.delete(sb.length()-1, sb.length());

		return sb.toString();
	}

	void getPathToRoot(StringBuilder sb, char delimitor){

		if (parent!= null && parent != ROOT) {
			parent.getPathToRoot(sb, delimitor);

			sb.append(StringUtils.camelToKebabCase(parent.getDbAnnotationTypeName()) + delimitor);
		}
	}
	
	public boolean isLeaf() {
		return getChildren().isEmpty();
	}
	
	public String toString() {
		return /*this.getDbId() + " : " +*/ this.getDbAnnotationTypeName();
	}
	
	public Set<AnnotationPropertyApiModel> getProperties() {
		return AnnotationPropertyApiModel.getPropertySet(this);
	}
	public AnnotationPropertyApiModel getPropertyByDbName(String dbName) {
		return AnnotationPropertyApiModel.getPropertyByDbName(this, dbName);
	}

	public String getAnnotationCategoryHierarchyForXML() {

		return getPathToRoot(';');
	}

	public String getAnnotationCategoryNameForXML() {
		return StringUtils.camelToKebabCase(getApiTypeName());
	}
}
