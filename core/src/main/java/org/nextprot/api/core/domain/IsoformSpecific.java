package org.nextprot.api.core.domain;

/**
 * Created by fnikitin on 12/06/15.
 */
public interface IsoformSpecific {

    /**
     * @param isoformName a nextprot isoform unique name (starting with NX_)
     * @return true if the mapping applies to the isoform otherwise false
     */
    boolean isSpecificForIsoform(String isoformName);
}
