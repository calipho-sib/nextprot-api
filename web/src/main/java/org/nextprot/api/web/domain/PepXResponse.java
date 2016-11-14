package org.nextprot.api.web.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;



public class PepXResponse {

	
	private Map<String, Object> params = new HashMap<>();
	private List<PepXMatch> peptideMatches = new ArrayList<PepXMatch>();

	
	public Map<String, Object> getParams() {
		return params;
	}
	public void setParams(Map<String, Object> params) {
		this.params = params;
	}
	public List<PepXMatch>  getPeptideMatches() {
		return peptideMatches;
	}
	public void setPeptideMatches(List<PepXMatch> peptideMatches) {
 		this.peptideMatches = peptideMatches;
	}

	public PepXMatch getPeptideMatch(String peptide) {
		for(PepXMatch pepMatch : peptideMatches){
			if(pepMatch.getPeptide().equals(peptide))
				return pepMatch;
		}
		return null;
	}
	
	
	public Set<String> getEntriesNames() {
		Set<String> entryNames = new HashSet<>();
		for(PepXMatch pepXMatch : peptideMatches){
			entryNames.addAll(pepXMatch.getEntryNamesMatches());
		}
		return entryNames;
	}
	
	public Set<String> getPeptidesForEntry(String entryName) {
		Set<String> peptides = new HashSet<>();
		for(PepXMatch pep: peptideMatches){
			if (pep.getEntryNamesMatches().contains(entryName)) {
				peptides.add(pep.getPeptide());
			}
		}
		return peptides;
	}
	
	public static class PepXMatch {

		private String peptide;
		private List<PepXEntryMatch> entryMatches = new ArrayList<PepXEntryMatch>();
		
		public String getPeptide() {
			return peptide;
		}
		public void setPeptide(String peptide) {
			this.peptide = peptide;
		}
		public List<PepXEntryMatch> getEntryMatches() {
			return entryMatches;
		}
		public void setEntryMatches(List<PepXEntryMatch> entryMatches) {
			this.entryMatches = entryMatches;
		}
		public List<String> getEntryNamesMatches() {
			List<String> entryNames = new ArrayList<>();
			for(PepXEntryMatch pepxEntryMatch : entryMatches){
				entryNames.add(pepxEntryMatch.getEntryName());
			}
			return entryNames;
		}

		
		public PepXEntryMatch getPepxMatchesForEntry(String entryName) {
			for(PepXEntryMatch pepxEntryMatch : entryMatches){
				if(pepxEntryMatch.getEntryName().equals(entryName)){
					return pepxEntryMatch;
				}
			}
			return null;
		}

	}

	public static class PepXIsoformMatch {
		
		public PepXIsoformMatch(){};

		public PepXIsoformMatch(String isoformName){
			setIsoformAccession(isoformName);
		};
		
		public PepXIsoformMatch(String isoformName, Integer position){
			setIsoformAccession(isoformName);
			this.position = position;
		};

		private String isoformAc;
		private Integer position = null;

		@Deprecated
		public String getIsoformName() {
			return isoformAc;
		}
		
		public String getIsoformAccession() {
			return isoformAc;
		}
		
		public void setIsoformAccession(String isoformAc) {
			if(!isoformAc.startsWith("NX_")){
				this.isoformAc = "NX_" + isoformAc;
			}else this.isoformAc = isoformAc;
		}
		public Integer getPosition() {
			if(position != null && position == 0) {
				return null; // If position =0 we assume it is null (don't exist)
			}else return position;
		}
		public void setPosition(Integer position) {
			if(position == 0){
				this.position = null;
			}else  this.position = position;
		}
	}

	public static class PepXEntryMatch {

		private String entryName;
		private List<PepXIsoformMatch> isoforms = new ArrayList<PepXIsoformMatch>();

		public String getEntryName() {
			return entryName;
		}
		public void setEntryName(String entryName) {
			this.entryName = entryName;
		}
		public List<PepXIsoformMatch> getIsoforms() {
			return isoforms;
		}
		public void setIsoforms(List<PepXIsoformMatch> isoforms) {
			this.isoforms = isoforms;
		}

	}



}
