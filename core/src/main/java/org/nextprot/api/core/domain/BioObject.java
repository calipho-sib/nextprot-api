package org.nextprot.api.core.domain;

import com.google.common.base.Preconditions;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class BioObject implements Serializable {

	
	private static final String ANNOTATION_HASH_PROPERTY_NAME =  "annotationHash";
	
    private static final long serialVersionUID = 3L;

    public static final String NEXTPROT_DATABASE = "neXtProt"; // ok: this is the cv_name of nextprot in cv_databases (!= cv_datasources)

    public enum BioType { CHEMICAL, PROTEIN, PROTEIN_ISOFORM, COMPLEX, GROUP, ENTRY_ANNOTATION} //TODO daniel should this be normal annotation or simply normal?
    
    //When it is internal, it is something that exists in neXtProt (sequence). When it is EXTERNAL it is a xref (link to another db). MIXED when both are 
    public enum ResourceType { INTERNAL, EXTERNAL, MIXED } 

    private long id;
    private String accession;
    private final String database;
    private final BioType bioType;
    private final ResourceType resourceType;
    private Map<String, String> properties = new HashMap<>();

    public BioObject(BioType bioType, ResourceType resourceType, String database) {

        Preconditions.checkNotNull(bioType);

        this.bioType = bioType;
        this.resourceType = resourceType;
        this.database = database;
    }

    public static BioObject internal(BioType bioType) {

        return new BioObject(bioType, ResourceType.INTERNAL, NEXTPROT_DATABASE);
    }

    public static BioObject external(BioType bioType, String database) {

        return new BioObject(bioType, ResourceType.EXTERNAL, database);
    }

    protected String toBioObjectString() {
        return "";
    }
    
    protected String toBaseString() {

        StringBuilder sb= new StringBuilder();

        sb.append("BioObject id:").append(id)
        .append(", accession:").append(accession)
        .append(", database:").append(database)
        .append(", bioType:").append(bioType)
        .append(", resourceType:").append(resourceType)
        .append(" specific content:").append(toBioObjectString());

        return sb.toString();
    }
    
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

    public String getDatabase() {
        return database;
    }

    public BioType getBioType() {
        return bioType;
    }

    public ResourceType getResourceType() {
        return resourceType;
    }

    public void setAnnotationHash(String value) {
    	this.properties.put(ANNOTATION_HASH_PROPERTY_NAME, value);
    }

    
    public void putPropertyNameValue(String name, String value) {
        this.properties.put(name, value);
    }

    public String getPropertyValue(String name) {
        return this.properties.get(name);
    }

    public Map<String, String> getProperties() {
    	return this.properties;
    }

    public String getAnnotationHash() {
    	return properties.get(ANNOTATION_HASH_PROPERTY_NAME);
    }

    public boolean isInteractant() {

        return bioType == BioType.PROTEIN || bioType == BioType.PROTEIN_ISOFORM || bioType == BioType.CHEMICAL;
    }
    
    public int size() {
        return 1;
    }

    public String toString() {
    	return "BioObject id: " + id + " " + database +":" +  accession + " ty:"  + bioType + " rt:" + resourceType;
    }
}
