package org.nextprot.api.core.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AggregateFunctionPrediction {

    private String entryAC;

    private Map<String, List<FunctionPrediction>> predictions = new HashMap<>();

    public AggregateFunctionPrediction(String entryAC) {
        this.entryAC = entryAC;
    }

    public void addPrediction(FunctionPrediction functionPrediction) {
        String type = functionPrediction.getType();
        if(predictions.get(type) == null) {
            List<FunctionPrediction> predictionsByType = new ArrayList<>();
            predictionsByType.add(functionPrediction);
            predictions.put(type, predictionsByType);
        } else {
            List<FunctionPrediction> predictionsByType = predictions.get(type);
            predictionsByType.add(functionPrediction);
        }
    }
    public String getEntryAC() {
        return entryAC;
    }

    public Map<String, List<FunctionPrediction>> getPredictions() {
        return predictions;
    }

}
