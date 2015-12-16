package org.nextprot.api.core.domain;


public enum CvDatabasePreferredLink {
	
	BRENDA_BTO(XRefDatabase.BRENDA,"http://purl.obolibrary.org/obo/%s"),
//    BRENDA_BTO(DatabaseName.BRENDA,"http://www.brenda-enzymes.org/ontology/inc/tree/result_option.php4?tissue=1&id_go=%s"),
	EMBL(XRefDatabase.EMBL, "http://www.ebi.ac.uk/cgi-bin/dbfetch?db=emblcds&id=%s"),
	ENSEMBL_GENE(XRefDatabase.ENSEMBL,"http://www.ensembl.org/Homo_sapiens/Gene/Summary?db=core;g=%s"),
	ENSEMBL_TRANSCRIPT(XRefDatabase.ENSEMBL,"http://www.ensembl.org/Homo_sapiens/Transcript/Summary?db=core;t=%s"),
	ENSEMBL_PROTEIN(XRefDatabase.ENSEMBL, "http://www.ensembl.org/Homo_sapiens/Transcript/ProteinSummary?db=core;p=%s"),
	GERMONLINE(XRefDatabase.GERMONLINE,"http://www.germonline.org/Homo_sapiens/geneview?gene=%s"),
	INTACT_BINARY(XRefDatabase.INTACT,"http://www.ebi.ac.uk/intact/search/do/search?binary=%s"),
	PROSITE(XRefDatabase.PROSITE, "http://prosite.expasy.org/cgi-bin/prosite/prosite-search-ac?%s"),
	PDB(XRefDatabase.PDB, "http://www.pdb.org/pdb/explore/explore.do?pdbId=%s"),
	PROTEOPEDIA(XRefDatabase.PROTEOPEDIA,"http://www.proteopedia.org/wiki/index.php/%s"),
	HPA_GENE(XRefDatabase.HPA, "http://www.proteinatlas.org/%s"),
    HPA_SUBCELL(XRefDatabase.HPA, "http://www.proteinatlas.org/%s"),
    HPA_ANTIBODY(XRefDatabase.HPA, "http://www.proteinatlas.org/search/%s"),
    COSMIC_SAMPLE(XRefDatabase.COSMIC, "http://cancer.sanger.ac.uk/cosmic/sample/overview?id=%s"),
    COSMIC_MUTATION(XRefDatabase.COSMIC, "http://cancer.sanger.ac.uk/cosmic/mutation/overview?id=%s"),
    COSMIC_GENE(XRefDatabase.COSMIC, "http://cancer.sanger.ac.uk/cosmic/gene/overview?ln=%s"),
    CLINVAR_MUTATION(XRefDatabase.CLINVAR, "http://www.ncbi.nlm.nih.gov/clinvar/%s"),
    CLINVAR_GENE(XRefDatabase.CLINVAR, "http://www.ncbi.nlm.nih.gov/clinvar/?term=%s"),
	GENEVESTIGATOR(XRefDatabase.GENEVESTIGATOR, "http://genevisible.com/tissues/HS/UniProt/%s"),
	REFSEQ_NUCLEOTIDE(XRefDatabase.REF_SEQ, "http://www.ncbi.nlm.nih.gov/nuccore/%s"),
	PEPTIDE_ATLAS_PROTEIN(XRefDatabase.PEPTIDE_ATLAS, "https://db.systemsbiology.net/sbeams/cgi/PeptideAtlas/GetProtein?protein_name=%s;organism_name=Human;action=GO"),
	PEPTIDE_ATLAS_PEPTIDE(XRefDatabase.PEPTIDE_ATLAS, "https://db.systemsbiology.net/sbeams/cgi/PeptideAtlas/GetPeptide?searchWithinThis=Peptide+Name&searchForThis=%s;organism_name=Human"),
	OBO(XRefDatabase.OBO, "http://purl.obolibrary.org/obo/%s"),
	JCRB(XRefDatabase.JCRB, "http://cellbank.nibio.go.jp/~cellbank/en/search_res_list.cgi?KEYWOD=%s"),
	IFO(XRefDatabase.IFO, "http://cellbank.nibio.go.jp/~cellbank/cgi-bin/search_res_det.cgi?RNO=%s")
	;

	private final XRefDatabase dbName;
	private final String link;

    CvDatabasePreferredLink(final XRefDatabase dbName , final String link) {
		this.dbName = dbName;
		this.link = link;
	}

	// ----------------- Instance methods ----------------- //

    public XRefDatabase getDb() {
        return dbName;
    }

	public String getDbName() {
    	return dbName.getName();
    }


	public String getLink() {
    	return link;
    }

	// ----------------- Class methods ----------------- //
	 
	public static boolean dbHasPreferredLink(String dbName) {
		for (CvDatabasePreferredLink l : CvDatabasePreferredLink.values()) {
			if (l.getDbName().toLowerCase().equals(dbName.toLowerCase())) 
				return true;
		}
		return false;
	}

    public static boolean isDbHasPreferredLink(XRefDatabase dbName) {

        for (CvDatabasePreferredLink l : CvDatabasePreferredLink.values()) {
            if (l.getDb()== dbName) return true;
        }

        return false;
    }
	
}
