package org.nextprot.api.core.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
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
	public static class DBXref implements Serializable {
		
		private static final long serialVersionUID = 1L;

		private String db;
		private String dbkey;

		public String getDb() {
			return db;
		}

		public void setDb(String db) {
			this.db = db;
		}

		public String getDbkey() {
			return dbkey;
		}

		public void setDbkey(String dbkey) {
			this.dbkey = dbkey;
		}



	}

	public static class MDataPublication implements Serializable {
		
		private static final long serialVersionUID = 1L;

		private String type;

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public String getKey() {
			return key;
		}

		public void setKey(String key) {
			this.key = key;
		}

		public DBXref getDb_xref() {
			return db_xref;
		}

		public void setDb_xref(DBXref db_xref) {
			this.db_xref = db_xref;
		}

		private String key;
		private DBXref db_xref;


	}

	public static class MDataPublications implements Serializable {
		
		private static final long serialVersionUID = 1L;

		public List<MDataPublication> getValues() {
			return values;
		}

		@JsonProperty("publication")
		public void setPublication(List<MDataPublication> values) {
			this.values = values;
		}

		private List<MDataPublication> values;

	}

	public static class MDataMetaDataProperties implements Serializable {
		
		private static final long serialVersionUID = 1L;
		private String isVariable;
		private String label;

		public String getIsVariable() {
			return isVariable;
		}

		public void setIsVariable(String isVariable) {
			this.isVariable = isVariable;
		}

		public String getLabel() {
			return label;
		}

		public void setLabel(String label) {
			this.label = label;
		}

		public String getContent() {
			return content;
		}

		public void setContent(String content) {
			this.content = content;
		}

		private String content;

	}



	@JsonInclude(JsonInclude.Include.NON_NULL)
	public static class MDataMetaData implements Serializable {
		
		private static final long serialVersionUID = 1L;

		private MDataMetaDataProperties BS;
		private MDataMetaDataProperties CC;
		private MDataMetaDataProperties CL;
		private MDataMetaDataProperties DA;
		private MDataMetaDataProperties DC;
		private MDataMetaDataProperties DE;
		private MDataMetaDataProperties DI;
		private MDataMetaDataProperties DM;
		private MDataMetaDataProperties DP;
		private MDataMetaDataProperties IP;
		private MDataMetaDataProperties OG;
		private MDataMetaDataProperties SP;
		private MDataMetaDataProperties TP;
		private MDataMetaDataProperties TS;


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


		public MDataMetaDataProperties getDI() {
			return DI;
		}

		public MDataMetaDataProperties getDM() {
			return DM;
		}

		public MDataMetaDataProperties getDE() {
			return DE;
		}


		public MDataMetaDataProperties getIP() {
			return IP;
		}


		public MDataMetaDataProperties getOG() {
			return OG;
		}

		public MDataMetaDataProperties getTP() {
			return TP;
		}

		@JsonProperty("BS")
		public void setBS(MDataMetaDataProperties BS) {
			this.BS = BS;
		}

		@JsonProperty("CC")
		public void setCC(MDataMetaDataProperties CC) {
			this.CC = CC;
		}
		@JsonProperty("CL")
		public void setCL(MDataMetaDataProperties CL) {
			this.CL = CL;
		}

		@JsonProperty("DA")
		public void setDA(MDataMetaDataProperties DA) {
			this.DA = DA;
		}

		@JsonProperty("DC")
		public void setDC(MDataMetaDataProperties DC) {
			this.DC = DC;
		}

		@JsonProperty("DE")
		public void setDE(MDataMetaDataProperties DE) {
			this.DE = DE;
		}

		@JsonProperty("DI")
		public void setDI(MDataMetaDataProperties DI) {
			this.DI = DI;
		}

		@JsonProperty("DM")
		public void setDM(MDataMetaDataProperties DM) {
			this.DM = DM;
		}

		@JsonProperty("DP")
		public MDataMetaDataProperties getDP() {
			return DP;
		}

		@JsonProperty("DP")
		public void setDP(MDataMetaDataProperties DP) {
			this.DP = DP;
		}

		@JsonProperty("IP")
		public void setIP(MDataMetaDataProperties IP) {
			this.IP = IP;
		}

		@JsonProperty("OG")
		public void setOG(MDataMetaDataProperties OG) {
			this.OG = OG;
		}

		public MDataMetaDataProperties getSP() {
			return SP;
		}

		@JsonProperty("SP")
		public void setSP(MDataMetaDataProperties SP) {
			this.SP = SP;
		}


		@JsonProperty("TP")
		public void setTP(MDataMetaDataProperties TP) {
			this.TP = TP;
		}

		public MDataMetaDataProperties getTS() {
			return TS;
		}

		@JsonProperty("TS")
		public void setTS(MDataMetaDataProperties TS) {
			this.TS = TS;
		}


	}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public static class MDataContext implements Serializable {

		private static final long serialVersionUID = 1L;

		private MDataMetaData metadata;

		public MDataMetaData getMetadata() {
			return metadata;
		}

		public void setMetadata(MDataMetaData metadata) {
			this.metadata = metadata;
		}

		public MDataPublications getPublications() {
			return publications;
		}

		public void setPublications(MDataPublications publications) {
			this.publications = publications;
		}

		private MDataPublications publications;

	}


}
