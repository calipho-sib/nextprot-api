package org.nextprot.api.blast.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.nextprot.api.blast.service.BlastProgram;

@JsonPropertyOrder({
        "title",
        "sequence",
        "search_settings"
})
public class BlastSequenceInput extends BlastProgram.Params {

    private String title;
    private String sequence;
    private BlastSearchParams blastSearchParams;

    public BlastSequenceInput(String binPath, String nextprotBlastDbPath) {
        super(binPath, nextprotBlastDbPath);
    }

    public String getTitle() {

        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSequence() {

        return sequence;
    }

    public void setSequence(String sequence) {

        this.sequence = sequence;
    }

    public void setBlastSearchParams(BlastSearchParams blastSearchParams) {
        this.blastSearchParams = blastSearchParams;
    }

    @JsonProperty("search_settings")
    public BlastSearchParams getSearchParams() {

        return blastSearchParams;
    }
}
