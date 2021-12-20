package org.nextprot.api.core.service;

import java.util.List;
import java.util.Map;

import org.nextprot.api.core.domain.AggregateFunctionPrediction;

public interface FunctionPredictionService {

    AggregateFunctionPrediction getFunctionPredictions(String entryAccession);
    Map<String,List<String>>  getInvalidPredictions();
}
