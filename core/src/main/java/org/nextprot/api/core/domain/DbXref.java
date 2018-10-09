package org.nextprot.api.core.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Preconditions;
import org.jsondoc.core.annotation.ApiObject;
import org.jsondoc.core.annotation.ApiObjectField;
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
	}

	public static class EnsemblInfos {

		private final long transcriptXrefId;
		private final String geneAc;
		private final String proteinAc;
		private final long genePropertyId;
		private final long proteinPropertyId;

		public EnsemblInfos(long transcriptXrefId, String geneAc, long genePropertyId, String proteinAc, long proteinPropertyId) {

			Preconditions.checkArgument(geneAc.startsWith("ENSG"));
			Preconditions.checkArgument(proteinAc.startsWith("ENSP"));

			this.transcriptXrefId = transcriptXrefId;
			this.geneAc = geneAc;
			this.proteinAc = proteinAc;
			this.genePropertyId = genePropertyId;
			this.proteinPropertyId = proteinPropertyId;
		}

		public long getTranscriptXrefId() {
			return transcriptXrefId;
		}

		public String getGeneAc() {
			return geneAc;
		}

		public String getProteinAc() {
			return proteinAc;
		}

		public long getGenePropertyId() {
			return genePropertyId;
		}

		public long getProteinPropertyId() {
			return proteinPropertyId;
		}
	}

}
