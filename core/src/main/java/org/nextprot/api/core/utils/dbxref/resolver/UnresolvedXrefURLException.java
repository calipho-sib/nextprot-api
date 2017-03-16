package org.nextprot.api.core.utils.dbxref.resolver;


import org.nextprot.api.commons.exception.NextProtException;

/**
 * Exception thrown when DbXref URL cannot be resolved
 */
public class UnresolvedXrefURLException extends NextProtException {

    public UnresolvedXrefURLException(String string) {
        super(string);
    }

    public UnresolvedXrefURLException(String string, Exception e) {
        super(string, e);
    }
}
