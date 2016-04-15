package org.nextprot.api.web.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class PepXResponse {

	
	private Map<String, Object> params = new HashMap<>();
	private List<PeptideMatch> peptideMatches = new ArrayList<PeptideMatch>();

	
	public Map<String, Object> getParams() {
		return params;
	}
	public void setParams(Map<String, Object> params) {
		this.params = params;
	}
	public List<PeptideMatch>  getPeptideMatches() {
		return peptideMatches;
	}
	public void setPeptideMatches(List<PeptideMatch> peptideMatches) {
 		this.peptideMatches = peptideMatches;
	}

	public PeptideMatch getPeptideMatch(String peptide) {
		for(PeptideMatch pepMatch : peptideMatches){
			if(pepMatch.getPeptide().equals(peptide))
				return pepMatch;
		}
		return null;
	}
	
	public static class PeptideMatch {

		private String peptide;
		private List<Object> entryMatches = new ArrayList<Object>();
		
		public String getPeptide() {
			return peptide;
		}
		public void setPeptide(String peptide) {
			this.peptide = peptide;
		}
		public List<Object> getEntryMatches() {
			return entryMatches;
		}
		public void setEntryMatches(List<Object> entryMatches) {
			this.entryMatches = entryMatches;
		}

	}


}
