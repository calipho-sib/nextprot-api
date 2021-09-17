package org.nextprot.api.core.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.codec.digest.DigestUtils;
import org.nextprot.api.core.domain.ExperimentalContext;

public class ExperimentalContextUtil {
	
	//pam:ec
	public static List<ExperimentalContext> filterExperimentalContextsByIds(List<ExperimentalContext> expContexts, Set<Long> ids){
		List<ExperimentalContext> ecsFiltered = new ArrayList<ExperimentalContext>();
		for(ExperimentalContext ec : expContexts){
			if(ids.contains(ec.getContextId())){
				ecsFiltered.add(ec);
			}
		}
		return ecsFiltered;
	}

	
	public static String computeMd5ForBgee(String tissueAc, String stageAc, String detectionMethodAc) {
		return computeMd5(tissueAc, stageAc, null, null, null, detectionMethodAc, "MDATA_0038");
	}
	
	public static String computeMd5ForHPA(String tissueAc, String detectionMethodAc) {
		return computeMd5(tissueAc, null, null, null, null, detectionMethodAc, "MDATA_0005");
	}
	
	public static String computeMd5ForCosmic(
			String tissueAc, String cellLineAc, String diseaseAc, String detectionMethodAc) {
		return computeMd5(tissueAc, null, cellLineAc, diseaseAc, null, detectionMethodAc, "MDATA_0040");
	}
	
	public static String computeMd5ForBioeditorVAs(String tissueAc, String cellLineAc, String detectionMethodAc) {
		return computeMd5(tissueAc, null, cellLineAc, null, null, detectionMethodAc, "MDATA_0407");
	}
	
	public static String computeMd5ForCellosaurus(String diseaseAc, String cellLineAc, String detectionMethodAc) {
		return computeMd5(null, null, cellLineAc, diseaseAc, null, detectionMethodAc, "MDATA_0408");
	}
	
	/*
	 * pam, 16 Oct 2020
	 * md5 is a column in the NP1 experimental_contexts table
	 * It is not clear how it is used but it is a mandatory field
	 * The computation of md5 is a copy of how it is computed in 
	 * NP1 nextprot-loaders code
    */
	

    private static String computeMd5 (
    		String tissueAc, String stageAc, String cellLineAc, String diseaseAc, 
    		String organelleAc, String detectionMethodAc, String mdataAc) {
        
    	StringBuilder text = new StringBuilder();

        if (tissueAc != null) text.append(tissueAc);            
        if (stageAc != null) text.append(stageAc);            
        if (cellLineAc != null) text.append(cellLineAc);            
        if (diseaseAc != null) text.append(diseaseAc);            
        if (organelleAc != null) text.append(organelleAc);            
        if (detectionMethodAc != null) text.append(detectionMethodAc);            
        if (mdataAc != null) text.append(mdataAc);            
        /*
        System.out.println("context string:<"+text.toString() + ">");
        String finalStr = text.toString().toLowerCase().replaceAll("\\W", "");
        System.out.println("final   string:<"+finalStr + ">");
        */
        return DigestUtils.md5Hex(text.toString().toLowerCase().replaceAll("\\W", ""));
    }


}
