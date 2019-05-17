package org.nextprot.api.etl.statement.source;


import org.nextprot.commons.statements.specs.Specifications;

import java.io.IOException;
import java.util.stream.Stream;

public interface SplittableStatementSource {

	Specifications specifications();
	Stream<SimpleStatementSource> split() throws IOException;
}
