package org.nextprot.api.etl;


import org.nextprot.commons.statements.specs.CompositeField;
import org.nextprot.commons.statements.specs.Specifications;
import org.nextprot.commons.statements.specs.StatementField;
import org.nextprot.commons.statements.specs.StatementSpecifications;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public enum NextProtSource implements StatementSpecifications {

	BioEditor("neXtProt", "http://kant.sib.swiss:9001/bioeditor", new Specifications.Builder().build()),
	GlyConnect("GlyConnect", "http://kant.sib.swiss:9001/glyconnect", new Specifications.Builder().build()),
	GnomAD("gnomAD", "http://kant.sib.swiss:9001/gnomad", new Specifications.Builder()
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

	@Override
	public StatementField getField(String fieldName) {

		return specifications.getField(fieldName);
	}

	@Override
	public boolean hasField(String fieldName) {

		return specifications.hasField(fieldName);
	}

	@Override
	public Collection<StatementField> getFields() {

		return specifications.getFields();
	}

	@Override
	public int size() {

		return specifications.size();
	}

	@Override
	public CompositeField searchCompositeFieldOrNull(StatementField field) {

		return specifications.searchCompositeFieldOrNull(field);
	}
}
