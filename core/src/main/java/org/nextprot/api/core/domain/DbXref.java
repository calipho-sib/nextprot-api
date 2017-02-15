package org.nextprot.api.core.domain;

import com.google.common.base.Preconditions;
import org.jsondoc.core.annotation.ApiObject;
import org.jsondoc.core.annotation.ApiObjectField;
import org.nextprot.api.core.utils.dbxref.resolver.DbXrefURLResolverDelegate;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

@ApiObject(name = "xref", description = "A cross reference")
public class DbXref implements Serializable {

	//private static final Log LOGGER = LogFactory.getLog(DbXref.class);

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
	private List<DbXrefProperty> properties = Collections.emptyList();

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

	public String getResolvedUrl(String entryAccession) {
		try {
			return new DbXrefURLResolverDelegate().resolve(this, entryAccession);
		} catch (Exception ex) {

			//LOGGER.warn("xref "+accession+" (db:"+databaseName+") - " + ex.getLocalizedMessage(), ex);
			return  "None";
		}
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
