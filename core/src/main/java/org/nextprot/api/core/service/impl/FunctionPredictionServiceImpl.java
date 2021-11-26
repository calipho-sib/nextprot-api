package org.nextprot.api.core.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nextprot.api.commons.exception.ResourceNotFoundException;
import org.nextprot.api.core.dao.FunctionPredictionDAO;
import org.nextprot.api.core.domain.AggregateFunctionPrediction;
import org.nextprot.api.core.domain.CvTerm;
import org.nextprot.api.core.domain.FunctionPrediction;
import org.nextprot.api.core.service.FunctionPredictionService;
import org.nextprot.api.core.service.MasterIdentifierService;
import org.nextprot.api.core.service.TerminologyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class FunctionPredictionServiceImpl implements FunctionPredictionService {

    private static final Log LOGGER = LogFactory.getLog(FunctionPredictionService.class);

    @Autowired
    FunctionPredictionDAO functionPredictionDAO;
    @Autowired
    private TerminologyService terminologyService;
    @Autowired
    private MasterIdentifierService masterIdentifierService;

    /**
     * Returns the list of function predictions
     * @return List of function predictions
     */
    public AggregateFunctionPrediction getFunctionPredictions(String entryAccession) {

        AggregateFunctionPrediction aggregateFunctionPrediction = new AggregateFunctionPrediction(entryAccession);

        // Loads the predictions via DAO layer
        functionPredictionDAO.getPredictions(entryAccession)
                .forEach(functionPrediction -> {
                    // Populate the names from terminology service
                    String cvTermAccession = functionPrediction.getCvTermAccession();

                    try {
                        CvTerm cvTerm = terminologyService.findCvTermByAccessionOrThrowRuntimeException(cvTermAccession);
                        functionPrediction.setType(cvTerm.getOntologyAltname());
                        functionPrediction.setCvTermName(cvTerm.getName());
                        functionPrediction.setCvTermDescription(cvTerm.getDescription());

                        //Evidences
                        functionPrediction.getEvidences()
                                .forEach(predictionEvidence -> {
                                    String ecoCodeAccession = predictionEvidence.getEvidenceCodeAC();
                                    CvTerm ecoCVTerm = terminologyService.findCvTermByAccessionOrThrowRuntimeException(ecoCodeAccession);
                                    predictionEvidence.setEvidenceCodeName(ecoCVTerm.getName());
                                });
                    } catch(ResourceNotFoundException e) {
                        // No cv term found
                        LOGGER.error("CV Term " + cvTermAccession + " is not valid");
                    }

                    aggregateFunctionPrediction.addPrediction(functionPrediction);
                });

        return aggregateFunctionPrediction;
    }
    
    
    
    public Map<String,List<String>> getInvalidPredictions() {

    	List<FunctionPrediction> fpList = functionPredictionDAO.getAllPredictions();
    	Set<String> validEntrySet = masterIdentifierService.findUniqueNames();
    	
    	Set<String> invalidEntries = new HashSet<>();
    	Set<String> invalidECOTerms = new HashSet<>();
    	Set<String> invalidGOTerms = new HashSet<>();
    	
    	for (FunctionPrediction fp: fpList) {
    		// check entry accession is valid
    		String entryAc = fp.getEntryAC();
    		if (! validEntrySet.contains(entryAc)) invalidEntries.add(entryAc);
    		// check GO term is valid / known
    		String goTermAc = fp.getCvTermAccession();
            try { 
            	terminologyService.findCvTermByAccessionOrThrowRuntimeException(goTermAc); 
            } catch (ResourceNotFoundException e) { 
            	invalidGOTerms.add(goTermAc);
            }
            // check ECO term is valid / known
    		String ecoTermAc = fp.getEvidences().get(0).getEvidenceCodeAC();
            try { 
            	terminologyService.findCvTermByAccessionOrThrowRuntimeException(ecoTermAc); 
            } catch (ResourceNotFoundException e) { 
            	invalidECOTerms.add(ecoTermAc);
            }
    	}
    	Map<String,List<String>> result = new HashMap<>();
    	result.put("invalidEntries", new ArrayList<String>(invalidEntries));
    	result.put("invalidECOTerms", new ArrayList<String>(invalidECOTerms));
    	result.put("invalidGOTerms", new ArrayList<String>(invalidGOTerms));
    	return result;
    	    	
    }

}