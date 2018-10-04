package org.nextprot.api.core.domain;

import java.io.Serializable;

public class Family implements Serializable{
	
	private static final long serialVersionUID = -2044466405961942191L;

	private Long familyId;
	private String accession;
	private String name;
	private String level;
	private String description;
	private String region;
	private Family parent;
		
	public Long getFamilyId() {
		return familyId;
	}

	public void setFamilyId(Long termId) {
		this.familyId = termId;
	}

	public Family getParent() {
		return parent;
	}

	public void setParent(Family parent) {
		this.parent = parent;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public String getAccession() {
		return accession;
	}
	
	public void setAccession(String accession) {
		this.accession = accession;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {

		if (name.endsWith(" family")) {
			this.level = "Family";           	// 952 cases
			this.name = name.substring(0,name.length() - 7);
		}
		else if (name.endsWith(" superfamily")) {
			this.level = "Superfamily";			// 304 cases
			this.name = name.substring(0,name.length() - 12);
		}
		else if (name.endsWith(" subfamily")) {
			this.level = "Subfamily";			// 143 cases
			this.name = name.substring(0,name.length() - 10);
		}
		else if (name.endsWith(" sub-subfamily")) {
			this.level = "Subsubfamily";
			this.name = name.substring(0,name.length() - 14);
		}
		else {
			System.out.println("ERROR: cannot determine level for family name <" + name + ">");
			this.level = "";					//  0 case, but who knows...
			this.name = name;
		}

	}
	
	public String getLevel() {
		return level;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("id       : " + this.getFamilyId() + "\n");
		sb.append("ac       : " + this.getAccession() + "\n");
		sb.append("level    : " + this.getLevel() + "\n");
		sb.append("name     : '" + this.getName() + "'\n");
		sb.append("descr    : " + this.getDescription() + "\n");
		sb.append("region   : " + this.getRegion() + "\n");
		sb.append("parentId : " + (this.getParent()==null ? null : this.getParent().getFamilyId()) + "\n");
		return sb.toString();
	}
	
}
