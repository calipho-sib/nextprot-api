package org.nextprot.api.user.domain;

import org.nextprot.api.commons.resource.UserResource;

import java.util.HashSet;
import java.util.Set;

public class UserProteinList implements UserResource {

	private static final long serialVersionUID = 1968815880984849468L;

	private long id;
	private String name;
	private String description;
	private Set<String> accessionNumbers = new HashSet<String>();
	private int entriesCount = 0;
    private long ownerId;
	private String owner;

	public long getId() {
		return this.id;
	}
	
	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public Set<String> getAccessionNumbers() {
		return accessionNumbers;
	}
	
	public void setId(long id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setAccessions(Set<String> accessions) {
		this.accessionNumbers = accessions;
		this.entriesCount = this.accessionNumbers.size();
	}
	
	public int getEntriesCount() {
		return entriesCount;
	}

	public void setEntriesCount(int count) {
		entriesCount = count;
	}

	@Override
	public void setOwnerName(String name) { this.owner = name; }

	@Override
    public String getOwnerName() {
        return owner;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) { this.owner = owner; }

    public long getOwnerId() { return ownerId; }

	public void setOwnerId(long ownerId) { this.ownerId = ownerId; }

	public void addAccessions(Set<String> acs) {
		accessionNumbers.addAll(acs);
	}
	
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("name : " );
		sb.append(this.name);
		sb.append(" (" );
		sb.append(this.id);
		sb.append(") accessions size:" );
		sb.append(entriesCount);
		return sb.toString();
	}
}
