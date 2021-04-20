package org.nextprot.api.core.service.impl;

import org.nextprot.api.core.dao.FunctionPredictionDAO;
import org.nextprot.api.core.domain.CvTerm;
import org.nextprot.api.core.domain.FunctionPrediction;
import org.nextprot.api.core.service.FunctionPredictionService;
import org.nextprot.api.core.service.TerminologyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FunctionPredictionServiceImpl implements FunctionPredictionService {

    @Autowired
    FunctionPredictionDAO functionPredictionDAO;

    @Autowired
    private TerminologyService terminologyService;

    /**
     * Returns the list of function predictions
     * @return List of function predictions
     */
    public List<FunctionPrediction> getFunctionPredictions() {
        // Loads the predictions via DAO layer
        List<FunctionPrediction> functionPredictions = functionPredictionDAO.getPredictions()
                .stream()
                .map(functionPrediction -> {
                    String cvTermAccession = functionPrediction.getCvTermAccession();
                    CvTerm cvTerm = terminologyService.findCvTermByAccessionOrThrowRuntimeException(cvTermAccession);
                    functionPrediction.setCvName(cvTerm.getName());
                    functionPrediction.setCvTermDescription(cvTerm.getDescription());

                    //Evidences
                    functionPrediction.getEvidences()
                            .forEach(predictionEvidence -> {
                                String ecoCodeAccession = predictionEvidence.getEcoCodeAccession();
                                CvTerm ecoCVTerm = terminologyService.findCvTermByAccessionOrThrowRuntimeException(ecoCodeAccession);
                                predictionEvidence.setEcoCodeName(ecoCVTerm.getName());
                            });
                    return functionPrediction;
                })
                .collect(Collectors.toList());

        return functionPredictions;
    }
}
