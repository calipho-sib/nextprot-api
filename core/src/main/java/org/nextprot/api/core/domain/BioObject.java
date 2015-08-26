package org.nextprot.api.core.domain;

import com.google.common.base.Preconditions;

/**
 * A wrapper over biological domain object
 *
 * Created by fnikitin on 26/08/15.
 */
public abstract class BioObject<T> {

    public enum Kind { MOLECULE, PROTEIN, ISOFORM, GROUP }

    private long id;
    private String accession;
    private String database;
    private final Kind kind;
    private T content;

    protected BioObject(Kind kind) {

        Preconditions.checkNotNull(kind);

        this.kind = kind;
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

    public void setDatabase(String database) {
        this.database = database;
    }

    public Kind getKind() {
        return kind;
    }

    public T getContent() {
        return content;
    }

    public void setContent(T content) {
        this.content = content;
    }
}
