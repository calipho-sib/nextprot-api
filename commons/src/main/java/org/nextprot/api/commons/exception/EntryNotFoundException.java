package org.nextprot.api.commons.exception;


public class EntryNotFoundException extends NextProtException {

	private static final long serialVersionUID = -7757144894731083422L;

	private final String entry;

	public EntryNotFoundException(String entry) {

		this("", entry);
	}

	public EntryNotFoundException(String contextMessage, String entry) {

		super(contextMessage+"Entry " + entry + " not found. Give a valid neXtProt entry as parameter, example: NX_P59103.");

		this.entry = entry;
	}

	public String getEntry() {

		return entry;
	}
}