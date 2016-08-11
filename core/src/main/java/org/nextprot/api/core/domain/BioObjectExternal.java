package org.nextprot.api.core.domain;

/**
 * A biological domain object external to nextProt
 *
 * Created by fnikitin on 26/08/15.
 */
public class BioObjectExternal extends BioObject<DbXref> {

    private static final long serialVersionUID = 1L;

    public BioObjectExternal(BioType bioType, String database) {

        super(bioType, ResourceType.EXTERNAL, database);
    }

	@Override
	protected String toBioObjectString() {
		StringBuilder sb = new StringBuilder();
		sb.append("BioObjectExternal");
		return sb.toString();

	}
}
