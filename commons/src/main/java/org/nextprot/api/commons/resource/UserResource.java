package org.nextprot.api.commons.resource;


import java.io.Serializable;

public interface UserResource extends Serializable {

	public void setOwnerName(String name);
	public String getOwnerName();
	public void setOwnerId(String name);
	public String getOwnerId();
}
