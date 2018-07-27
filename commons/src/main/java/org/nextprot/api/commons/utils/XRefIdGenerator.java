package org.nextprot.api.commons.utils;

import org.nextprot.api.commons.constants.IdentifierOffset;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.commons.algo.MD5Algo;

import java.math.BigInteger;
import java.util.Map;

public class XRefIdGenerator {

    private String database;
    private String accession;

    public XRefIdGenerator(String database, String accession){
        this.database = database;
        this.accession = accession;
    }

    private long computeAccessionDecimalHash(String ac){
        String md5Truncated = MD5Algo.computeMD5(ac).substring(0, 10);
        return new BigInteger(md5Truncated, 16).longValue();
    }

    private static long power(long number, int base, int exponent){
        if(exponent == 0){ return number; }
        else { return power(number * base, base, exponent -1); }
    }

    private static long powerDB(long cv_id){
        return power(cv_id, 10, 14);
    }

    /**
     * Builds a long, based on the following format:
     *
     * 7_0_999_0_9_999_999_999_999L
     * 7 (chosen number) followed by delimiter 0
     * 999 (corresponds to the database id) followed by delimiter 0
     * 9_999_999_999_999 (13 digits that corresponds to a the first 10 chars of the MD5 hash)
     *
     * @return the xref id
     */
    public long build(Map<String, Integer> dictionary){

        if(dictionary != null && dictionary.containsKey(this.database)){
            return IdentifierOffset.NX_FLAT_XREFS_OFFSET + //7_0_000_0_0_000_000_000_000L
                    powerDB(dictionary.get(this.database)) +
                    computeAccessionDecimalHash(this.accession);
        }else {
            throw new NextProtException("Could not find id related to " + this.database);
        }
    }

}