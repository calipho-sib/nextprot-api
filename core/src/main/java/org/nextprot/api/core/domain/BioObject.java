package org.nextprot.api.core.domain;

import com.google.common.base.Preconditions;

import java.io.Serializable;
import java.util.Objects;

/**
 * A wrapper over biological domain object
 *
 * Created by fnikitin on 26/08/15.
 */
public abstract class BioObject<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    protected static final String NEXTPROT = "neXtProt";

    public enum BioType { CHEMICAL, PROTEIN, PROTEIN_ISOFORM, COMPLEX, GROUP, NORMAL_ANNOTATION} //TODO daniel should this be normal annotation or simply normal?
    public enum ResourceType { INTERNAL, EXTERNAL, MIXED }

    private long id;
    private String accession;
    private final String database;
    private final BioType bioType;
    private final ResourceType resourceType;
    transient private T content;

    protected BioObject(BioType bioType, ResourceType resourceType, String database) {

        Preconditions.checkNotNull(bioType);

        this.bioType = bioType;
        this.resourceType = resourceType;
        this.database = database;
    }

    protected abstract String toBioObjectString();
    
    
    protected String toBaseString() {
    	{
        	StringBuilder sb= new StringBuilder();
        	sb.append("BioObject id:").append(id)
        	.append(", accession:").append(accession)
        	.append(", database:").append(database)
        	.append(", bioType:").append(bioType)
        	.append("resourceType:").append(resourceType)
        	.append(" specific content:").append(toBioObjectString());
        	return sb.toString();
        }
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

    public int size() {
        return 1;
    }

    public T getContent() {
        return content;
    }

    public void setContent(T content) {
        this.content = content;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BioObject)) return false;
        BioObject<?> bioObject = (BioObject<?>) o;
        return id == bioObject.id &&
                Objects.equals(accession, bioObject.accession) &&
                Objects.equals(database, bioObject.database) &&
                bioType == bioObject.bioType &&
                resourceType == bioObject.resourceType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, accession, database, bioType, resourceType);
    }
}
