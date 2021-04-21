package org.nextprot.api.core.service;

import org.nextprot.api.core.domain.AggregateFunctionPrediction;

public interface FunctionPredictionService {

    AggregateFunctionPrediction getFunctionPredictions(String entryAccession);
}
