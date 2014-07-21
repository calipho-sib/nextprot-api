package org.nextprot.api.domain.rdf;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.jsondoc.core.annotation.ApiObject;

@ApiObject(name = "Evidence", description = "Meta description of an evidence")
public class OWLEvidence implements Serializable{

	private static final long serialVersionUID = 4404147147281845675L;
	

	//
	// all evidences codes
	final String[] disjoin={"EXP","IDA","IPI","IGI","IEP","ISS","ISO","ISA","ISM","IGC",
							 "RCA","TAS","NAS","IC","ND","IEA","NR","IMP","IBA","IBD","IKR","IRD"};
	
	//
	// equivalence for UNKNOWN, PROBABLE, POTENTIAL, BY SIMILARITY
	// map uniprot evidence code with ECO
	final static Map<String, String> evidenceType = new HashMap<String, String>();
	final static Map<String, String> evidenceInfo = new HashMap<String, String>();
	private String type;
	
	private String description;
	private int count;

	public OWLEvidence() {
		//
		// map uniprot evidence code with ECO
		evidenceType.put("UNKNOWN", "EXP");
		evidenceType.put("PROBABLE", "IC");
		evidenceType.put("POTENTIAL", "IEA");
		evidenceType.put("BY_SIMILARITY", "ISS");

		evidenceInfo.put("UNKNOWN", "Inferred from Experiment");
		evidenceInfo.put("PROBABLE", "Inferred by Curator");
		evidenceInfo.put("POTENTIAL", "Inferred from Electronic Annotation");
		evidenceInfo.put("BY_SIMILARITY", "Inferred from Sequence or Structural Similarity");

	}

	public String getType() {
		if (evidenceType.containsKey(type))
			return evidenceType.get(type);
		return type;		
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDescription() {
		if (evidenceInfo.containsKey(type))
			return evidenceInfo.get(type);

		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setCount(int count) {
		this.count=count;
	}

	public int getCount(){
		return count;
	}
	
	public String getDisjointWith(String sep){
		String from=this.getType(), eval;
		StringBuffer d=new StringBuffer();
		for(int i=0;i<disjoin.length;i++){
			if(!disjoin[i].equals(from)){
				eval=(d.length()==0)?"":sep;
				d.append(eval+disjoin[i]);
			}
		}
		return d.toString();
	}
}
