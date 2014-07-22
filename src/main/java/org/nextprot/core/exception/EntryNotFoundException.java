package org.nextprot.core.exception;


public class EntryNotFoundException extends NextProtException {

	private static final long serialVersionUID = -7757144894731083422L;

	public EntryNotFoundException(String s) {
		super("Entry " + s + " not found. Give a valid neXtProt entry as parameter, example: NX_P59103 ");
	}
}