package org.nextprot.api.core.domain;


public enum CvDatabasePreferredLink {
	
	BRENDA_BTO("http://purl.obolibrary.org/obo/%s"),
//    BRENDA_BTO(DatabaseName.BRENDA,"http://www.brenda-enzymes.org/ontology/inc/tree/result_option.php4?tissue=1&id_go=%s"),
	EMBL("http://www.ebi.ac.uk/cgi-bin/dbfetch?db=emblcds&id=%s"),
	EMBL_GENE( "http://www.ebi.ac.uk/ena/data/view/%s"),
	EMBL_PROTEIN( "http://www.ebi.ac.uk/ena/data/view/%s"),
	ENSEMBL_GENE("http://www.ensembl.org/Homo_sapiens/Gene/Summary?db=core;g=%s"),
	ENSEMBL_TRANSCRIPT("http://www.ensembl.org/Homo_sapiens/Transcript/Summary?db=core;t=%s"),
	ENSEMBL_PROTEIN( "http://www.ensembl.org/Homo_sapiens/Transcript/ProteinSummary?db=core;p=%s"),
	GERMONLINE("http://www.germonline.org/Homo_sapiens/geneview?gene=%s"),
	INTACT_BINARY("http://www.ebi.ac.uk/intact/search/do/search?binary=%s"),
	PROSITE( "http://prosite.expasy.org/cgi-bin/prosite/prosite-search-ac?%s"),
	PDB( "http://www.pdb.org/pdb/explore/explore.do?pdbId=%s"),
	PROTEOPEDIA("http://www.proteopedia.org/wiki/index.php/%s"),
	HPA_GENE( "http://www.proteinatlas.org/%s"),
    HPA_SUBCELL( "http://www.proteinatlas.org/%s"),
    HPA_ANTIBODY( "http://www.proteinatlas.org/search/%s"),
    COSMIC_SAMPLE( "http://cancer.sanger.ac.uk/cosmic/sample/overview?id=%s"),
    COSMIC_MUTATION( "http://cancer.sanger.ac.uk/cosmic/mutation/overview?id=%s"),
    COSMIC_GENE( "http://cancer.sanger.ac.uk/cosmic/gene/overview?ln=%s"),
    CLINVAR_MUTATION( "http://www.ncbi.nlm.nih.gov/clinvar/%s"),
    CLINVAR_GENE( "http://www.ncbi.nlm.nih.gov/clinvar/?term=%s"),
	GENEVESTIGATOR( "http://genevisible.com/tissues/HS/UniProt/%s"),
	REFSEQ_NUCLEOTIDE( "http://www.ncbi.nlm.nih.gov/nuccore/%s"),
	PEPTIDE_ATLAS_PROTEIN( "https://db.systemsbiology.net/sbeams/cgi/PeptideAtlas/GetProtein?protein_name=%s;organism_name=Human;action=GO"),
	PEPTIDE_ATLAS_PEPTIDE( "https://db.systemsbiology.net/sbeams/cgi/PeptideAtlas/GetPeptide?searchWithinThis=Peptide+Name&searchForThis=%s;organism_name=Human"),
	OBO( "http://purl.obolibrary.org/obo/%s"),
	JCRB( "http://cellbank.nibio.go.jp/~cellbank/en/search_res_list.cgi?KEYWOD=%s"),
	IFO( "http://cellbank.nibio.go.jp/~cellbank/cgi-bin/search_res_det.cgi?RNO=%s")
	;

	private final String link;

    CvDatabasePreferredLink(final String link) {
		this.link = link;
	}

	public String getLink() {
    	return link;
    }
}
