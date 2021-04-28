package org.nextprot.api.core.domain;

import java.util.*;

public class AggregateFunctionPrediction {

    private String entryAC;

    private Map<String, List<FunctionPrediction>> predictions = new HashMap<>();

    public AggregateFunctionPrediction(String entryAC) {
        this.entryAC = entryAC;
    }

    public void addPrediction(FunctionPrediction newFunctionPrediction) {
        String type = newFunctionPrediction.getType();
        if(predictions.get(type) == null) {
            List<FunctionPrediction> predictionsByType = new ArrayList<>();
            predictionsByType.add(newFunctionPrediction);
            predictions.put(type, predictionsByType);
        } else {
            List<FunctionPrediction> predictionsByType = predictions.get(type);

            // Evidence aggregation: only add an evidence if the GO term is the same
            Optional<FunctionPrediction> functionPredictionByCVTerm = predictionsByType.stream()
                    .filter(functionPrediction -> functionPrediction.getCvTermAccession().equals(newFunctionPrediction.getCvTermAccession()))
                    .findAny();

            if(functionPredictionByCVTerm.isPresent()){
                functionPredictionByCVTerm.get()
                        .addEvidence(newFunctionPrediction.getEvidences().get(0));
            } else {
                predictionsByType.add(newFunctionPrediction);
            }
        }
    }
    public String getEntryAC() {
        return entryAC;
    }

    public Map<String, List<FunctionPrediction>> getPredictions() {
        return predictions;
    }

}
