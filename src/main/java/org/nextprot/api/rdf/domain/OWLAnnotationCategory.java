package org.nextprot.api.rdf.domain;

import java.util.HashSet;
import java.util.HashMap;
import java.util.Set;
import java.util.Map;

import org.nextprot.api.commons.utils.StringUtils;

/**
 * Description: <br> * 
 * @author Pam inspired from Oliv's OWLAnnotationCategoryOld version <br>
 */

public enum OWLAnnotationCategory  {
	
	/*
	 * ENUMs with a negative dbId are virtual annotation types. Virtual means that there is NO annotation in our data of this type
	 * ENUMs with a positive dbId are annotation types attached to at least one annotation in our data  
	 */

	// names 
	NAME(-100, "Name", "name","Name",null),
		FAMILY_NAME(1059,"family name", "familyName", "family name", new OWLAnnotationCategory[]{ NAME }),
		ENZYME_CLASSIFICATION(1065,"enzyme classification", "enzymeClassification", "enzyme classification", new OWLAnnotationCategory[]{ NAME }),

	// generic categories for annotations
	
		POSITIONAL_ANNOTATION(-3, "PositionalAnnotation", "positionalAnnotation", "Positional annotation", null),
			PROCESSING_PRODUCT(-4, "ProcessingProduct", "processingProduct","Processing product", new OWLAnnotationCategory[]{ POSITIONAL_ANNOTATION }),
			TOPOLOGY(-5, "Topology", "topology","Topology", new OWLAnnotationCategory[]{ POSITIONAL_ANNOTATION }),
			REGION(-6, "Region", "region","Region", new OWLAnnotationCategory[]{ POSITIONAL_ANNOTATION }),
			GENERIC_SITE(-7, "GenericSite", "site","Site", new OWLAnnotationCategory[]{ POSITIONAL_ANNOTATION }),
			GENERIC_PTM(-8, "GenericPtm", "ptm","PTM", new OWLAnnotationCategory[]{ POSITIONAL_ANNOTATION }),
			SECONDARY_STRUCTURE(-9, "secondary structure", "secondaryStructure","Secondary structure", new OWLAnnotationCategory[]{  POSITIONAL_ANNOTATION }),
			MAPPING(-91, "GenericMapping", "mapping","Mapping", new OWLAnnotationCategory[]{ POSITIONAL_ANNOTATION }),
			
		GENERAL_ANNOTATION(-2, "GeneralAnnotation", "generalAnnotation","General Annotation", null ),
			GENERIC_FUNCTION(-10, "GenericFunction", "function","Function", new OWLAnnotationCategory[]{  GENERAL_ANNOTATION }),
			GENERIC_INTERACTION(-11, "GenericInteraction", "interaction","Interaction", new OWLAnnotationCategory[]{  GENERAL_ANNOTATION }),
			GENERIC_LOCATION(-12, "GenericLocation", "location","Location", new OWLAnnotationCategory[]{  GENERAL_ANNOTATION }),
			MEDICAL(-13, "Medical", "medical","Medical", new OWLAnnotationCategory[]{  GENERAL_ANNOTATION }),

			//TEST1(-1111,"test1","test1","test1", new OWLAnnotationCategory[]{POSITIONAL_ANNOTATION, GENERAL_ANNOTATION}),
			
    
	
	// instantiated annotation categories with real cv_term id and data existing for them			

    // instances of positional annotations
			
    /*
     * Transformations done on loading annotations from db:
     * OK - 1/ dbId=1002 "transit peptide": split annotations into 2 new types "mitochondrial transit peptide" and "peroxysome trasit peptide"
     * OK - 2/ dbId=1005 "transmembrane region": move annotations to new type "intramembrane region" if annotation.cv_term_id=51748 "In membrane"
     * OK - 3/ dbId=1050 "biotechnology": move all annotations to existing type dbId=1052 "Miscellaneous"
     */
			
	PDB_MAPPING(116892,"3D structure", "pdbMapping", "PDB mapping", new OWLAnnotationCategory[]{ MAPPING }),
			
	NON_CONSECUTIVE_RESIDUE(1031,"non-consecutive residues", "nonConsecutiveResidue", "Non-consecutive residue", new OWLAnnotationCategory[]{  POSITIONAL_ANNOTATION }),
	NON_TERMINAL_RESIDUE(1032,"non-terminal residue", "nonTerminalResidue", "Non-terminal residue", new OWLAnnotationCategory[]{  POSITIONAL_ANNOTATION }),
	DOMAIN_INFO(1043,"domain information", "domainInfo", "Domain information", new OWLAnnotationCategory[]{  POSITIONAL_ANNOTATION }),
		
	INITIATOR_METHIONINE(1000,"initiator methionine", "initiatorMethionine", "Initiator methionine", new OWLAnnotationCategory[]{ PROCESSING_PRODUCT }),	
	SIGNAL_PEPTIDE(1001,"signal peptide", "signalPeptide", "signal peptide", new OWLAnnotationCategory[]{ PROCESSING_PRODUCT }),
	//TRANSIT_PEPTIDE(1002,"transit peptide", "transitPeptide", "transit peptide", new OWLAnnotationCategory[]{PROCESSING_PRODUCT }), // split into mitochondrial & peroxisome // db annotation split into types of 2 next lines
	PEROXISOME_TRANSIT_PEPTIDE(-10021,"peroxisome transit peptide", "peroxisomeTransitPeptide", "Peroxisome transit peptide", new OWLAnnotationCategory[]{PROCESSING_PRODUCT }),
	MITOCHONDRIAL_TRANSIT_PEPTIDE(-10022,"mitochondrial transit peptide", "mitochondrialTransitPeptide", "Mitochondrial transit peptide", new OWLAnnotationCategory[]{PROCESSING_PRODUCT }), 
	MATURATION_PEPTIDE(1003,"maturation peptide", "maturationPeptide", "maturation peptide", new OWLAnnotationCategory[]{PROCESSING_PRODUCT }),
	MATURE_PROTEIN(1004,"mature protein", "matureProtein", "mature protein", new OWLAnnotationCategory[]{PROCESSING_PRODUCT }),
	
	TRANSMEMBRANE_REGION(1005,"transmembrane region", "transmembraneRegion", "transmembrane region",  new OWLAnnotationCategory[]{TOPOLOGY }), 
	INTRAMEMBRANE_REGION(-10051,"intramembrane region", "intramembraneRegion", "intramembrane region",  new OWLAnnotationCategory[]{TOPOLOGY }), // Note: this annotation type does not exist in db, it is considered a transmembrane region but is linked to the cv_term = "In membrane"
	TOPOLOGICAL_DOMAIN(1015,"topological domain", "topologicalDomain", "topological domain", new OWLAnnotationCategory[]{TOPOLOGY }),
	
	DOMAIN(1006,"domain", "domain", "domain", new OWLAnnotationCategory[]{REGION }),
	REPEAT(1007,"repeat", "repeat", "repeat",  new OWLAnnotationCategory[]{REGION }),
	CALCIUM_BINDING_REGION(1008,"calcium-binding region", "calciumBindingRegion", "calcium-binding region",  new OWLAnnotationCategory[]{REGION }),
	ZINC_FINGER_REGION(1009,"zinc finger region", "zincFingerRegion", "zinc finger region",  new OWLAnnotationCategory[]{REGION }),
	DNA_BINDING_REGION(1010,"DNA-binding region", "dnaBindingRegion", "DNA-binding region",  new OWLAnnotationCategory[]{REGION }),
	NUCLEOTIDE_PHOSPHATE_BINDING_REGION(1011,"nucleotide phosphate-binding region", "nucleotidePhosphateBindingRegion", "nucleotide phosphate-binding region",  new OWLAnnotationCategory[]{REGION }),
	COILED_COIL_REGION(1012,"coiled-coil region", "coiledCoilRegion", "coiled-coil region",  new OWLAnnotationCategory[]{REGION }),
	SHORT_SEQUENCE_MOTIF(1013,"short sequence motif", "shortSequenceMotif", "short sequence motif",  new OWLAnnotationCategory[]{REGION }),
	COMPOSITIONALLY_BIASED_REGION(1014,"compositionally biased region", "compositionallyBiasedRegion", "compositionally biased region",  new OWLAnnotationCategory[]{REGION }),
	MISCELLANEOUS_REGION(11,"region of interest", "miscellaneousRegion", "miscellaneous region", new OWLAnnotationCategory[]{REGION }),
	INTERACTING_REGION(1068,"interacting region", "interactingRegion", "interacting region", new OWLAnnotationCategory[]{REGION }),
	
	ACTIVE_SITE(1016,"active site", "activeSite", "active site", new OWLAnnotationCategory[]{GENERIC_SITE }),
	METAL_BINDING_SITE(1017,"metal ion-binding site", "metalBindingSite", "metal binding site", new OWLAnnotationCategory[]{GENERIC_SITE }),
	BINDING_SITE(1018,"binding site", "bindingSite", "binding site", new OWLAnnotationCategory[]{GENERIC_SITE }),
	CLEAVAGE_SITE(1067,"cleavage site", "cleavageSite", "cleavage site", new OWLAnnotationCategory[]{GENERIC_SITE, PROCESSING_PRODUCT }), // Note: 2 parents
	MISCELLANEOUS_SITE(12,"site", "miscellaneousSite", "miscellaneous site", new OWLAnnotationCategory[]{GENERIC_SITE }), 
		
	SELENOCYSTEINE(1019,"non-standard amino acid", "selenocysteine", "selenocysteine", new OWLAnnotationCategory[]{GENERIC_PTM }),
	LIPIDATION_SITE(1020,"lipid moiety-binding region", "lipidationSite", "lipid moiety-binding region", new OWLAnnotationCategory[]{GENERIC_PTM }),
	GLYCOSYLATION_SITE(1021,"glycosylation site", "glycosylationSite", "glycosylation site", new OWLAnnotationCategory[]{GENERIC_PTM }),
	CROSS_LINK(1023,"cross-link", "crossLink", "cross-link", new OWLAnnotationCategory[]{GENERIC_PTM }),
	DISULFIDE_BOND(1022,"disulfide bond", "disulfideBond", "disulfide bond", new OWLAnnotationCategory[]{GENERIC_PTM }),
	MODIFIED_RESIDUE(13,"amino acid modification", "modifiedResidue", "modified residue", new OWLAnnotationCategory[]{GENERIC_PTM }),
	PTM_INFO(1044,"PTM", "ptmInfo", "PTM info", new OWLAnnotationCategory[]{GENERIC_PTM }),

	HELIX(1024,"helix", "helix", "helix", new OWLAnnotationCategory[]{SECONDARY_STRUCTURE }),
	TURN(1025,"turn", "turn", "turn", new OWLAnnotationCategory[]{SECONDARY_STRUCTURE }),
	BETA_STRAND(1026,"beta strand", "betaStrand", "beta strand", new OWLAnnotationCategory[]{SECONDARY_STRUCTURE }),
	
	VARIANT(1027,"sequence variant", "variant", "variant", new OWLAnnotationCategory[]{POSITIONAL_ANNOTATION }),
	VARIANT_INFO(1045,"polymorphism", "variantInfo", "VariantInfo", new OWLAnnotationCategory[]{POSITIONAL_ANNOTATION }),

	MUTAGENESIS(1028,"mutagenesis site", "mutagenesis", "mutagenesis", new OWLAnnotationCategory[]{POSITIONAL_ANNOTATION }),
	SEQUENCE_CONFLICT(1029,"sequence conflict", "sequenceConflict", "sequence conflict", new OWLAnnotationCategory[]{POSITIONAL_ANNOTATION }),
	
	// instances of general annotations
	
	INDUCTION(1042,"induction", "induction", "induction", new OWLAnnotationCategory[]{GENERAL_ANNOTATION }),
	//BIOTECHNOLOGY(1050,"biotechnology", "biotechnology", "biotechnology", new OWLAnnotationCategory[]{GENERAL_ANNOTATION }),  // OK: only 5 annotations exist, so moved to miscellaneous
	MISCELLANEOUS(1052,"miscellaneous", "miscellaneous", "miscellaneous", new OWLAnnotationCategory[]{GENERAL_ANNOTATION }),
	CAUTION(1054,"caution", "caution", "caution", new OWLAnnotationCategory[]{GENERAL_ANNOTATION }),
	SEQUENCE_CAUTION(1056,"sequence caution", "sequenceCaution", "sequence caution", new OWLAnnotationCategory[]{GENERAL_ANNOTATION }),
	UNIPROT_KEYWORD(1064,"uniprot keyword", "uniprotKeyword", "uniprot keyword", new OWLAnnotationCategory[]{GENERAL_ANNOTATION }),  // TODO: check with Amos if is a general annotation
	
	FUNCTION_INFO(1033,"function", "functionInfo", "function info",  new OWLAnnotationCategory[]{GENERIC_FUNCTION }),
	CATALYTIC_ACTIVITY(1034,"catalytic activity", "catalyticActivity", "catalytic activity", new OWLAnnotationCategory[]{GENERIC_FUNCTION }),
	COFACTOR(1035,"cofactor", "cofactor", "cofactor", new OWLAnnotationCategory[]{GENERIC_FUNCTION, GENERIC_INTERACTION }),  // Note: 2 parents
	ENZYME_REGULATION(1036,"enzyme regulation", "enzymeRegulation", "enzyme regulation", new OWLAnnotationCategory[]{GENERIC_FUNCTION, GENERIC_INTERACTION}), // Note: 2 parents
	PATHWAY(1038,"pathway", "pathway", "pathway", new OWLAnnotationCategory[]{GENERIC_FUNCTION }),
	GO_MOLECULAR_FUNCTION(1061,"go molecular function", "goMolecularFunction", "go molecular function", new OWLAnnotationCategory[]{GENERIC_FUNCTION }),
	GO_BIOLOGICAL_PROCESS(1062,"go biological process", "goBiologicalProcess", "go biological process", new OWLAnnotationCategory[]{GENERIC_FUNCTION }),
	
	SMALL_MOLECULE_INTERACTION(-112,"SmallMoleculeInteraction", "smallMoleculeInteraction", "Small molecule interaction", new OWLAnnotationCategory[]{GENERIC_INTERACTION, MEDICAL }), // Note: DrugBank xref, 2 parents
	INTERACTION_INFO(1037,"subunit", "interactionInfo", "interaction info", new OWLAnnotationCategory[]{GENERIC_INTERACTION }),
	BINARY_INTERACTION(-111,"BinaryInteraction", "binaryInteraction", "binary interaction", new OWLAnnotationCategory[]{GENERIC_INTERACTION }), // placeholder for data coming from table db partnership
	
	
	SUBCELLULAR_LOCATION(1039,"subcellular location", "subcellularLocation", "subcellular location", new OWLAnnotationCategory[]{GENERIC_LOCATION }),
	SUBCELLULAR_LOCATION_INFO(63868,"subcellular location info", "subcellularLocationInfo", "subcellular location info", new OWLAnnotationCategory[]{GENERIC_LOCATION }),
	GO_CELLULAR_COMPONENT(1063,"go cellular component", "goCellularComponent", "go cellular component", new OWLAnnotationCategory[]{GENERIC_LOCATION }),
	DEVELOPMENTAL_STAGE(1041,"developmental stage", "developmentalStage", "developmental stage", new OWLAnnotationCategory[]{GENERIC_LOCATION }),
	EPRESSION_INFO(1055,"expression info", "expressionInfo", "expression info", new OWLAnnotationCategory[]{GENERIC_LOCATION }),
	EPRESSION_PROFILE(1040,"tissue specificity", "expressionProfile", "expression profile (tissue specificity)", new OWLAnnotationCategory[]{GENERIC_LOCATION }),

	DISEASE(1046,"disease", "disease", "disease", new OWLAnnotationCategory[]{MEDICAL }),
	ALLERGEN(1048,"allergen", "allergen", "allergen", new OWLAnnotationCategory[]{MEDICAL }),
	PHARMACEUTICAL(1051,"pharmaceutical", "pharmaceutical", "pharmaceutical", new OWLAnnotationCategory[]{MEDICAL }),

	;

	private final Integer dbId; // if positive, identifies a real record of the table nextprot.cv_terms (category annotation_type)
	private final String dbAnnotationTypeName; // if dbId is positive, dbAnnotationTypeName is an exact match of the corresponding record in nextprot.cv_terms 
	private final String rdfName; // a string from which an rdf predicate and an rdfs:type name is derived 
	private final String rdfLabel; // a human readable label for the rdf:type
	private String description=null; // may be set later from reading values in the db 
	
	private final Set<OWLAnnotationCategory> parents;
	
	/** Category of control vocabulary that may be used to define the annotation */
	private OWLAnnotationCategory(
			final Integer dbId, 
			final String dbAnnotationTypeName, 
			final String rdfName, 
	        final String rdfLabel,
			final OWLAnnotationCategory[] parentCategories) {
		
		this.dbId = dbId;
		this.dbAnnotationTypeName=dbAnnotationTypeName;
		this.rdfName = rdfName;
		this.rdfLabel = rdfLabel;
		this.parents = new HashSet<OWLAnnotationCategory>();
		if (parentCategories!=null) {
			for (int i=0;i<parentCategories.length;i++) parents.add(parentCategories[i]);
		}
	}

	
	private static Map<String,OWLAnnotationCategory> mapEnum=new HashMap<String,OWLAnnotationCategory>();
	/**
	 * Allows to retrieve info about an annotation category given its annotation type name in the database
	 * @param typeName the annotation type name ( the cv_terms.cv_name related to the annotation.cv_annotation_type_id)
	 * @return
	 */
	
	/**
	 * Root categories are those declared with no parents (null)
	 * @return the set of root categories
	 */
	public static Set<OWLAnnotationCategory> getRoots() {
		Set<OWLAnnotationCategory> roots = new HashSet<OWLAnnotationCategory>();
		for (OWLAnnotationCategory cat : OWLAnnotationCategory.values()) {
			if (cat.getParents().size()==0) roots.add(cat);
		}
		return roots;
	}

	public static Set<OWLAnnotationCategory> getInstantiatedCategories() {
		Set<OWLAnnotationCategory> set = new HashSet<OWLAnnotationCategory>();
		for (OWLAnnotationCategory cat: OWLAnnotationCategory.values()) {
			if (cat.isInstantiated()) set.add(cat);
		}
		return set;
	}

	public static OWLAnnotationCategory getByDbAnnotationTypeName(String typeName) {
		// first look in the hash map (faster)
		if (mapEnum.containsKey(typeName)) return mapEnum.get(typeName);
		// then try to find the category by iterating through the values
		for (OWLAnnotationCategory category : OWLAnnotationCategory.values()) {
			if(category.getDbAnnotationTypeName().equals(typeName)) {
				mapEnum.put(category.getDbAnnotationTypeName(), category);
				return category;
			}
		}
		//finally if not found throw an error
		throw new RuntimeException("Could not find AnnotationCategory for type: "+typeName);
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
		return StringUtils.lowerFirstChar(this.rdfName);
	}
	
	public String getRdfTypeName() {
		return StringUtils.upperFirstChar(this.rdfName);
	}
		
	public String getRdfLabel() {
		return StringUtils.upperFirstChar(this.rdfLabel);
	}
	
	public Set<OWLAnnotationCategory> getParents() {
		return this.parents;
	}
	
	public Set<OWLAnnotationCategory> getChildren() {
		Set<OWLAnnotationCategory> children = new HashSet<OWLAnnotationCategory>();
		for (OWLAnnotationCategory cat : OWLAnnotationCategory.values()) {
			if (cat.getParents().contains(this)) children.add(cat);
		}
		return children;
	}
	
	public Set<OWLAnnotationCategory> getAllChildren() {
		Set<OWLAnnotationCategory> mine = getChildren();
		Set<OWLAnnotationCategory> all = new HashSet<OWLAnnotationCategory>(mine);
		for (OWLAnnotationCategory child : mine) all.addAll(child.getAllChildren());
		return all;
	}
	
	public Set<OWLAnnotationCategory> getAllParents() {
		Set<OWLAnnotationCategory> mine = getParents();
		Set<OWLAnnotationCategory> all = new HashSet<OWLAnnotationCategory>(mine);
		for (OWLAnnotationCategory parent : mine) all.addAll(parent.getAllParents());
		return all;
	}
	
	public String toString() {
		return this.getDbId() + " : " + this.getDbAnnotationTypeName();
	}

}
