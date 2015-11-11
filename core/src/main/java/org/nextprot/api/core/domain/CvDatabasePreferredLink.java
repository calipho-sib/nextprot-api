package org.nextprot.api.core.domain;



/**
 * Title:        CvDatabaseLinks.java<br>
 * Description:  Preferred links for som databases<br>
 * Copyright:    Copyright (c) 2010<br>
 * Company:      GeneBio<br>
 *
 * @author Catherine<br>
 */

public enum CvDatabasePreferredLink {
	
	BRENDA_BTO("Brenda","http://purl.obolibrary.org/obo/%s"),
//    BRENDA_BTO("Brenda","http://www.brenda-enzymes.org/ontology/inc/tree/result_option.php4?tissue=1&id_go=%s"),
	EMBL("EMBL", "http://www.ebi.ac.uk/cgi-bin/dbfetch?db=emblcds&id=%s"),
	ENSEMBL_GENE("Ensembl","http://www.ensembl.org/Homo_sapiens/Gene/Summary?db=core;g=%s"),
	ENSEMBL_TRANSCRIPT("Ensembl","http://www.ensembl.org/Homo_sapiens/Transcript/Summary?db=core;t=%s"),
	ENSEMBL_PROTEIN("Ensembl", "http://www.ensembl.org/Homo_sapiens/Transcript/ProteinSummary?db=core;p=%s"),
	GERMONLINE("GermOnline","http://www.germonline.org/Homo_sapiens/geneview?gene=%s"),
	INTACT_BINARY("IntAct","http://www.ebi.ac.uk/intact/search/do/search?binary=%s"),
	PROSITE("Prosite", "http://prosite.expasy.org/cgi-bin/prosite/prosite-search-ac?%s"),
	PDB("PDB", "http://www.pdb.org/pdb/explore/explore.do?pdbId=%s"),
	PROTEOPEDIA("Proteopedia","http://www.proteopedia.org/wiki/index.php/%s"),
	HPA_GENE("HPA", "http://www.proteinatlas.org/%s"),
    HPA_SUBCELL("HPA", "http://www.proteinatlas.org/%s"),
    HPA_ANTIBODY("HPA", "http://www.proteinatlas.org/search/%s"),
    COSMIC_SAMPLE("Cosmic", "http://cancer.sanger.ac.uk/cosmic/sample/overview?id=%s"),
    COSMIC_MUTATION("Cosmic", "http://cancer.sanger.ac.uk/cosmic/mutation/overview?id=%s"),
    COSMIC_GENE("Cosmic", "http://cancer.sanger.ac.uk/cosmic/gene/overview?ln=%s"),
    CLINVAR_MUTATION("Clinvar", "http://www.ncbi.nlm.nih.gov/clinvar/%s"),
    CLINVAR_GENE("Clinvar", "http://www.ncbi.nlm.nih.gov/clinvar/?term=%s"),
	GENEVESTIGATOR("Genevestigator", "http://genevisible.com/tissues/HS/UniProt/%s");

	private final String dbName;
	private final String link;


	private CvDatabasePreferredLink(final String dbName ,final String link) {
		this.dbName = dbName;
		this.link = link;
	}


	// ----------------- Instance methods ----------------- //

	public String getDbName() {
    	return dbName;
    }


	public String getLink() {
    	return link;
    }

	// ----------------- Class methods ----------------- //
	 
	public static Boolean dbHasPreferredLink(String dbName) {
		for (CvDatabasePreferredLink l : CvDatabasePreferredLink.values()) {
			if (l.getDbName().toLowerCase().equals(dbName.toLowerCase())) 
				return true;
		}
		return false;
	}
	
}
