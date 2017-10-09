package org.nextprot.api.commons.exception;


import org.nextprot.api.commons.bio.Chromosome;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


@ResponseStatus(HttpStatus.NOT_FOUND)
public class ChromosomeNotFoundException extends NextProtException {

    private static final long serialVersionUID = 1L;

    private final String chromosome;

    public ChromosomeNotFoundException(String chromosome) {

        super("Chromosome '" + chromosome + "' was not found. Give a valid name among "+ Chromosome.getNames().toString());

        this.chromosome = chromosome;
    }

    public String getChromosome() {

        return chromosome;
    }
}