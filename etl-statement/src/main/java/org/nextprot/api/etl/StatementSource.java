package org.nextprot.api.etl;


import org.nextprot.commons.statements.specs.CompositeField;
import org.nextprot.commons.statements.specs.Specifications;
import org.nextprot.commons.statements.specs.StatementField;
import org.nextprot.commons.statements.specs.StatementSpecifications;
import org.nextprot.commons.utils.EnumConstantDictionary;
import org.nextprot.commons.utils.EnumDictionarySupplier;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public enum StatementSource implements StatementSpecifications, EnumDictionarySupplier<StatementSource> {

	BioEditor("neXtProt",
			"http://kant.sib.swiss:9001/bioeditor",
			new Specifications.Builder().build()),

	GlyConnect("GlyConnect",
			"http://kant.sib.swiss:9001/glyconnect",
			new Specifications.Builder().build()),

	ENYO("ENYO",
			"http://kant.sib.swiss:9001/enyo",
			new Specifications.Builder().build()),

	GnomAD("gnomAD",
			"http://kant.sib.swiss:9001/gnomad",
			new Specifications.Builder()
					.withExtraFields(Arrays.asList("CANONICAL", "ALLELE_COUNT", "ALLELE_SAMPLED"))
					.withExtraFieldsContributingToUnicityKey(Collections.singletonList("DBSNP_ID"))
					.build())
	;

	private static EnumConstantDictionary<StatementSource> dictionaryOfConstants =
			new EnumConstantDictionary<StatementSource>(StatementSource.class, values()) {
				@Override
				protected void updateDictionaryOfConstants(Map<String, StatementSource> dictionary) {

					for (StatementSource source : values()) {
						dictionary.put(source.toString().toLowerCase(), source);
						dictionary.put(source.toString().toUpperCase(), source);
					}
				}
			};

	private final String sourceName;
	private final String statementsUrl;
	private final StatementSpecifications specifications;

	StatementSource(String sourceName, String statementsUrl, StatementSpecifications specifications) {
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

	public StatementSpecifications getSpecifications() {
		return specifications;
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

	@Override
	public EnumConstantDictionary<StatementSource> getEnumConstantDictionary() {

		return dictionaryOfConstants;
	}

	public static StatementSource valueOfKey(String value) {

		return dictionaryOfConstants.valueOfKey(value);
	}
}
