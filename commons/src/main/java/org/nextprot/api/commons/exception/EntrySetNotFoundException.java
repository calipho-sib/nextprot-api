package org.nextprot.api.commons.exception;


import java.util.Set;

public class EntrySetNotFoundException extends NextProtException {

	private static final long serialVersionUID = 1L;

	private final Set<String> entries;

	public EntrySetNotFoundException(Set<String> entries) {

		this("", entries);
	}

	public EntrySetNotFoundException(String contextMessage, Set<String> entries) {

		super(contextMessage+"Entries " + entries + " not found. Give valid neXtProt entries, example: NX_P59103.");

		this.entries = entries;
	}

	public Set<String> getEntrySet() {

		return entries;
	}
}