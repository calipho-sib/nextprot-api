package org.nextprot.api.core.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.json.JSONObject;
import org.json.XML;
import org.nextprot.api.commons.exception.NextProtException;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

public class Mdata implements Serializable{


	private static final long serialVersionUID = 1;

	private long id; 			// equivalent to publication id
	private String accession;   // MDATA name      
	private String title; 
	private String rawXml;
	private MDataContext mdataContext;

	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getAccession() {
		return accession;
	}
	public void setAccession(String accession) {
		this.accession = accession;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}

	@JsonIgnore
	public String getRawXml() {
		return rawXml;
	}

	public void setRawXml(String rawXml) {
		this.rawXml = rawXml;
		if(!this.rawXml.isEmpty()){
			this.mdataContext = convertXmlToMDataContext(this.rawXml);
		}
	}

	static MDataContext convertXmlToMDataContext(String xml){
		try {
			JSONObject jObject = XML.toJSONObject(xml);
			ObjectMapper mapper = new ObjectMapper();
			mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
			mapper.enable(SerializationFeature.INDENT_OUTPUT);
			return mapper.readValue(jObject.toString(), MDataContext.class);
		} catch (IOException e) {
			System.out.println(xml);
			throw new NextProtException("Failed to convert XML mdata to object ", e);
		}
	}

	public MDataContext getMdataContext() {
		return mdataContext;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("id       : " + this.id + "\n");
		sb.append("ac       : " + this.accession + "\n");
		sb.append("title    : " + this.title + "'\n");
		return sb.toString();
	}


	// UTILITY CLASSES TO REPRESENT MDATA ////////////////////////////////////
	static class DBXref {
		@JsonProperty
		private String db, dbkey;

		public String getDb() {
			return db;
		}

		public String getDbkey() {
			return dbkey;
		}

	}

	static class MDataPublication {
		@JsonProperty
		private String type, key;

		@JsonProperty
		private DBXref db_xref;

		public String getType() {
			return type;
		}

		public String getKey() {
			return key;
		}

		public DBXref getDb_xref() {
			return db_xref;
		}

	}

	static class MDataPublications {
		@JsonProperty
		private List<MDataPublication> publication;

		public List<MDataPublication> getPublication() {
			return publication;
		}
	}

	static class MDataMetaDataProperties {
		@JsonProperty
		private String isVariable, label, content;

		public String getIsVariable() {
			return isVariable;
		}

		public String getLabel() {
			return label;
		}

		public String getContent() {
			return content;
		}

	}



	static class MDataMetaData {
		@JsonProperty
		private MDataMetaDataProperties BS;
		@JsonProperty
		private MDataMetaDataProperties CC;
		@JsonProperty
		private MDataMetaDataProperties CL;
		@JsonProperty
		private MDataMetaDataProperties DA;
		@JsonProperty
		private MDataMetaDataProperties DC;
		@JsonProperty
		private MDataMetaDataProperties DE;
		@JsonProperty
		private MDataMetaDataProperties DI;
		@JsonProperty
		private MDataMetaDataProperties DM;
		@JsonProperty
		private MDataMetaDataProperties DP;
		@JsonProperty
		private MDataMetaDataProperties IP;
		@JsonProperty
		private MDataMetaDataProperties OG;
		@JsonProperty
		private MDataMetaDataProperties SP;
		@JsonProperty
		private MDataMetaDataProperties TP;
		@JsonProperty
		private MDataMetaDataProperties TS;
		
		// @JsonProperty private MDataMetaDataProperties CP; // ??
		
		public MDataMetaDataProperties getBS() {
			return BS;
		}
		public MDataMetaDataProperties getCC() {
			return CC;
		}
		public MDataMetaDataProperties getCL() {
			return CL;
		}
		public MDataMetaDataProperties getDA() {
			return DA;
		}
		public MDataMetaDataProperties getDC() {
			return DC;
		}
		public MDataMetaDataProperties getDE() {
			return DE;
		}
		public MDataMetaDataProperties getDI() {
			return DI;
		}
		public MDataMetaDataProperties getDM() {
			return DM;
		}
		public MDataMetaDataProperties getDP() {
			return DP;
		}
		public MDataMetaDataProperties getIP() {
			return IP;
		}
		public MDataMetaDataProperties getOG() {
			return OG;
		}
		public MDataMetaDataProperties getSP() {
			return SP;
		}
		public MDataMetaDataProperties getTP() {
			return TP;
		}
		public MDataMetaDataProperties getTS() {
			return TS;
		}

		
		
	}

	static class MDataContext {

		@JsonProperty
		private MDataMetaData metadata;
		@JsonProperty
		private MDataPublications publications;

		public MDataMetaData getMetadata() {
			return metadata;
		}

		public MDataPublications getPublications() {
			return publications;
		}

	}


}
