package org.nextprot.api.core.domain;

import org.jsondoc.core.annotation.ApiObject;
import org.jsondoc.core.annotation.ApiObjectField;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApiObject(name = "antibody", description = "The antibody ")
public class AntibodyMapping implements Serializable, IsoformSpecific {

	private static final long serialVersionUID = -2942379774117119626L;

	@ApiObjectField(description = "The antibody unique name")
	private String antibodyUniqueName;

	@ApiObjectField(description = "The list of cross references")
	private List<DbXref> xrefs;

	@ApiObjectField(description = "Information about the isoform specificity")
	private Map<String, IsoformSpecificity> isoformSpecificity = new HashMap<String, IsoformSpecificity>();

	//private List<IsoformSpecificity> isoformSpecificity;

	private Long xrefId;
	private String assignedBy;
	
	
	public String getAssignedBy() {
		return assignedBy;
	}

	public void setAssignedBy(String assignedBy) {
		this.assignedBy = assignedBy;
	}

	public void setXrefId(Long id) {
		this.xrefId=id;
	}
	
	public Long getXrefId() {
		return this.xrefId;
	}
	
	public String getAntibodyUniqueName() {
		return antibodyUniqueName;
	}

	public void setAntibodyUniqueName(String uniqueName) {
		this.antibodyUniqueName = uniqueName;
	}

	public List<DbXref> getXrefs() {
		return xrefs;
	}

	public void setXrefs(List<DbXref> xrefs) {
		this.xrefs = xrefs;
	}

	/*
	public List<IsoformSpecificity> getIsoformSpecificity() {
		return isoformSpecificity;
	}

	public void setIsoformSpecificity(List<IsoformSpecificity> isoformSpecificity) {
		this.isoformSpecificity = isoformSpecificity;
	}

	public void addIsoformSpecificityOld(IsoformSpecificity isoformSpecificity) {
		if(this.isoformSpecificity == null)
			this.isoformSpecificity = new ArrayList<IsoformSpecificity>();
		this.isoformSpecificity.add(isoformSpecificity);
	}
	public boolean isSpecificForIsoform(String isoformName) {
		for (IsoformSpecificity isospec: this.isoformSpecificity) {
			if (isospec.getIsoformName().equals(isoformName)) return true;
		}
		return false;
	}
	*/
	
	public Map<String, IsoformSpecificity> getIsoformSpecificity() {
		return this.isoformSpecificity;
	}

	public void setIsoformSpecificity(
			Map<String, IsoformSpecificity> isoformSpecificity) {
		this.isoformSpecificity = isoformSpecificity;
	}

	/** 
	 * The DAO object temporarily fills one isoform specificity by antibody * isoform * map position
	 * So we have one and only one isoform specificity in the original domain object at step 1
	 * The isoform specificities are then grouped by antibody 
	 * and the mapping positions grouped by isoform specificity at step 2
	 * @return
	 */
	public IsoformSpecificity getFirstIsoformSpecificity() {
		if (isoformSpecificity.entrySet().size()==0) return null;
		return isoformSpecificity.entrySet().iterator().next().getValue();
	}
	
	public void addIsoformSpecificity(IsoformSpecificity newIsoformSpecificity) {
		String isoName = newIsoformSpecificity.getIsoformName();
		//System.out.println("adding specificity:" + newIsoformSpecificity);
		//for (IsoformSpecificity isp: this.isoformSpecificity.values()) System.out.println("among existing: "+ isp.toString());
		if(this.isoformSpecificity.containsKey(isoName)) { // add position
			//System.out.println("specificity already exists for this isoform");
			IsoformSpecificity isospec = this.isoformSpecificity.get(isoName);
			isospec.addPosition(newIsoformSpecificity.getPositions().get(0));
			//System.out.println("merged specificity: " + isospec.toString());
		} else {
			//System.out.println("specificity does NOT exist for this isoform");
			this.isoformSpecificity.put(isoName, newIsoformSpecificity);
			//System.out.println("new specificity: " + newIsoformSpecificity.toString());
		}
	}
	
	
	/**
	 * 
	 * @param isoformName a nextprot isoform unique name (starting with NX_)
	 * @return true if the mapping applies to the isoform otherwise false
	 */
	@Override
	public boolean isSpecificForIsoform(String isoformName) {
		return this.isoformSpecificity.containsKey(isoformName);
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("AntibodyMapping for " + this.antibodyUniqueName + "\n");
		sb.append("xrefId: " + this.xrefId + "\n");
		for (IsoformSpecificity spec: this.isoformSpecificity.values()) sb.append(spec.toString()+"\n");
		return sb.toString();
	}
	
}
