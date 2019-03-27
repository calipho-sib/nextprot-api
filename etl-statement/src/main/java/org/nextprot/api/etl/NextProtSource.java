package org.nextprot.api.etl;

import org.nextprot.commons.statements.schema.NXFlatTableSchema;
import org.nextprot.commons.statements.schema.Schema;

import java.util.Arrays;
import java.util.Collections;
import java.util.function.Supplier;

public enum NextProtSource {

	BioEditor("neXtProt", "http://kant.sib.swiss:9001/bioeditor", NXFlatTableSchema::new),
	GlyConnect("GlyConnect", "http://kant.sib.swiss:9001/glyconnect", NXFlatTableSchema::new),
	GnomAD("gnomAD", "http://kant.sib.swiss:9001/gnomad", () -> NXFlatTableSchema.withExtraFields(Arrays.asList(
			"CANONICAL", "ALLELE_COUNT", "ALLELE_SAMPLED"), Collections.singletonList("DBSNP_ID")))
	;

	private String sourceName;
	private String statementsUrl;
	private Schema schema;

	NextProtSource(String sourceName, String statementsUrl, Supplier<Schema> schemaSupplier) {
		this.sourceName = sourceName;
		this.statementsUrl = statementsUrl;
		this.schema = schemaSupplier.get();
	}

	public String getSourceName() {
		return sourceName;
	}

	public String getStatementsUrl() {
		return statementsUrl;
	}

	public Schema getSchema() {
		return schema;
	}
}
