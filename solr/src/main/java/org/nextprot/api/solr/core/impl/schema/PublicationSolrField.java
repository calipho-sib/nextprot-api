package org.nextprot.api.solr.core.impl.schema;

import org.nextprot.api.solr.core.SolrField;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public enum PublicationSolrField implements SolrField {
    ID("id"),
    IDSP0("idsp0"),                   // searchable (text_split0)
    AC("ac"), 						  // PMIDs + DOIs
    VOLUME_S("volume_s"),             // sortable (string)
    VOLUME("volume", "volume"),       // searchable, also used for display by UI (text_split0)
    FIRST_PAGE("first_page"),
    LAST_PAGE("last_page"),
    YEAR("year","year"),
    DATE("date"),
    TITLE("title","title"),           // searchable, displayable (text_split0)
    TITLE_S("title_s"),               // sortable (string)
    ABSTRACT("abstract","abstract"),
    TYPE("type"),
    JOURNAL("journal","journal"),     // searchable (text_split0)
    PRETTY_JOURNAL("pretty_journal"), // displayable (string)
    //SOURCE("source"),
    AUTHORS("authors","author"),      // searchable (text_split0)
    PRETTY_AUTHORS("pretty_authors"), // displayable  (also text_split0 but formatted)
    FILTERS("filters"), 			  // Computed, curated, largescale
    TEXT("text");

    private String name;
    private String publicName;

    PublicationSolrField(String name) {
        this.name = name;
    }

    PublicationSolrField(String name, String publicName) {
        this.name = name;
        this.publicName = publicName;
    }

    public String getName() {
        return this.name;
    }

    public boolean hasPublicName() {
        return this.publicName!=null && this.publicName.length()>0;
    }

    public String getPublicName() {
        return this.publicName;
    }

    public static Set<PublicationSolrField> allNonRedundantFields() {

	    Set<PublicationSolrField> all = new HashSet<>(Arrays.asList(PublicationSolrField.values()));
	    all.remove(PublicationSolrField.TEXT);

	    return all;
    }
}
