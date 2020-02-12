package org.nextprot.api.core.domain;


import org.nextprot.api.core.domain.release.ReleaseDataSources;

public enum CvDatabasePreferredLink {

	BRENDA("http://www.brenda-enzymes.org/enzyme.php?ecno=%s&UniProtAcc=%u"),
	BRENDA_BTO("http://purl.obolibrary.org/obo/%s"),
	CELLOSAURUS("https://web.expasy.org/cellosaurus/%s"),
	CHITARS("http://chitars.bioinfo.cnio.es/cgi-bin/search.pl?searchtype=gene_name&searchstr=%s&human=1"),
	CLINVAR_MUTATION( "http://www.ncbi.nlm.nih.gov/clinvar/%s"),
	CLINVAR_GENE( "http://www.ncbi.nlm.nih.gov/clinvar/?term=%s"),
	COSMIC_SAMPLE( "http://cancer.sanger.ac.uk/cosmic/sample/overview?id=%s"),
	COSMIC_MUTATION( "http://cancer.sanger.ac.uk/cosmic/mutation/overview?id=%s"),
	COSMIC_GENE( "http://cancer.sanger.ac.uk/cosmic/gene/overview?ln=%s"),
	ECO("http://purl.obolibrary.org/obo/%s"),
	EMBL("http://www.ebi.ac.uk/cgi-bin/dbfetch?db=emblcds&id=%s"),
	EMBL_GENE("http://www.ebi.ac.uk/ena/data/view/%s"),
	EMBL_PROTEIN("http://www.ebi.ac.uk/ena/data/view/%s"),
	ENSEMBL_GENE("http://www.ensembl.org/Homo_sapiens/Gene/Summary?db=core;g=%s"),
	ENSEMBL_TRANSCRIPT("http://www.ensembl.org/Homo_sapiens/Transcript/Summary?db=core;t=%s"),
	ENSEMBL_PROTEIN("http://www.ensembl.org/Homo_sapiens/Transcript/ProteinSummary?db=core;p=%s"),
	EXPRESSION_ATLAS("http://www.ebi.ac.uk/gxa/search?geneQuery=%09%s"),
	GENEVESTIGATOR("http://genevisible.com/tissues/HS/UniProt/%s"),
	GERMONLINE("http://www.germonline.org/Homo_sapiens/geneview?gene=%s"),
	GLY_CONNECT("https://glyconnect.expasy.org/browser/proteins/%s"),
	GNOMAD("https://gnomad.broadinstitute.org/variant/%s"),
	HPA_GENE(ReleaseDataSources.HPA.getUrl()+"%s"),
	HPA_SUBCELL(ReleaseDataSources.HPA.getUrl()+"%s"),
	HPA_ANTIBODY(ReleaseDataSources.HPA.getUrl()+ "search/%s"),
	IFO("http://cellbank.nibio.go.jp/~cellbank/cgi-bin/search_res_det.cgi?RNO=%s"),
	INTACT_BINARY("http://www.ebi.ac.uk/intact/search/do/search?binary=%s"),
	JCRB("http://cellbank.nibio.go.jp/~cellbank/en/search_res_list.cgi?KEYWOD=%s"),
	LOC("https://www.ncbi.nlm.nih.gov/gene?term=%s[All Fields]&cmd=DetailsSearch"),
	MESH("https://meshb.nlm.nih.gov/record/ui?ui=%s"),
	OBO("http://purl.obolibrary.org/obo/%s"),
	PDB("https://www.rcsb.org/pdb/explore/explore.do?pdbId=%s"),
	PEPTIDE_ATLAS_PEPTIDE("https://db.systemsbiology.net/sbeams/cgi/PeptideAtlas/GetPeptide?searchWithinThis=Peptide+Name&searchForThis=%s;organism_name=Human"),
	PEPTIDE_ATLAS_PROTEIN("https://db.systemsbiology.net/sbeams/cgi/PeptideAtlas/GetProtein?protein_name=%s;organism_name=Human;action=GO"),
	PROSITE( "http://prosite.expasy.org/cgi-bin/prosite/prosite-search-ac?%s"),
	PROTEOPEDIA("http://www.proteopedia.org/wiki/index.php/%s"),
	PSIMOD("https://www.ebi.ac.uk/ols/ontologies/mod/terms?iri=http://purl.obolibrary.org/obo/MOD_%s"),
	REFSEQ_NUCLEOTIDE("http://www.ncbi.nlm.nih.gov/nuccore/%s"),
	RULEBASE("http://www.uniprot.org/unirule/%s"),
	SIGNOR("http://signor.uniroma2.it/relation_result.php?id=%u&organism=human"),
	UCSC("https://genome.ucsc.edu/cgi-bin/hgLinkIn?resource=uniprot&id=%u"),
	UNIPROT_DISEASES("http://www.uniprot.org/diseases/%s"),
	UNIPROT_DOMAIN("https://www.uniprot.org/uniprot/?query=annotation%3A%28type%3Apositional+%s%29"),
	UNIPROT_KEYWORDS("http://www.uniprot.org/keywords/%s"),
	UNIPROT_LOCATIONS("http://www.uniprot.org/locations/%s"),
	VARIO("http://purl.obolibrary.org/obo/%s"),
	;

	private final String link;

    CvDatabasePreferredLink(final String link) {
		this.link = link;
	}

	public String getLink() {
    	return link;
    }
}
