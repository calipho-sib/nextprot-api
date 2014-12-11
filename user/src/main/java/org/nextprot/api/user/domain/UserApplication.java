package org.nextprot.api.user.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.nextprot.api.commons.resource.UserResource;

import java.sql.Date;

//@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties({"resourceOwner"})
public class UserApplication implements UserResource {

	private static final String USER_APP_RO = "RO";
	private static final String USER_APP_RW = "RW";
	
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
	private String status = "ACTIVE"; // "ACTIVE", "BANNED";
    private String userDataAccess = USER_APP_RO; // "RO", "RW"
    private String origins;
    private Date creationDate;

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

	@Override
	public boolean isPersisted() {
		return id != 0;
	}

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
    public boolean hasUserDataAccess() {
        return USER_APP_RW.equals(userDataAccess); //TODO use enum or boolean ???
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
    public void setCreationDate(Date date) {
        this.creationDate = date;
    }
    public Date getCreationDate() { return creationDate; }

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
}
