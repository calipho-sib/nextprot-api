package org.nextprot.api.core.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nextprot.api.commons.exception.ResourceNotFoundException;
import org.nextprot.api.core.dao.FunctionPredictionDAO;
import org.nextprot.api.core.domain.AggregateFunctionPrediction;
import org.nextprot.api.core.domain.CvTerm;
import org.nextprot.api.core.service.FunctionPredictionService;
import org.nextprot.api.core.service.TerminologyService;
import org.nextprot.api.core.utils.PeptideUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class FunctionPredictionServiceImpl implements FunctionPredictionService {

    private static final Log LOGGER = LogFactory.getLog(FunctionPredictionService.class);

    @Autowired
    FunctionPredictionDAO functionPredictionDAO;

    @Autowired
    private TerminologyService terminologyService;

    private List<AggregateFunctionPrediction> aggregateFunctionPredictions;

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
}