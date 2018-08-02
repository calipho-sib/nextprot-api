package org.nextprot.api.commons.utils;

import com.google.common.math.LongMath;
import org.nextprot.commons.algo.MD5Algo;

import java.math.BigInteger;

/**
 * A statement xref id is immutable and encoded on 64 bits that consists on 3 fields:
 *
 * ~3  bits (7) for statement flag
 * ~10 bits for xref database id
 * ~50 bits for xref accession id (truncated MD5)
 *
 *  organised on the following format:
 *  7_DDD_AAA_AAA_AAA_AAA_AAA
 *
 *  7                   (7: nxflat statement static reserved number)
 *  DDD                 (D: database id [3 digits])
 *  AAA_AAA_AAA_AAA_AAA (A: xref accession id [15 digits])
 *
 *  The class also provides static operations to extract informations encoded into the long such as:
 *  the database id and a boolean that tells if it is a statement xref id
 */
public class StatementXRefId {

    //                                                7_DDD_AAA_AAA_AAA_AAA_AAA
    private final static long STATEMENT_XREF_OFFSET = 7_000_000_000_000_000_000L;
    private final static long STATEMENT_XREF_DATABASE_OFFSET = LongMath.pow(10, 15);

    private final long databaseId;
    private final String accession;

    public StatementXRefId(long databaseId, String accession) {

        this.databaseId = databaseId;
        this.accession = accession.trim();
    }

    /**
     * Builds the long number based on StatementXRefId mixed protocol
     * @return the xref id
     */
    public long id() {

        return STATEMENT_XREF_OFFSET + databaseXrefOffset() + truncatedMD5Accession48bits();
    }

    private long databaseXrefOffset() {

        return databaseId * STATEMENT_XREF_DATABASE_OFFSET;
    }

    private long truncatedMD5Accession48bits() {

        String md5Truncated48bits = MD5Algo.computeMD5(accession).substring(0, 12);
        return new BigInteger(md5Truncated48bits, 16).longValue();
    }

    /**
     * Get the database id encoded into this given xref id or -1 if not a statement
     * @param xrefId the xref id
     * @return the database id or -1 if not a statement
     */
    public static long getXrefDatabaseId(long xrefId) {

        if (isStatementXrefId(xrefId)) {
            return (xrefId - STATEMENT_XREF_OFFSET) / STATEMENT_XREF_DATABASE_OFFSET;
        }
        return -1;
    }

    public static boolean isStatementXrefId(long statementXrefId) {

        return statementXrefId > STATEMENT_XREF_OFFSET;
    }
}