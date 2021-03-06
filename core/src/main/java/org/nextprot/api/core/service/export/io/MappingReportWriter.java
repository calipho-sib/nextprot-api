package org.nextprot.api.core.service.export.io;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.List;

import org.nextprot.api.core.service.export.format.NextprotMediaType;

/**
 * Writes mappings in various formats
 */
public class MappingReportWriter {

    private final PrintWriter writer;

    public MappingReportWriter(OutputStream os) {
        this.writer = new PrintWriter(os);
    }
    
    public void writeHPAMapping(List<String> map_list, NextprotMediaType mediaType) {
    	for (String map : map_list) {
    		String[] fields = map.split("\\|");
    		String nxAc = fields[0];
    		String hpaAc = fields[1];
    		String line;
    		if (mediaType == NextprotMediaType.SPLOG)    line = nxAc + "    DR   HPA; " + hpaAc + "; -.\n";
    		else if (mediaType == NextprotMediaType.TSV) line = nxAc + "\t" + hpaAc + "\n";
    		else                                         line = nxAc + " " + hpaAc + "\n";
    		writer.write(line);
    	}
    }

    public void writeRefSeqMapping(List<String> map_list, NextprotMediaType mediaType) {
    	for (String map : map_list) {
    		String[] fields = map.split("\\|");
    		String nxac = fields[0];
    		String refseq_id = fields[1];
    		String refseq_nsid = fields[2];
    		String line;
    		if (mediaType == NextprotMediaType.TSV) line = nxac + "\t" + refseq_id + "\t" + refseq_nsid + "\n";
    		else                                    line = nxac + " "  + refseq_id + " "  + refseq_nsid + "\n";
    		writer.write(line);
    	}
    }

    public void close() {
        writer.close();
    }

    

}
