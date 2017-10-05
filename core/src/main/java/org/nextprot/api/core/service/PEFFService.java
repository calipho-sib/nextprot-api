package org.nextprot.api.core.service;


import org.nextprot.api.core.domain.IsoformPEFFHeader;

public interface PEFFService {

    /**
     * Extract and format in PEFF header all isoform required information
     * @param isoformAccession the isoform accession to format as PEFF
     * @return a PEFF header pojo
     */
    IsoformPEFFHeader formatPEFFHeader(String isoformAccession);
}
