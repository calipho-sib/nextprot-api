package org.nextprot.api.core.dao;

import org.nextprot.api.core.domain.FunctionPrediction;
import java.util.List;

public interface FunctionPredictionDAO {

    List<FunctionPrediction> getPredictions(String entry);
}
