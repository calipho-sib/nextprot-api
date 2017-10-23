package org.nextprot.api.web.service;

/**
 * Created by dteixeir on 21.09.17.
 */
import java.util.Map;

public interface GraphQlExecutor {
    Object executeRequest(Map requestBody);
}