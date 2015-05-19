package org.nextprot.api.web.service.impl;

import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.core.service.export.format.NPFileFormat;

public enum NPFileExporter {

    XML(new XMLStreamExporter()),
    JSON(new JSONStreamExporter()),
    FASTA(new FastaStreamExporter()),
    PEFF(new PeffStreamExporter2())
    ;

    private NPStreamExporter exporter;

    NPFileExporter(NPStreamExporter exporter) {

        this.exporter = exporter;
    }

    public NPStreamExporter getNPStreamExporter() {
        return exporter;
    }

    public static NPFileExporter valueOf(NPFileFormat format) {

        switch (format) {

            case XML:
                return XML;
            case JSON:
                return JSON;
            case FASTA:
                return FASTA;
            case PEFF:
                return PEFF;
            default:
                throw new NextProtException("Format "+format+" not yet supported");
        }
    }
}
