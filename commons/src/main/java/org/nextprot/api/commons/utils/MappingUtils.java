package org.nextprot.api.commons.utils;

import com.google.common.base.Preconditions;

public class MappingUtils {
	

	
	public static String getExonCodingStatus(final int startPositionIsoform, final int endPositionIsoform, final int startPositionExon, final int endPositionExon){
		
		Preconditions.checkArgument(!(startPositionIsoform > endPositionIsoform), "The start position of the isoform on the gene " + startPositionIsoform + " can not be bigger than the end " + endPositionIsoform);
		Preconditions.checkArgument(!(startPositionExon > endPositionExon), "The start position of the exon on the gene " + startPositionIsoform + " can not be bigger than the end " + endPositionIsoform);

		String codingStatus = null;
		
		// Case where only one exon can translate the whole isoform
		if (((startPositionExon - endPositionIsoform) < 3)  &&
				((startPositionExon - endPositionIsoform) > 0)  && 
				(startPositionExon >= endPositionIsoform) && 
				(endPositionExon >= endPositionIsoform) ) {
			codingStatus = "STOP_ONLY";
			// ************ SPI ******************* EPI *******************
			// **************************************SPE************<EPE>**
		}

		
		// not coding exons in the beginning of the transcript
		else if (endPositionExon < startPositionIsoform) {
			codingStatus = "NOT_CODING";
			// ************ SPI ******************* EPI *******************
			// **<SPE>***EPE***********************************************
		}

		// end codon
		else if (startPositionExon > endPositionIsoform) {
			codingStatus = "NOT_CODING";
			// ************ SPI ******************* EPI *******************
			// ********************************************SPE*<EPE>*******
		}

		// start codon
		else if ((startPositionExon <= startPositionIsoform) && (endPositionExon > startPositionExon) && (endPositionExon < endPositionIsoform)) {
			codingStatus = "START";
			// ************ SPI ******************* EPI *******************
			// *******SPE**********<EPE>***********************************
		}

		// end codon
		else if ((endPositionExon >= endPositionIsoform) && (startPositionExon > startPositionIsoform) && (startPositionExon < endPositionIsoform)) {
			codingStatus = "STOP";
			// ************ SPI ******************* EPI *******************
			// *********************<SPE>******************EPE*************
		}

		// Case where only one exon can translate the whole isoform
		else if ((startPositionExon <= startPositionIsoform) && (endPositionExon >= endPositionIsoform)) {
			codingStatus = "MONO";
			// ************ SPI ******************* EPI *******************
			// *************SPE**********************************EPE*******
		} else {
			
			// In the last case it must be a coding exon
			codingStatus = "CODING";
		}
		
		return codingStatus;
	}

}
