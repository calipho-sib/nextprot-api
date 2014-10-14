package org.nextprot.api.user.domain;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

//@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties({"resourceOwner"})
public class UserApplication implements Serializable, UserResource {

	private static final long serialVersionUID = -4106316166685442169L;

	private long id;

	//TODO doesn't look like it is working ... (should throw an exception before getting in the databse)
	@JsonProperty(required=true)
	private String name;
	@JsonProperty(required=true)
	private String description;
	private String organisation;
	private String responsibleName;
	private String responsibleEmail;
	private String website;
	private String owner;
    private long ownerId;
    @JsonProperty(required=true)
    private String token;
	private String status;
    private String userDataAccess;
    private String origins;
    //private Date creationDate;

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getResponsibleName() {
		return responsibleName;
	}
	public void setResponsibleName(String responsibleName) {
		this.responsibleName = responsibleName;
	}
	public String getResponsibleEmail() {
		return responsibleEmail;
	}
	public void setResponsibleEmail(String responsibleEmail) {
		this.responsibleEmail = responsibleEmail;
	}
	public String getWebsite() {
		return website;
	}
	public void setWebsite(String website) {
		this.website = website;
	}
    public long getOwnerId() { return ownerId; }
    public void setOwnerId(long ownerId) { this.ownerId = ownerId; }
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public String getOrganisation() {
        return organisation;
    }
    public void setOrganisation(String organisation) {
        this.organisation = organisation;
    }
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public String getUserDataAccess() {
        return userDataAccess;
    }
    public void setUserDataAccess(String userDataAccess) {
        this.userDataAccess = userDataAccess;
    }
    public String getOrigins() {
        return origins;
    }
    public void setOrigins(String origins) {
        this.origins = origins;
    }
    //public Date getCreationDate() { return creationDate; }

    @Override
	public String getResourceOwner() {
		return owner;
	}
	public String getOwner() {
		return owner;
	}
    public void setOwner(String owner) { this.owner = owner; }
}
