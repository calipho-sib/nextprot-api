package org.nextprot.api.domain;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.jsondoc.core.annotation.ApiObject;
import org.jsondoc.core.annotation.ApiObjectField;

@ApiObject(name = "xref", description = "A cross reference")
public class DbXref implements Serializable {

	private static final long serialVersionUID = 2316953378438971441L;

	@ApiObjectField(description = "The neXtProt identifier")
	private Long dbXrefId;

	@ApiObjectField(description = "The accession code of the cross reference")
	private String accession;

	@ApiObjectField(description = "The database name")
	private String databaseName;

	@ApiObjectField(description = "The database category")
	private String databaseCategory;

	@ApiObjectField(description = "The url")
	private String url;

	private String linkUrl;

	private String resolvedUrl;

	@ApiObjectField(description = "A list of properties. A property contains an accession, a property name and a value.")
	private List<DbXrefProperty> properties;

	public Long getDbXrefId() {
		return dbXrefId;
	}

	public void setDbXrefId(Long dbXrefId) {
		this.dbXrefId = dbXrefId;
	}

	public String getAccession() {
		return accession;
	}

	public void setAccession(String accession) {
		this.accession = accession;
	}

	public String getDatabaseName() {
		return databaseName;
	}

	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}

	public String getDatabaseCategory() {
		return databaseCategory;
	}

	public void setDatabaseCategory(String databaseCategory) {
		this.databaseCategory = databaseCategory;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getLinkUrl() {
		return linkUrl;
	}

	public void setLinkUrl(String linkUrl) {
		this.linkUrl = linkUrl;
	}
	
	public String getResolvedUrl() {
		if (resolvedUrl==null){
			resolvedUrl=this.resolveLinkTarget();
		}
		return resolvedUrl;
	}

	public void setResolvedUrl(String resolvedUrl) {
		this.resolvedUrl = resolvedUrl;
	}

	public List<DbXrefProperty> getProperties() {
		return properties;
	}

	public void setProperties(List<DbXrefProperty> properties) {
		this.properties = properties;
	}

	// private DbXrefProperty getProperty(String name) {
	// for(DbXrefProperty prop : this.properties)
	// if(prop.getName().equals(name)) return prop;
	// return null;
	// }

	// public String resolveLinkTarget(String primaryId) {
	// primaryId = primaryId.startsWith("NX_") ? primaryId.substring(3) :
	// primaryId;
	// if (!this.linkUrl.contains("%u")) {
	// return resolveLinkTarget();
	// }
	//
	// String templateURL = this.linkUrl;
	// if (!templateURL.startsWith("http")) {
	// templateURL = "http://" + templateURL;
	// }
	//
	// if (this.databaseName.equalsIgnoreCase("brenda")) {
	// if (getAccession().startsWith("BTO")) {
	// String accession = getAccession().replace(":", "_");
	// templateURL = CvDatabasePreferredLink.BRENDA_BTO.getLink().replace("%s",
	// accession);
	// }
	// else {
	// templateURL = templateURL.replaceFirst("%s1", getAccession());
	// String organismId = "247";
	// // this.retrievePropertyByName("organism name").getPropertyValue();
	// // organism always human: hardcode it
	// templateURL = templateURL.replaceFirst("%s2", organismId);
	// }
	// }
	//
	// return templateURL.replaceAll("%u", primaryId);
	// }
	//

	/**
	 * COPIED FROM DATAMODEL
	 * 
	 * @return the link to the xref datbase for the current element (protocol
	 *         not included)
	 */
	public String resolveLinkTarget() {
		// Deal 1rst with special cases
		String primaryId = this.getAccession();
		String db = this.getDatabaseName();
		
		//link to a web page
		if (db.equals("WEBINFO")) {
			return this.getAccession();
		}
		
		String templateURL = this.getLinkUrl();
		if (StringUtils.isEmpty(templateURL) && !CvDatabasePreferredLink.dbHasPreferredLink(db)) return "";
		
		if (!templateURL.startsWith("http")) {
			templateURL = "http://" + templateURL;
		}

		// There is a valid cds: use emblcds
		if (db.equals("EMBL") && primaryId.indexOf('.') > 0) {
			primaryId = primaryId.split("\\.")[0];
			templateURL = CvDatabasePreferredLink.EMBL.getLink();
		}
		if (db.equals("Ensembl")) {
			// organism always human: hardcode it
			if (primaryId.startsWith("ENST")) {
				templateURL = CvDatabasePreferredLink.ENSEMBL_TRANSCRIPT.getLink();
			}
			else if (primaryId.startsWith("ENSP")) {
				templateURL = CvDatabasePreferredLink.ENSEMBL_PROTEIN.getLink();
			}
			else if (primaryId.startsWith("ENSG")) {
				templateURL = CvDatabasePreferredLink.ENSEMBL_GENE.getLink();
			}
		}
        if (db.equals("Cosmic")) {
//            ResourceProperty property = retrievePropertyByName("type");
        	DbXrefProperty property = getPropertyByName("type");
            // sample type by default (cellosaurus)
            if (property == null || property.getValue().equals("sample id")) {
                templateURL = CvDatabasePreferredLink.COSMIC_SAMPLE.getLink();
            }
            else if (property.getValue().equals("mutation id")) {
                templateURL = CvDatabasePreferredLink.COSMIC_MUTATION.getLink();
            }
            else if (property.getValue().equals("gene name")) {
                templateURL = CvDatabasePreferredLink.COSMIC_GENE.getLink();
            }
        }
		
		if (db.equals("GermOnline")) {
			templateURL = CvDatabasePreferredLink.GERMONLINE.getLink();
		}

        if (db.equals("HPA")) {
            if (primaryId.startsWith("ENSG")) {
                templateURL = CvDatabasePreferredLink.HPA_GENE.getLink();
            }
            else {
                templateURL = CvDatabasePreferredLink.HPA_ANTIBODY.getLink();
            }
        }

		if (db.equals("UniGene")) {
			// organism always human: hardcode it
			templateURL = templateURL.replaceFirst("%s1", "Hs");
			templateURL = templateURL.replaceFirst("%s2", primaryId.split("\\.")[1]);
			return templateURL;
		}
		if (db.equals("UCSC")) {
			// organism always human: hardcode it
			templateURL = templateURL.replaceFirst("%s2", "human");
			templateURL = templateURL.replaceFirst("%s1", primaryId);
			return templateURL;
		}
		if (db.equals("IntAct")) {
			if (this.getAccession().startsWith("EBI")) {
				templateURL = CvDatabasePreferredLink.INTACT_BINARY.getLink();
			}
		}
		
		if (db.equals("PROSITE")) {
			// Overwrite native dbxref raw link w a more user-friendly one
			templateURL = CvDatabasePreferredLink.PROSITE.getLink();
		}
		if (db.equals("HSSP")) {
//			ResourceProperty pdbAccession = this.retrievePropertyByName("PDB accession");
			DbXrefProperty pdbAccession = getPropertyByName("type");
			if ( pdbAccession!= null && pdbAccession.getValue()!=null) {
				primaryId = getPropertyByName("PDB accession").getValue().toLowerCase();
			} else {
				primaryId = this.getAccession().toLowerCase();
			}
			
		}
		
		if (db.equals("Bgee")) {
			if (this.getAccession().startsWith("ENSG"))
			    templateURL = templateURL.replace("uniprot_id", "page=gene&action=expression&gene_id");
		}
//		if (db.equals("PeptideAtlas")) {
//		    if (accession.length() > 6)
//		        templateURL = "https://db.systemsbiology.net/sbeams/cgi/PeptideAtlas/GetPeptide?searchWithinThis=Peptide+Name&searchForThis=%s&action=QUERY";
//		}
		if (db.equals("PDB")) {
			// Overwrite native dbxref raw link w a more user-friendly one
			templateURL = CvDatabasePreferredLink.PDB.getLink();
		}
		if (db.equals("WEBINFO")) {
			templateURL = this.getAccession();
			if (!templateURL.startsWith("http")) {
				templateURL = "http://" + templateURL;
			}
			return templateURL;
		}
		
		if (db.equals("IFO") || db.equals("JCRB")) {
			primaryId = primaryId.toLowerCase();
		}
		// general case
		if (templateURL.matches(".*%s\\b.*")) {
			return templateURL.replaceAll("\"", "").replaceAll("%s", primaryId);
		}
		// failed: return home url for db
		return this.getUrl();
	}
	
	
	private DbXrefProperty getPropertyByName(String propertyName) {
		if(this.getProperties() != null)
			for(DbXrefProperty prop : this.getProperties())
				if(prop.getName().equals(propertyName))
					return prop;
		
		return null;
	}	


	public static class DbXrefProperty implements Serializable {

		private static final long serialVersionUID = 5442533253933424052L;

		private Long dbXrefId;
		private Long propertyId;
		private String name;
		private String value;

		public Long getDbXrefId() {
			return dbXrefId;
		}

		public void setDbXrefId(Long dbXrefId) {
			this.dbXrefId = dbXrefId;
		}

		public Long getPropertyId() {
			return propertyId;
		}

		public void setPropertyId(Long propertyId) {
			this.propertyId = propertyId;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}
	}

}
