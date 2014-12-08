package org.nextprot.api.user.domain;

import java.util.HashSet;
import java.util.Set;

import org.nextprot.api.commons.resource.ResourceOwner;

public class UserProteinList implements ResourceOwner {

	private static final long serialVersionUID = 1968815880984849468L;

	private long id;
	private String name;
	private String description;
	private Set<String> accessionNumbers = new HashSet<String>();
	private int proteinCount = 0;
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
		this.proteinCount = this.accessionNumbers.size();
	}
	
	public int getProteinCount() {
		return proteinCount;
	}

	public void setProteinCount(int count) {
		proteinCount = count;
	}

    @Override
    public String getResourceOwner() {
        return owner;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) { this.owner = owner; }

    public long getOwnerId() { return ownerId; }

    public void setOwnerId(long ownerId) { this.ownerId = ownerId; }
}
