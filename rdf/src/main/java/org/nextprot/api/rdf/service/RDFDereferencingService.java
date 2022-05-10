package org.nextprot.api.rdf.service;

import java.util.Optional;

public interface RDFDereferencingService {

    String generateRDFContent(String entity, Optional<String> accession, String contentType);
}
