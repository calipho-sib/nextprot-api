package org.nextprot.api.commons.utils;

import com.google.common.math.LongMath;
import org.nextprot.commons.algo.MD5Algo;

import java.math.BigInteger;

/**
 * Build from DbXref, the xref protocol id is encoded on 64 bits and consists on 3 fields:
 *
 * ~3  bits (7) for the xref protocol flag
 * ~10 bits for xref database id
 * ~50 bits for xref accession id (truncated MD5)
 *
 *  Here is the decimal format:
 *  7_DDD_AAA_AAA_AAA_AAA_AAA
 *
 *  7                   (7: xref protocol reserved number)
 *  DDD                 (D: database id [3 digits])
 *  AAA_AAA_AAA_AAA_AAA (A: xref accession id [15 digits])
 *
 *  The class also provides static operations to extract informations encoded into a long such as:
 *  1. the database id
 *  2. the database accession id
 *  3. the xref accession id
 *  a boolean that tells if it is a xref protocol id
 */
public class XRefProtocolId {

    //                                                7_DDD_AAA_AAA_AAA_AAA_AAA
    private final static long STATEMENT_XREF_OFFSET = 7_000_000_000_000_000_000L;
    private final static long STATEMENT_XREF_DATABASE_OFFSET = LongMath.pow(10, 15);

    private final long databaseId;
    private final String accession;
    private final long id;

    public XRefProtocolId(long databaseId, String accession) {

        this.databaseId = databaseId;
        this.accession = accession.trim();
        this.id = STATEMENT_XREF_OFFSET + databaseXrefOffset() + truncatedMD5Accession48bits();
    }

    public long id() {

        return id;
    }

    private long databaseXrefOffset() {

        return databaseId * STATEMENT_XREF_DATABASE_OFFSET;
    }

    private long truncatedMD5Accession48bits() {

        String md5Truncated48bits = MD5Algo.computeMD5(accession).substring(0, 12);
        return new BigInteger(md5Truncated48bits, 16).longValue();
    }

    public static boolean isXrefProtocolId(long statementXrefId) {

        return statementXrefId > STATEMENT_XREF_OFFSET;
    }

    /**
     * Get the database id encoded into this given xref id or -1 if not a statement
     * @param xrefId the xref id
     * @return the database id or -1 if not a statement
     */
    public static long calcXrefDatabaseId(long xrefId) {

        if (isXrefProtocolId(xrefId)) {
            return (xrefId - STATEMENT_XREF_OFFSET) / STATEMENT_XREF_DATABASE_OFFSET;
        }
        return -1;
    }

    /**
     * Get the accession id in the database encoded into this given xref id or -1 if not a statement
     * @param xrefId the xref id
     * @return the accession id or -1 if not a statement
     */
    public static long calcTruncatedXrefAccessionId(long xrefId) {

        if (isXrefProtocolId(xrefId)) {

            return xrefId - (xrefId / STATEMENT_XREF_DATABASE_OFFSET) * STATEMENT_XREF_DATABASE_OFFSET;
        }
        return -1;
    }
}