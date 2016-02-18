package org.nextprot.api.core.domain.publication;

import org.jsondoc.core.annotation.ApiObjectField;
import org.nextprot.api.core.domain.PublicationCvJournal;

import java.io.Serializable;

public class JournalLocation extends BookLocation implements Serializable {

    private static final long serialVersionUID = 0L;

    private PublicationCvJournal journal;

    @ApiObjectField(description = "The journal volume")
    private String volume;

    @ApiObjectField(description = "The journal issue")
    private String issue;

    public JournalLocation(PublicationCvJournal journal) {
        super(PublicationType.ARTICLE);

        this.journal = journal;
        this.setName(journal.getName());
    }

    public JournalLocation() {
        super(PublicationType.ARTICLE);
    }

    public long getJournalId() {
        return journal.getJournalId();
    }

    public String getAbbrev() {
        return journal.getAbbrev();
    }

    public String getMedAbbrev() {
        return journal.getMedAbbrev();
    }

    public String getNLMid() {
        return journal.getNLMid();
    }

    public long getPublicationId() {
        return journal.getPublicationId();
    }

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }

    public String getIssue() {
        return issue;
    }

    public void setIssue(String issue) {
        this.issue = issue;
    }

    @Override
    PublicationType getPublicationType() {
        return PublicationType.ARTICLE;
    }
}
