package org.nextprot.api.core.domain.annotation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class AnnotationVariant implements Serializable {

	private static final long serialVersionUID = -2952120392107319047L;
	private String original;
	private String variant;
	private String rawDescription;
	private String description = null; // set by parsing rawDescription
	private List<String> diseaseTerms = null; // set by parsing rawDescription

	// TODO: [original|variantAminoAcid never used] ; discuss about the usage of originalAminoAcid and variantAminoAcid as they can be empty, single or multiple
	/*
	public String getOriginalAminoAcid() {
		return originalAminoAcid;
	}
	public void setOriginalAminoAcid(String originalAminoAcid) {
		this.originalAminoAcid = originalAminoAcid;
	}
	public String getVariantAminoAcid() {
		return variantAminoAcid;
	}
	public void setVariantAminoAcid(String variantAminoAcid) {
		this.variantAminoAcid = variantAminoAcid;
	}

	private String originalAminoAcid;
	private String variantAminoAcid;
	*/

	@Deprecated //TODO See with Fred AminoAcidCode which can be null, -, multiple ...
	public AnnotationVariant(String original, String variant) {
		this.original = original;
		this.variant = variant;
	}

	public AnnotationVariant(String original, String variant, String rawDescription) {
		super();
		this.original = original;
		this.variant = variant;
		// Commenting code that is never used: see [original|variantAminoAcid never used] comment above
		//this.originalAminoAcid = AminoAcidCode.valueOfOneLetterCode(original.charAt(0)).get3LetterCode();
		//this.variantAminoAcid = AminoAcidCode.valueOfOneLetterCode(variant.charAt(0)).get3LetterCode();
		this.rawDescription = rawDescription;
		parseRawDescription();
	}

	public String getOriginal() {
		return original;
	}
	public void setOriginal(String original) {
		this.original = original;
	}
	public String getVariant() {
		return variant;
	}
	public void setVariant(String variant) {
		this.variant = variant;
	}

	public void setRawDescription(String rawDescription) {
		this.rawDescription=rawDescription;
	}
	
	public String getDescription() {
		return this.description;
	}
	public List<String> getDiseaseTerms() {
		return this.diseaseTerms;
	}
	
	
	private void addDiseaseTerm(String term) {
		if (diseaseTerms==null) diseaseTerms=new ArrayList<String>();
		diseaseTerms.add(term);
	}
	
	
	/*
	 * turns the raw description of a variant into a list of disease terms and a description
	 * 
	 */
	private void parseRawDescription() {
		final String pattern = "UNIPROT_DISEASE:";
		if (rawDescription==null) return;
		String text = rawDescription;
		int idx=0;
		while(true) {
			int pos = text.indexOf(pattern,idx);
			if (pos<0) break;
			int p1 = pos + pattern.length();
			addDiseaseTerm(text.substring(p1,p1+8));
			idx=p1+8;
		}
		if (idx==0) {
			description=text.trim();
		} else {
			idx = text.indexOf("]",idx);
			text=text.substring(idx+1); 
			// we expect <.> or <;> after last <]> in raw description 
			if (text.startsWith(".")) text=text.substring(1); 
			if (text.startsWith(";")) text=text.substring(1); 
			text=text.trim(); // remove trimming spaces
			if (text.length()==0) text=null;
			description=text;
		}
	}
	
	
}
