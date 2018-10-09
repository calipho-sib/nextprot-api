package org.nextprot.api.core.domain.publication;

import org.nextprot.api.core.domain.DbXref;
import org.nextprot.api.core.service.dbxref.resolver.DbXrefURLResolverDelegate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PublicationDirectLink implements Comparable<PublicationDirectLink>, Serializable {

    private static final long serialVersionUID = 1L;

    private long publicationId;
	private String datasource;  // PIR or UniProt
	private String database;    // PDB,IntAct, GeneRif,...
	private String accession;   // null when database = UniProtKB
	private String link;        // null when database = UniProtKB
	private String label;
	private PublicationProperty publicationProperty;

    PublicationDirectLink(long publicationId, String propertyName, String propertyValue) {

        this(publicationId, PublicationProperty.valueOf(propertyName.toUpperCase()), propertyValue);
    }

    public PublicationDirectLink(long publicationId, PublicationProperty propertyName, String propertyValue) {

		this.publicationId = publicationId;
		this.publicationProperty = propertyName;

		if (propertyName == PublicationProperty.SCOPE) {

			this.datasource = "Uniprot";
			this.database = "UniProtKB";
			this.label = propertyValue;
		}
		else if (propertyName == PublicationProperty.COMMENT) {

			this.datasource = "PIR";

			int labelIndex = 0;

            // parse things like "[database:accession] label";
			if (propertyValue.contains("]")) {

			    List<String> databaseAndAccession = new ArrayList<>();

                int lastParsedIndex = parseDatabaseAndAccession(propertyValue, databaseAndAccession);

                this.database = databaseAndAccession.get(0);
                this.accession = databaseAndAccession.get(1);
                this.link = getLinkFor(this.database, this.accession);

                labelIndex = lastParsedIndex + 1;
            }

			this.label = (labelIndex < propertyValue.length()) ? propertyValue.substring(labelIndex).trim() : "";
		}
	}

	private int parseDatabaseAndAccession(String propertyValue, List<String> stringCollector) {

        int closedBracketIndex = propertyValue.indexOf("]");

        String head = propertyValue.substring(0, closedBracketIndex);
        int colonDelimitorIndex = head.indexOf(":");

        if (colonDelimitorIndex != -1) {
            stringCollector.add(head.substring(1, colonDelimitorIndex));
            stringCollector.add(head.substring(colonDelimitorIndex + 1));
        }
        else {
            throw new IllegalArgumentException(propertyValue+": missing colon delimitor in comment value of publication id "+publicationId);
        }

        return closedBracketIndex;
    }

	public long getPublicationId() {
		return publicationId;
	}

	public void setPublicationId(long publicationId) {
		this.publicationId = publicationId;
	}

	public String getDatasource() {
		return datasource;
	}

	public void setDatasource(String datasource) {
		this.datasource = datasource;
	}

	public String getDatabase() {
		return database;
	}

	public void setDatabase(String database) {
		this.database = database;
	}

	public String getAccession() {
		return accession;
	}

	public void setAccession(String accession) {
		this.accession = accession;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

    public PublicationProperty getPublicationProperty() {
        return publicationProperty;
    }

    @Override
	public int compareTo(PublicationDirectLink o) {
		int res;
		res = -this.datasource.compareTo(o.datasource);  // UniProt first, then PIR stuff
		if (res != 0) return res;
		res = this.database.compareToIgnoreCase(o.database);  // db alpha case INsensitive
		if (res != 0) return res;
		return this.label.compareTo(o.label); // label alpha
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("pubid:").append(publicationId).append(" ");
		sb.append("src:").append(datasource).append(" ");
		sb.append("db:").append(database).append(" ");
		sb.append("ac:").append(accession).append(" ");
		sb.append("href:").append(link).append(" ");
		sb.append("label:").append(label).append(" ");
		return sb.toString();
	}

// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

	/**
	 * The Map is a modified copy of what we have in the NP1 db about databases referenced by PIR.
	 * It is used to give the default url template that is used to resolve XRef link.
	 */

	private static Map<String, String> db2link;
	private static DbXrefURLResolverDelegate resolver;

	static {
		resolver = new DbXrefURLResolverDelegate();
		db2link = new HashMap<>();
		db2link.put("BioCyc", "http://biocyc.org/getid?id=%s");
		db2link.put("BioMuta", "https://hive.biochemistry.gwu.edu/tools/biomuta/biomuta.php?gene=%s"); 
		// the one below is modified because the site is not maintained any more, this is a fake template which links to the only home page
		db2link.put("GAD", "https://geneticassociationdb.nih.gov/?id=%s");
		db2link.put("GeneRif", "http://www.ncbi.nlm.nih.gov/sites/entrez?db=gene&report=GeneRif&term=%s");
		// the one below causes an error
		//db2link.put("IntAct","https://www.ebi.ac.uk/intact/interactors/id:%s*");
		// is replaced with this one which is ok
		db2link.put("IntAct", "https://www.ebi.ac.uk/intact/query/%s*");
		db2link.put("iPTMnet", "http://research.bioinformatics.udel.edu/iptmnet/entry/%s");
		db2link.put("MEROPS", "https://www.ebi.ac.uk/merops/cgi-bin/pepsum?mid=%s");
		db2link.put("MINT", "http://mint.bio.uniroma2.it/mint/search/search.do?queryType=protein&interactorAc=%s");
		db2link.put("PDB", "http://www.ebi.ac.uk/pdbe-srv/view/entry/%s");
		// modified to use https
		db2link.put("PhosphoSitePlus", "https://www.phosphosite.org/uniprotAccAction?id=%s");
		db2link.put("PRO", "http://research.bioinformatics.udel.edu/pro/entry/%s/");
		// for the one below I removed the 2nd parameter which seems unnecessary: &FLG=%u
		db2link.put("Reactome", "http://www.reactome.org/PathwayBrowser/#%s");
		// The one below is not in NP1 db, Alain found it
		db2link.put("PubTator", "https://www.ncbi.nlm.nih.gov/CBBresearch/Lu/Demo/PubTator/index.cgi?searchtype=PubMed_Search&query=%s");
	}

	private static String getLinkFor(String dbName, String accession) {
		//System.out.println("getLinkFor: " + dbName + " | " + accession);
		DbXref xref = new DbXref();
		xref.setAccession(accession);
		xref.setDatabaseName(dbName);
		xref.setLinkUrl(db2link.get(dbName));
		return resolver.resolve(xref);
	}
}
