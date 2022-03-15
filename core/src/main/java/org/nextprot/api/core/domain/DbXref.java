package org.nextprot.api.core.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Preconditions;
import org.jsondoc.core.annotation.ApiObject;
import org.jsondoc.core.annotation.ApiObjectField;
import org.nextprot.api.commons.constants.IdentifierOffset;
import org.nextprot.api.core.service.dbxref.resolver.DbXrefURLResolverDelegate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@ApiObject(name = "xref", description = "A cross reference")
public class DbXref implements Serializable {

	private static final long serialVersionUID = 2L;

	@ApiObjectField(description = "The entry identifier referering this DbXref")
	private String proteinAccessionReferer = "";

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

	@ApiObjectField(description = "The resolved url")
	private String resolvedUrl;

    @ApiObjectField(description = "The link url")
    private String linkUrl;

	@ApiObjectField(description = "A list of properties. A property contains an accession, a property name and a value.")
	private List<DbXrefProperty> properties = new ArrayList<>();

	public Long getDbXrefId() {
		return dbXrefId;
	}

	public void addProperty(String name, String value, Long propertyId) {
		DbXrefProperty p = new DbXrefProperty();
		p.setDbXrefId(this.dbXrefId);
		p.setName(name);
		p.setValue(value);
		p.setPropertyId(propertyId);
		this.properties.add(p);
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

	@JsonIgnore
	public String getProteinAccessionReferer() {
		return proteinAccessionReferer;
	}

	public void setProteinAccessionReferer(String proteinAccessionReferer) {
		this.proteinAccessionReferer = proteinAccessionReferer;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {

		// sometimes xref URL is not valid on NPDB (TODO: should fix the url directly there !!)
		this.url = new DbXrefURLResolverDelegate().getValidXrefURL(url, databaseName);
	}

	public String getResolvedUrl() {

		if (resolvedUrl == null) {
			try {
				resolvedUrl = new DbXrefURLResolverDelegate().resolve(this);
			} catch (Exception ex) {

				//LOGGER.warn("xref "+accession+" (db:"+databaseName+") - " + ex.getLocalizedMessage(), ex);
				resolvedUrl = "None";
			}
		}

		return resolvedUrl;
	}

    public String getLinkUrl() {
        return linkUrl;
    }

    public void setLinkUrl(String linkUrl) {
        this.linkUrl = linkUrl;
    }
	public List<DbXrefProperty> getProperties() {
		return properties;
	}

	public void addProperties(List<DbXrefProperty> props) {

		properties.addAll(props);
	}

	public String getPropertyValue(String name) {
		for (DbXrefProperty prop: properties) {
			if (name.equals(prop.getName())) return prop.getValue();
		}
		return null;
	}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DbXref dbXref = (DbXref) o;
        return Objects.equals(dbXrefId, dbXref.dbXrefId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dbXrefId);
    }

    public void setProperties(List<DbXrefProperty> properties) {
		this.properties = properties;
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
		
		public String toString() {
			StringBuffer sb = new StringBuffer();
			sb.append("xrefId=" + this.dbXrefId);
			sb.append(" propId=" + this.getPropertyId());
			sb.append(" " + this.getName()+ "=" + this.getValue());
			return sb.toString();
		}

	}

	public static class EnsemblInfos {

		private final long enstXrefId; 			// db_xrefs.resource_id of ENST
		private final long enstIsoMapId;		// mapping_annotations.annotation_id as the unique identifier for ENST-iso pairs
		private final long enstIsoMapQual;		// quality of ENST-iso alignment
		private final String enst;		
		private final String iso;
		private final String ensg;
		private final String ensp;

		public EnsemblInfos(long enstXrefId, long enstIsoMapId, long enstIsoMapQual, 
				String enst, String ensg, String ensp, String iso) {

			Preconditions.checkArgument(ensg.startsWith("ENSG"));
			Preconditions.checkArgument(ensp==null || ensp.startsWith("ENSP"));

			this.enstXrefId = enstXrefId;
			this.enstIsoMapId = enstIsoMapId;
			this.enstIsoMapQual = enstIsoMapQual;
			this.enst = enst;
			this.iso = iso;
			this.ensg = ensg;
			this.ensp = ensp;
			
		}

		public EnsemblInfos(DbXref.DbXrefProperty prop) {

			if (! prop.getName().equals("nxmap")) 
				throw new RuntimeException("Error on trying to create new EnsemblInfo from nxmap property, but property name was: " + prop.getName() );
			
			this.enstXrefId = prop.getDbXrefId();
			this.enstIsoMapId = prop.getPropertyId() - IdentifierOffset.XREF_PROPERTY_OFFSET;
			String[] fields = prop.getValue().split("\\|");
	        //"ENST1|ENSG2|ENSP3|NX_A00001-1|GOLD"
			this.enst = fields[0];
			this.ensg = fields[1];
			this.ensp = fields[2].length()==0 ? null : fields[2];
			this.iso = fields[3]; 
			this.enstIsoMapQual = fields[4].equals("GOLD") ? 10 : fields[4].equals("SILVER") ? 50 : 100;
		}
		
		public DbXref.DbXrefProperty toDbXrefProperty() {

			DbXref.DbXrefProperty prop = new DbXref.DbXrefProperty();
	        prop.setDbXrefId(this.getEnstXrefId());
	        prop.setPropertyId(IdentifierOffset.XREF_PROPERTY_OFFSET + this.getEnstIsoMapId());
	        //prop.setName("nxmap_" + this.getEnstIsoMapId());
	        prop.setName("nxmap"); // multiple props with same name are allowed
	        //"ENST1|ENSG2|ENSP3|NX_A00001-1|GOLD"
	        StringBuilder sb = new StringBuilder();
	        sb.append(this.getEnst());
	        sb.append("|");
	        sb.append(this.getEnsg());
	        sb.append("|");
	        sb.append(this.getEnsp()==null ? "" : this.getEnsp());
	        sb.append("|");
	        sb.append(this.getIso());
	        sb.append("|");
	        sb.append(this.getEnstIsoMapQual()==10 ? "GOLD" : this.getEnstIsoMapQual()==50 ? "SILVER" : "BRONZE");
	        prop.setValue(sb.toString());
			return prop;
		}
		
		
		public long getEnstXrefId() {
			return enstXrefId;
		}

		public String getIso() {
			return iso;
		}

		public String getEnsg() {
			return ensg;
		}

		public String getEnsp() {
			return ensp;
		}

		public long getEnstIsoMapId() {
			return enstIsoMapId;
		}

		public long getEnstIsoMapQual() {
			return enstIsoMapQual;
		}

		public String getEnst() {
			return enst;
		}

	}

}
