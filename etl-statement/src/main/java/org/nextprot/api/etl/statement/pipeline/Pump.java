package org.nextprot.api.etl.statement.pipeline;

import java.io.IOException;

/**
 * Pump element of a pipeline
 * @param <E> element type
 */
public interface Pump<E> {

	E pump() throws IOException;
	boolean isEmpty() throws IOException;
}
