package org.nextprot.api.core.domain.publication;

import org.jsondoc.core.annotation.ApiObjectField;

public class JournalLocation extends BookLocation {

    private static final long serialVersionUID = 0L;

    @ApiObjectField(description = "The journal volume")
    private String volume;

    @ApiObjectField(description = "The journal issue")
    private String issue;

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
}