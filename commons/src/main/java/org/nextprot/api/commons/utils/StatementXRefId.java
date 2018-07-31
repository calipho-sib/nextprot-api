package org.nextprot.api.commons.utils;

import com.google.common.math.LongMath;
import org.nextprot.api.commons.constants.IdentifierOffset;
import org.nextprot.commons.algo.MD5Algo;

import java.math.BigInteger;

public class StatementXRefId {

    private final static long STATEMENT_XREF_DATABASE_OFFSET = LongMath.pow(10, 14);

    private final long databaseId;
    private final String accession;

    public StatementXRefId(long databaseId, String accession) {

        this.databaseId= databaseId;
        this.accession = accession;
    }

    private long databaseXrefOffset() {

        return databaseId * STATEMENT_XREF_DATABASE_OFFSET;
    }

    private long truncatedMD5Accession() {

        String md5Truncated = MD5Algo.computeMD5(accession).substring(0, 10);
        return new BigInteger(md5Truncated, 16).longValue();
    }

    /**
     * Builds a long, based on the following format:
     *
     * 7_0_DDD_0_A_AAA_AAA_AAA_AAA
     *
     * 7                 (nxflat statement reserved number)
     * DDD               (database id)
     * A_AAA_AAA_AAA_AAA (accession id)
     *
     * @return the xref id
     */
    public long id() {

        return IdentifierOffset.STATEMENT_XREF_OFFSET + databaseXrefOffset() + truncatedMD5Accession();
    }

    public static long calcXrefDatabaseId(long statementXrefId) {

        if (isStatementXrefId(statementXrefId)) {
            return (statementXrefId - IdentifierOffset.STATEMENT_XREF_OFFSET) / STATEMENT_XREF_DATABASE_OFFSET;
        }
        return -1;
    }

    public static boolean isStatementXrefId(long statementXrefId) {

        return statementXrefId > IdentifierOffset.STATEMENT_XREF_OFFSET;
    }
}