package org.nextprot.api.core.service.annotation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nextprot.api.commons.bio.AminoAcidCode;
import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.core.domain.Isoform;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.domain.annotation.AnnotationIsoformSpecificity;
import org.nextprot.api.core.domain.annotation.AnnotationProperty;

public class VariantUtils {

	public static boolean isSimpleSubstitution(String originalAa, String mutatedAa) {
		// we expect one letter code in there
		if (originalAa.length()!=1) return false;
		if (mutatedAa.length()!=1) return false;
		return true;
	}
	
	public static String getRefSeqIdentifier(Isoform isoform, String geneName) {
		return isoform.getIsoformAccession();
	}
	
	public static void updateHGVSName(Annotation varAnnot, Isoform isoform, String geneName ) {
		
		// this method is for variants and mutagenesis only otherwise exit
		if (varAnnot.getAPICategory()!=AnnotationCategory.VARIANT && 
			varAnnot.getAPICategory()!=AnnotationCategory.MUTAGENESIS) {
			return;
		}
		
		// if the variant does not exist on the isoform, exit
		String isoformAc = isoform.getIsoformAccession();
		if (! varAnnot.getTargetingIsoformsMap().containsKey(isoformAc)) return;
		
		// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
		// handle simple substitutions
		// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
		String oriAa = varAnnot.getVariant().getOriginal();
		String mutAa = varAnnot.getVariant().getVariant();
		if (isSimpleSubstitution(oriAa, mutAa)) {
			StringBuilder sb = new StringBuilder();
			sb.append(getRefSeqIdentifier(isoform, geneName));
			AnnotationIsoformSpecificity isospec = varAnnot.getTargetingIsoformsMap().get(isoformAc);
			sb.append(":p.");
			// just turn amino acid 1 letter code to 3 letter code
			sb.append(AminoAcidCode.valueOfAminoAcid(oriAa).get3LetterCode());
			sb.append(isospec.getFirstPosition().toString());
			sb.append(AminoAcidCode.valueOfAminoAcid(mutAa).get3LetterCode());
			isospec.setHgvs(sb.toString());
		}
		// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
		// other cases not handled yet 
		// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
		// ...		
	}
	

}
