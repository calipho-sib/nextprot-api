package org.nextprot.api.solr.core;

public enum CvSolrField implements SolrField {
    ID("id"),
    AC("ac"),
    NAME("name"),
    NAME_S("name_s"),
    SYNONYMS("synonyms"),
    DESCRIPTION("description"),
    PROPERTIES("properties"),
    OTHER_XREFS("other_xrefs"),
    FILTERS("filters"),
    SCORE("score"),
    TEXT("text");

    private String name;

    CvSolrField(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public String getPublicName() {
        return null;
    }

    @Override
    public boolean hasPublicName() {
        return false;
    }
}
