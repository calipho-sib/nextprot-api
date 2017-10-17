package org.nextprot.api.core.utils;

public class PublicationDirectLink {

		private long publicationId;
		private String datasource; // PIR or UniProt
		private String database;   // PDB,IntAct, GeneRif,...
		private String accession;
		private String link;		// null when database = UniProtKB
		private String label;
		
		public PublicationDirectLink(String propertyValue) {
			
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
	
		
		
}
