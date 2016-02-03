package org.nextprot.api.core.domain.publication;

import java.io.Serializable;

public class JournalLocator extends BookMediumLocator<JournalLocation> implements Serializable {

    private static final long serialVersionUID = 0L;

    private long journalId;	// Internal journal id
    private String abbrev;	// ISO journal abbreviation
    private String med_abbrev;	// Medline journal abbreviation
    private String nlmid;	// The National Library of Medicine id, eg: http://www.ncbi.nlm.nih.gov/nlmcatalog/?term=0404511
    // WE are missing issn and e-isnn
    private long publicationId = -1L;

    public JournalLocator(PublicationType publicationType) {
        super(publicationType);
    }

    public long getJournalId() {
        return journalId;
    }

    public void setJournalId(long journalId) {
        this.journalId = journalId;
    }

    public String getAbbrev() {
        return abbrev;
    }

    public void setAbbrev(String abbrev) {
        this.abbrev = abbrev;
    }

    public String getMedAbbrev() {
        return med_abbrev;
    }

    public void setMedAbbrev(String medabbrev) {
        this.med_abbrev = medabbrev;
    }

    public String getNLMid() {
        return nlmid;
    }

    public void setNLMid(String nlmid) {
        this.nlmid = nlmid;
    }

    public long getPublicationId() {
        return publicationId;
    }

    public void setPublicationId(long publicationId) {
        this.publicationId = publicationId;
    }

    @Override
    PublicationType getExpectedPublicationType() {
        return PublicationType.ARTICLE;
    }
}
