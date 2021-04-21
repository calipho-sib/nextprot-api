package org.nextprot.api.core.service.impl;

import com.sun.xml.internal.rngom.digested.DGroupPattern;
import org.nextprot.api.core.dao.FunctionPredictionDAO;
import org.nextprot.api.core.domain.AggregateFunctionPrediction;
import org.nextprot.api.core.domain.CvTerm;
import org.nextprot.api.core.domain.FunctionPrediction;
import org.nextprot.api.core.service.FunctionPredictionService;
import org.nextprot.api.core.service.TerminologyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class FunctionPredictionServiceImpl implements FunctionPredictionService {

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
                    CvTerm cvTerm = terminologyService.findCvTermByAccessionOrThrowRuntimeException(cvTermAccession);
                    functionPrediction.setType(cvTerm.getOntologyAltname());
                    functionPrediction.setCvName(cvTerm.getName());
                    functionPrediction.setCvTermDescription(cvTerm.getDescription());

                    //Evidences
                    functionPrediction.getEvidences()
                            .forEach(predictionEvidence -> {
                                String ecoCodeAccession = predictionEvidence.getEcoCodeAccession();
                                CvTerm ecoCVTerm = terminologyService.findCvTermByAccessionOrThrowRuntimeException(ecoCodeAccession);
                                predictionEvidence.setEcoCodeName(ecoCVTerm.getName());
                            });

                    aggregateFunctionPrediction.addPrediction(functionPrediction);
                });

        return aggregateFunctionPrediction;
    }
}
