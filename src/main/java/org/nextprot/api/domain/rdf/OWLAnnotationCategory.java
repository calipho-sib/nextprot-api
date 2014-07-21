package org.nextprot.api.domain.rdf;

import java.util.ArrayList;
import java.util.List;

import org.nextprot.utils.StringUtils;





/**
 * Description: <br> * 
 * @author Olivier Evalet<br>
 */

public enum OWLAnnotationCategory  {

	ANNOTATION(1, "annotation", "annotation", null, "Annotation"),
	ONTOLOGY_ANNOTATION(4, "ontology annotation", "function", ANNOTATION,"Ontology"),
	  GO_FUNCTION(4001,"go molecular function", "function", ONTOLOGY_ANNOTATION, "GO molecular function"),
	  GO_PROCESS(4002, "go biological process", "function", ONTOLOGY_ANNOTATION, "GO biological process"),
	  GO_LOCATION(4003, "go cellular component", "localisation", ONTOLOGY_ANNOTATION,"GO cellular component"),
	  UNIPROT_KW(4004, "uniprot keyword", "function", ONTOLOGY_ANNOTATION, "Uniprot KW"), /*CONFIRM predicat*/
	  EC(4005, "enzyme classification", "function", ONTOLOGY_ANNOTATION, "Enzyme"), /*CONFIRM predicat*/

	GENERAL_ANNOTATION(3, "general annotation", "general",ANNOTATION,"Annotation"),
	  ALLERGEN(3001, "allergen", "function", GENERAL_ANNOTATION,"Allergen"),
	  BIOTECHNOLOGY(3002, "biotechnology", "medical", GENERAL_ANNOTATION,"Biotechnology"),
	  CATALIYTIC_ACTIVITY(3003, "catalytic activity", "function", GENERAL_ANNOTATION,"Catalytic activity"),
	  CAUTION(3004, "caution", "function", GENERAL_ANNOTATION,"Caution"),
	  SEQUENCE_CAUTION(3005, "sequence caution", "sequenceCaution", CAUTION,"Sequence caution"),
	  COFACTOR(3006, "cofactor", "function",GENERAL_ANNOTATION,"Cofactor"),
	  DEVELOPMENTAL_STAGE(3007, "developmental stage", "expression",GENERAL_ANNOTATION,"Developmental stage"),/*CONFIRM predicat*/
	  DISEASE(3008, "disease", "medical",GENERAL_ANNOTATION,"Disease"),
	  DISRUPTIVE_PHENOTYPE(3009, "disruption phenotype", "mutation", GENERAL_ANNOTATION,"Disruption phenotype"),
	  DOMAIN_INFO(3010, "domain information", "domainInfo",GENERAL_ANNOTATION,"DomainInfo"),
	  ENZYME_REGULATION(3011, "enzyme regulation", "function",GENERAL_ANNOTATION,"Enzyme regulation"),
	  EXPRESSION_INFO(3012, "expression info","expression", GENERAL_ANNOTATION,"Expression"),
	  FUNCTION(3013, "function", "function", GENERAL_ANNOTATION,"Function"),
	  INDUCTION(3014, "induction", "expression",GENERAL_ANNOTATION,"Induction"),
	  MISCELLANEOUS(3015, "miscellaneous", "annotation", GENERAL_ANNOTATION,"Miscellaneous"),
	  PATHWAY(3016, "pathway", "function", GENERAL_ANNOTATION, "Pathway"),
	  PHARMACEUTICAL(3017, "pharmaceutical", "medical", GENERAL_ANNOTATION,"Pharmaceutical"),
	  POLYMORPHISM(3018, "polymorphism", "medical", GENERAL_ANNOTATION,"Polymorphism"),
	  PTM(3019, "PTM", "ptm", GENERAL_ANNOTATION,"PTM"),  /* never used */
	  SIMILARITY(3020, "similarity", "families", GENERAL_ANNOTATION,"Similarity"),
	    DOMAIN_NAME(3021, "domain name", "families", SIMILARITY, "Domain"),
	    FAMILY_NAME(3022, "family name", "families", SIMILARITY, "Family"),
	  SUBUNIT(3023, "subunit","interaction", GENERAL_ANNOTATION,"Subunit"),
	  	SELF_ASSOCIATION(30231, "self-association","interaction", SUBUNIT, "Self-association"), /*CONFIRM predicat*/
	  	BINARY(30232, "binary interaction","interaction", SUBUNIT, "Binary interaction"), /*CONFIRM predicat*/
	  	COMPLEX(30233, "complex", "interaction",SUBUNIT, "Complex"), /*CONFIRM predicat*/
	  SUBCELLULAR_LOCATION(3024, "subcellular location","localisation",GENERAL_ANNOTATION, "Subcellular location"),
	  SUBCELLULAR_LOCATION_INFO(3027, "subcellular location info","localisation",GENERAL_ANNOTATION, "Subcellular location info"),
	  TISSUE(3025, "tissue specificity", "expression", GENERAL_ANNOTATION,"Tissue expression"),
	  TOXIC_DOSE(3026, "toxic dose", "function", GENERAL_ANNOTATION,"Toxic dose"),/*CONFIRM predicat*/

	FEATURE(2, "feature", "feature", ANNOTATION,"Sequence annotation"),

	MOLECULE_PROCESSING(10, "molecule processing", "processing", FEATURE,"Processing"),
	  MATURE_PROTEIN(1001, "mature protein", "processing", MOLECULE_PROCESSING,"Mature protein"),
	  MATURATION_PEPTIDE(1002, "maturation peptide", "processing", MOLECULE_PROCESSING,"Propeptide"),
	  INITIATOR_METHIONINE(1003, "initiator methionine", "processing", MOLECULE_PROCESSING,"Initiator Met"),
	  SIGNAL_PEPTIDE(1004, "signal peptide", "processing", MOLECULE_PROCESSING,"Signal peptide"),
	  TRANSIT_PEPTIDE(1005, "transit peptide", "processing", MOLECULE_PROCESSING,"Transit peptide"),

	REGION_OF_INTEREST(11, "region of interest", "region",FEATURE,"Regions"),
	  CALCIUM_BINDING(1101, "calcium-binding region", "region",REGION_OF_INTEREST,"Calcium binding"),
	  COIL_COIL(1102, "coiled-coil region", "region",REGION_OF_INTEREST,"Coiled-coil"),
	  COMPOSITION_BIASED(1103, "compositionally biased region", "regions",REGION_OF_INTEREST,"Composition bias"),
	  DNA_BINDING(1104, "DNA-binding region", "region",REGION_OF_INTEREST,"DNA binding"),
	  DOMAIN(1105, "domain", "region",REGION_OF_INTEREST,"Domain"),
	  INTERACTING_REGION(1113, "interacting region", "region",REGION_OF_INTEREST,"Miscellaneous region"),
	  MOTIF(1106, "short sequence motif", "region",REGION_OF_INTEREST,"Sequence motif"),
	  NUC_PHOSPHATE_BINDING(1107,"nucleotide phosphate-binding region","region",REGION_OF_INTEREST,"Nucleotide binding"),
	  REPEAT(1108, "repeat", "region",REGION_OF_INTEREST,"Repeat"),
	  TOPOLOGY(3025, "topology", "topology",REGION_OF_INTEREST,"Topology"),
	    TOPO_DOMAIN(1109, "topological domain", "topology",TOPOLOGY,"Topological domain"),
	    TRANSMEMBRANE(1110, "transmembrane region", "topology",TOPOLOGY,"Membrane"),
	  ZINC_FINGER(1111, "zinc finger region", "region",REGION_OF_INTEREST,"Zinc finger"),
	  CODING_SEQUENCE(1112, "coding sequence", "region",REGION_OF_INTEREST,"Coding sequence"),

	SITE(12, "site", "site",FEATURE,"Sites"),
	  ACTIVE_SITE(1201, "active site", "site",SITE,"Active site"),
	  BINDING_SITE(1202, "binding site", "site",SITE,"Binding site"),
	  CLEAVAGE_SITE(1204,"cleavage site", "site",SITE,"Cleavage site"),
	  METAL_BINDING_SITE(1203, "metal ion-binding site", "site",SITE,"Metal binding"),

	AMINO_ACID_MODIF(13, "amino acid modification", "modifiedResidue", FEATURE,"Modified residues"),
	  CROSS_LINK(1301, "cross-link", "modifiedResidue", AMINO_ACID_MODIF,"Cross-link"),
	  DISULFIDE_BOND(1302, "disulfide bond", "modifiedResidue", AMINO_ACID_MODIF,"Disulfide bond"),
	  GLYCO_SITE(1303, "glycosylation site", "modifiedResidue", AMINO_ACID_MODIF,"Glycosylation"),
	  LIPID_BINDING(1304, "lipid moiety-binding region", "modifiedResidue", AMINO_ACID_MODIF,"Lipidation"),
	  NON_STD_AMINO_ACID(1305, "non-standard amino acid", "modifiedResidue", AMINO_ACID_MODIF,"Non-standard AA"),

	VARIANT(15, "variant", "variant", FEATURE,"Variants"),
	  ALIGNMENT_CONFLICT(1501, "alignment conflict", "variant", VARIANT,"Alignment conflict"),
	  MUTAGENESIS(1502, "mutagenesis site", "mutation", VARIANT,"Mutagenesis"),
	  SEQ_CONFLICT(1503, "sequence conflict", "variant", VARIANT,"Conflict"),
	  SEQ_VARIANT(1504, "sequence variant", "variant", VARIANT,"Variant"),

	MISDEFINED_REGION(16, "misdefined region", "misdefinedRegion",FEATURE,"Misdefined region"),/*CONFIRM predicat*/
	  NON_CONSECUTIVE_RESID(1601, "non-consecutive residues", "misdefinedRegion",MISDEFINED_REGION,"Non-consecutive residue"),/*CONFIRM predicat*/
	  NON_TERM_RESID(1602, "non-terminal residue", "misdefinedRegion",MISDEFINED_REGION,"Non-terminal residue"),/*CONFIRM predicat*/
	  UNSURE_RESID(1603, "unsure residue", "misdefinedRegion",MISDEFINED_REGION,"Unsure residue"),/*CONFIRM predicat*/

	SECONDARY_STRUCTURE(14, "secondary structure", "structure", FEATURE,"Secondary structure"),
	  BETA_STRAND(1401, "beta strand", "structure", SECONDARY_STRUCTURE,"Beta strand"),
	  HELIX(1402, "helix", "structure", SECONDARY_STRUCTURE,"Helix"),
	  TURN(1403, "turn", "structure", SECONDARY_STRUCTURE,"Turn"),
	  
	  THREE_D_STRUCTURE(1113, "3D structure", "region", REGION_OF_INTEREST,"3D structure"), /*CONFIRM predicat with Anne, Oliv and Amos...*/

	;

	private final Integer id;
	private final String rdfPredicat;
	private final String rdfType;
	private final String rdfLabel;
	private final OWLAnnotationCategory rdfSubClassOf;
	
	/** Category of control vocabulary that may be used to define the annotation */
	private OWLAnnotationCategory(
			final Integer id, 
			final String rdfType, 
			final String rdfPredicat, 
			final OWLAnnotationCategory parent,
	        final String rdfLabel) {
		this.id = id;
		this.rdfType = rdfType;
		this.rdfPredicat = rdfPredicat;
		this.rdfSubClassOf = parent;
		this.rdfLabel = rdfLabel;
	}
	


	public static OWLAnnotationCategory getByType(String type){
		for (OWLAnnotationCategory category : OWLAnnotationCategory.values()) {
			if(category.getType().equals(type))
				return category;
		}
		
		throw new RuntimeException("Could not find AnnotationCategory for type: "+type);
	}

	/**
	 * list the domain of this predicat, it consist of all 
	 * AnnotationCategory that share the same predicat
	 * @return
	 */
	public List<String> getDomain(){
		List<String> domain=new ArrayList<String>();
		OWLAnnotationCategory ac=this.getSubClassOf();
		if(ac ==null){
			domain.add(this.getRdfType());
			return domain;
		}
		
		for (OWLAnnotationCategory category : OWLAnnotationCategory.values()) {
			if(category.getPredicat().equals(rdfPredicat))
				domain.add(category.getRdfType());
		}
		return domain;		
	}
	

	/**
	 * list disjJoin class for this AnnotationCategory
	 * @return
	 */
	public List<String> getDisjoinWith(){
		List<String> disjoin=new ArrayList<String>();
		for (OWLAnnotationCategory category : OWLAnnotationCategory.values()) {
			if(!category.getType().equals(rdfType))
				disjoin.add(category.getRdfType());
		}
		return disjoin;		
	}
	
	private OWLAnnotationCategory getSubClassOf() {		
		return rdfSubClassOf;
	}
	
	public String getPredicat() {
		return this.rdfPredicat;
	}
	
	public String getRdfType() {
		return StringUtils.toCamelCase(rdfType,false);
	}
	
	public Integer getId() {
		return id;
	}

	public String getType() {
		return rdfType;
	}
	
	public String getLabel() {
		return this.rdfLabel;
	}


}
