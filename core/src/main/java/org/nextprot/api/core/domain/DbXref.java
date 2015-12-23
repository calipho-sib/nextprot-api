package org.nextprot.api.core.domain;

import org.apache.commons.lang.StringUtils;
import org.jsondoc.core.annotation.ApiObject;
import org.jsondoc.core.annotation.ApiObjectField;
import org.nextprot.api.core.utils.dbxref.DbXrefURLResolver;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

@ApiObject(name = "xref", description = "A cross reference")
public class DbXref implements Serializable {

	private static final long serialVersionUID = 2316953378438971441L;

	@ApiObjectField(description = "The neXtProt identifier")
	// TODO: unecessary primitive wrapping
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
	private List<DbXrefProperty> properties = Collections.emptyList();

	// TODO: unecessary primitive wrapping
	public Long getDbXrefId() {
		return dbXrefId;
	}

	// TODO: unecessary primitive wrapping
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
		if (resolvedUrl == null) {
			resolvedUrl = DbXrefURLResolver.getInstance().resolve(this);
		}
		return resolvedUrl;
	}

	public void setResolvedUrl(String resolvedUrl) {
		this.resolvedUrl = resolvedUrl;
	}

	public List<DbXrefProperty> getProperties() {
		return properties;
	}

	public String getPropertyValue(String name) {
		for (DbXrefProperty prop: properties) {
			if (name.equals(prop.getName())) return prop.getValue();
		}
		return null;
	}
	
	public void setProperties(List<DbXrefProperty> properties) {
		this.properties = properties;
	}

    /**
     * COPIED FROM DATAMODEL
     *
     * @return the link to the xref datbase for the current element (protocol
     *         not included)
     */
	@Deprecated
	String resolveLinkTarget() {

		// Deal 1rst with special cases
		String primaryId = this.getAccession();
		String db = this.getDatabaseName();
		
		//link to a web page
		if (db.equals("WEBINFO")) {
			return getAccession();
		}
		
		String templateURL = getLinkUrl();
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
            if (primaryId.startsWith("COSM")) {
                templateURL = CvDatabasePreferredLink.COSMIC_MUTATION.getLink();                
                primaryId = primaryId.replaceFirst("COSM", "");
            }
            else if (primaryId.startsWith("COSS")) {
                templateURL = CvDatabasePreferredLink.COSMIC_SAMPLE.getLink();                
                primaryId = primaryId.replaceFirst("COSS", "");
            }
            else {
                templateURL = CvDatabasePreferredLink.COSMIC_GENE.getLink();                
            }
        }
        else if (db.equals("Clinvar")) {
            if (primaryId.matches("RCV\\d+")) {
                templateURL = CvDatabasePreferredLink.CLINVAR_MUTATION.getLink();                
            }
            else {
                templateURL = CvDatabasePreferredLink.CLINVAR_GENE.getLink();                
            }
        }
        if (db.equals("PIR")) {
            DbXrefProperty property = this.getPropertyByName("entry name");
            primaryId = property.getValue();
        }
		
		if (db.equals("GermOnline")) {
			templateURL = CvDatabasePreferredLink.GERMONLINE.getLink();
		}

		if (db.equals("Genevestigator")) {
            templateURL = CvDatabasePreferredLink.GENEVESTIGATOR.getLink();
        }

        if (db.equals("HPA")) {
            if (primaryId.startsWith("ENSG")) {
                if (primaryId.endsWith("subcellular")) {
                    templateURL = CvDatabasePreferredLink.HPA_SUBCELL.getLink();
                }
                else {
                    templateURL = CvDatabasePreferredLink.HPA_GENE.getLink();                    
                }
            }
            else {
                templateURL = CvDatabasePreferredLink.HPA_ANTIBODY.getLink();
            }
        }

		// TODO: Should The following db templates be tested for emptiness and valid format ??

		if (db.equals("Genevisible")) {
			// organism always human: hardcode it
			templateURL = templateURL.replaceFirst("%s1", primaryId);
			templateURL = templateURL.replaceFirst("%s2", "HS");
			return templateURL;
		}
		if (db.equals("UniGene")) {
			// organism always human: hardcode it
			templateURL = templateURL.replaceFirst("%s1", "Hs");
			templateURL = templateURL.replaceFirst("%s2", primaryId.split("\\.")[1]);
			return templateURL;
		}
		if (db.equals("UCSC")) {
			// organism always human: hardcode it
			templateURL = templateURL.replaceFirst("%s1", primaryId);
            templateURL = templateURL.replaceFirst("%s2", "human");
			return templateURL;
		}
		if (db.equals("IntAct")) {
			if (getAccession().startsWith("EBI")) {
				templateURL = CvDatabasePreferredLink.INTACT_BINARY.getLink();
			}
		}
		
		if (db.equals("PROSITE")) {
			// Overwrite native dbxref raw link w a more user-friendly one
			templateURL = CvDatabasePreferredLink.PROSITE.getLink();
		}

		// TODO: obsolete xref ? no entry found
		if (db.equals("HSSP")) {
			DbXrefProperty pdbAccession = this.getPropertyByName("PDB accession");
			if ( pdbAccession!= null && pdbAccession.getValue()!=null) {
				primaryId = this.getPropertyByName("PDB accession").getValue().toLowerCase();
			} else {
				primaryId = accession.toLowerCase();
			}
			
		}
		
		if (db.equals("Bgee")) {
			if (accession.contains("ENSG"))
			    templateURL = templateURL.replace("uniprot_id=", "page=expression&action=data&");
		}

		if (db.equals("PeptideAtlas")) {
            // protein URL
		    if (!accession.startsWith("PAp")) {
                templateURL = "https://db.systemsbiology.net/sbeams/cgi/PeptideAtlas/GetProtein?protein_name=%s;organism_name=Human;action=GO";
		    }
            // peptide URL
		    else {
                templateURL = "https://db.systemsbiology.net/sbeams/cgi/PeptideAtlas/GetPeptide?searchWithinThis=Peptide+Name&searchForThis=%s;organism_name=Human";
		    }
		}

		if (db.equals("SRMAtlas")) {
            primaryId = getPropertyByName("sequence").getValue();
        }

		if (db.equals("PDB")) {
			// Overwrite native dbxref raw link w a more user-friendly one
			templateURL = CvDatabasePreferredLink.PDB.getLink();
		}
		// WTF???
		if (db.equals("WEBINFO")) {
			templateURL = accession;
			if (!templateURL.startsWith("http")) {
				templateURL = "http://" + templateURL;
			}
			return templateURL;
		}

//		Db_URL: http://www2.idac.tohoku.ac.jp/dep/ccr/TKGdate/TKGvo10%n/%s.html
//	        Note: n% is the second digit of the cell line AC and %s is the cell line AC without the 'TKG'
//	        Example: for "DR   TKG; TKG 0377": n%=3 s%=0377
        if (db.equals("TKG")) {
            templateURL = templateURL.replaceAll("%n", String.valueOf(primaryId.charAt(1)));
        }

//        Db_URL: https://www.aidsreagent.org/reagentdetail.cfm?t=cell_lines&id=%s
//            Note: %s is the value after the dash in the DR line.
//            Example: for "DR   NIH-ARP; 11411-223": s%=223
        if (db.equals("NIH-ARP")) {
            primaryId = primaryId.replaceAll("^.+-", "");
        }

//        Db_URL: http://www.cghtmd.jp/CGHDatabase/mapViewer?hid=%s&aid=%t&lang=en
//            Note: %s and %t are respectively the values before and after the dash in the DR line.
//            Example: for "DR   CGH-DB; 9029-4": s%=9029, t%=4
        if (db.equals("CGH-DB")) {
            templateURL = templateURL.replaceAll("%t", primaryId.replaceAll("^.+-", ""));
            primaryId = primaryId.replaceAll("-.+$", "");
        }
		// jcrb: http://cellbank.nibio.go.jp/~cellbank/en/search_res_list.cgi?KEYWOD=%s
		// ifo:  http://cellbank.nibio.go.jp/~cellbank/cgi-bin/search_res_det.cgi?RNO=%s
		if (db.equals("IFO") || db.equals("JCRB")) {
			primaryId = primaryId.toLowerCase();
		}

		// TODO: ???
		// the following replace concerns the dbs CLO, PRO, FMA, Uberon and CL
		if (this.getLinkUrl().contains("purl.obolibrary.org/obo")) {
            primaryId = primaryId.replaceFirst(":", "_");
        }

		// general case
		if (templateURL.matches(".*%s\\b.*")) {
			return templateURL.replaceAll("\"", "").replaceAll("%s", primaryId);
		}

		// failed: return home url for db
		return this.getUrl();
	}

	@Deprecated
    public static String resolvePercentULinkTarget(String uniqueName, DbXref xref) {

        if (!xref.getLinkUrl().contains("%u")) {
            return xref.getResolvedUrl();
        }

        String primaryId = uniqueName.startsWith("NX_") ? uniqueName.substring(3) : uniqueName;

        String templateURL = xref.getLinkUrl();
        if (!templateURL.startsWith("http")) {
            templateURL = "http://" + templateURL;
        }

        if (xref.getDatabaseName().equalsIgnoreCase("brenda")) {
			// TODO: obsolete kind of brenda accession number ? no entry found
            if (xref.getAccession().startsWith("BTO")) {
                templateURL = CvDatabasePreferredLink.BRENDA_BTO.getLink().replace("%s", xref.getAccession().replace(":", "_"));
            }
            else {
				// TODO: WTF ??? no %s1 and no %s2 in templateURL!!!
                templateURL = templateURL.replaceFirst("%s1", xref.getAccession());

                // organism always human: hardcoded as "247"
                templateURL = templateURL.replaceFirst("%s2", "247");
            }
        }

        return templateURL.replaceAll("%u", primaryId);
    }

	public DbXrefProperty getPropertyByName(String propertyName) {
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
