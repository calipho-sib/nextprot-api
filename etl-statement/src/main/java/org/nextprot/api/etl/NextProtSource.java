package org.nextprot.api.etl;


import org.nextprot.commons.statements.specs.NXFlatTableSchema;
import org.nextprot.commons.statements.specs.StatementSpecifications;

import java.util.Arrays;
import java.util.Collections;

public enum NextProtSource {

	BioEditor("neXtProt", "http://kant.sib.swiss:9001/bioeditor", NXFlatTableSchema.build()),
	GlyConnect("GlyConnect", "http://kant.sib.swiss:9001/glyconnect", NXFlatTableSchema.build()),
	GnomAD("gnomAD", "http://kant.sib.swiss:9001/gnomad", new NXFlatTableSchema.Builder()
			.withExtraFields(Arrays.asList("CANONICAL", "ALLELE_COUNT", "ALLELE_SAMPLED"))
			.withExtraFieldsContributingToUnicityKey(Collections.singletonList("DBSNP_ID"))
			.build())
	;

	private String sourceName;
	private String statementsUrl;
	private StatementSpecifications specifications;

	NextProtSource(String sourceName, String statementsUrl, StatementSpecifications specifications) {
		this.sourceName = sourceName;
		this.statementsUrl = statementsUrl;
		this.specifications = specifications;
	}

	public String getSourceName() {
		return sourceName;
	}

	public String getStatementsUrl() {
		return statementsUrl;
	}

	public StatementSpecifications getStatementSpecifications() {
		return specifications;
	}
}
