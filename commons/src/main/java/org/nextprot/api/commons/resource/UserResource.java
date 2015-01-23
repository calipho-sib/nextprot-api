package org.nextprot.api.commons.resource;


import java.io.Serializable;

public interface UserResource extends Serializable {

	void setOwnerName(String name);
	String getOwnerName();
	void setOwnerId(long id);
	long getOwnerId();
}
