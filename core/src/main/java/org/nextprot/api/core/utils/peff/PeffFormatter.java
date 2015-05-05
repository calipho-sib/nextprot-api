package org.nextprot.api.core.utils.peff;

/**
 * PEFF is an enriched FASTA format specified by the HUPO PSI (PubMed:19132688) that allows mass spectrometry
 * search engines and other tools to easily access data essential to an optimal protein identification, namely
 * sequence variations and PTMs.
 *
 * Created by fnikitin on 05/05/15.
 */
public interface PeffFormatter {

    String asPeff();
}
