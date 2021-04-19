package org.nextprot.api.user.dao;

import org.nextprot.api.user.domain.FunctionPrediction;
import java.util.List;

public interface FunctionPredictionDAO {

    List<FunctionPrediction> getPredictions();
}
