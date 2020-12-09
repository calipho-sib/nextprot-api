package org.nextprot.api.core.domain;

import org.jsondoc.core.annotation.ApiObject;

import java.io.Serializable;

/**
 * @author Valentine Rech de Laval
 * @since 2020-11-19
 */
@ApiObject(name = "global entry statistics", description = "Global entry statistics")
public class GlobalEntryStatistics implements Serializable {

    private static final long serialVersionUID = 5321718660333712596L;

    private int numberOfEntriesWithExpressionProfile;
    private int numberOfEntriesWithBinaryInteraction;

    public int getNumberOfEntriesWithExpressionProfile() {
        return numberOfEntriesWithExpressionProfile;
    }

    public void incrementNumberOfEntriesWithExpressionProfile() {
        this.numberOfEntriesWithExpressionProfile++;
    }

    public int getNumberOfEntriesWithBinaryInteraction() {
        return numberOfEntriesWithBinaryInteraction;
    }

    public void incrementNumberOfEntriesWithBinaryInteraction() {
        this.numberOfEntriesWithBinaryInteraction++;
    }

    public void setNumberOfEntriesWithBinaryInteraction(int nb) {
        this.numberOfEntriesWithBinaryInteraction = this.numberOfEntriesWithBinaryInteraction + nb;
    }
}
