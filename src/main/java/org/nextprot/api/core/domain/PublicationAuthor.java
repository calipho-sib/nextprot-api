package org.nextprot.api.core.domain;

import java.io.Serializable;




public class PublicationAuthor implements Comparable<PublicationAuthor>, Serializable {

	private static final long serialVersionUID = -3967004517863213171L;
	private Long authorId;
	private String lastName;
	private String foreName;
	private String suffix="";
	private Integer rank;
	private boolean person;
	
	private Long publicationId;
	
	public String toString(){
		return String.format("{lastName=%s,foreName=%s,suffix=%s,rank=%d,person=%b}", lastName,foreName,suffix,rank,person);
	}
	
	public Long getAuthorId() {
		return authorId;
	}
	
	public void setAuthorId(Long authorId) {
		this.authorId = authorId;
	}
	
	public String getLastName() {
		return lastName;
	}
	
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	public String getForeName() {
		return foreName;
	}
	
	public void setForeName(String foreName) {
		this.foreName = foreName;
	}
	

	public String getSuffix() {
		return suffix;
	}

	public void setSuffix(String suffix) {
		this.suffix =suffix;
	}

	public Integer getRank() {
		return rank;
	}

	public void setRank(Integer rank) {
		this.rank = rank;
	}

	public Long getPublicationId() {
		return publicationId;
	}

	public void setPublicationId(Long publicationId) {
		this.publicationId = publicationId;
	}

	@Override
	public int compareTo(PublicationAuthor o) {
		if(! getRank().equals(o.getRank()))
			return getRank().compareTo(o.getRank());
		else return getLastName().compareTo(o.getLastName());
	}

	public boolean isPerson() {
		return person;
	}

	public void setPerson(boolean person) {
		this.person = person;
	}
	
	
}
