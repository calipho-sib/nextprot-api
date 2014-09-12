package org.nextprot.api.user.domain;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(Include.NON_NULL)
//@JsonIgnoreProperties({"foobar"})
public class UserApplication implements Serializable, UserResource {

	private static final long serialVersionUID = -4106316166685442169L;

	private long id;

	@JsonProperty(required=true)
	private String name;
	
	@JsonProperty(required=true)
	private String description;

	
	private String organisation;
	
	@JsonProperty(required=true)
	private String responsibleName;

	@JsonProperty(required=true)
	private String responsibleEmail;

	
	private String website;

	@JsonProperty(required=true)
	private String owner;

	private String grants;
	private String token;
	private Date tokenValidity;
	
	public UserApplication(){
		
	}

	public UserApplication(String s, int i, int i1){
		this.name = "lol";
	}

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
	public String getGrants() {
		return grants;
	}
	public void setGrants(String grants) {
		this.grants = grants;
	}
	
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public Date getTokenValidity() {
		return tokenValidity;
	}
	public void setTokenValidity(Date tokenValidity) {
		this.tokenValidity = tokenValidity;
	}

	@Override
	public String getResourceOwner() {
		return owner;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

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

}
