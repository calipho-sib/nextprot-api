package org.nextprot.api.etl;


import org.nextprot.api.commons.app.ApplicationContextProvider;
import org.nextprot.api.etl.service.StatementETLService;
import org.nextprot.api.etl.service.impl.MultipleBatchesStatementETLService;
import org.nextprot.api.etl.service.impl.SingleBatchStatementETLService;
import org.nextprot.commons.statements.specs.CompositeField;
import org.nextprot.commons.statements.specs.Specifications;
import org.nextprot.commons.statements.specs.StatementField;
import org.nextprot.commons.statements.specs.StatementSpecifications;
import org.nextprot.commons.utils.EnumConstantDictionary;
import org.nextprot.commons.utils.EnumDictionarySupplier;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.function.Supplier;

public enum StatementSource implements StatementSpecifications, EnumDictionarySupplier<StatementSource> {

	BioEditor("neXtProt",
			"http://kant.sib.swiss:9001/bioeditor",
			new Specifications.Builder().build()),

	GlyConnect("GlyConnect",
			"http://kant.sib.swiss:9001/glyconnect",
			new Specifications.Builder().build()),

	GnomAD("gnomAD",
			"http://kant.sib.swiss:9001/gnomad",
			new Specifications.Builder()
					.withExtraFields(Arrays.asList("CANONICAL", "ALLELE_COUNT", "ALLELE_SAMPLED"))
					.withExtraFieldsContributingToUnicityKey(Collections.singletonList("DBSNP_ID"))
					.build(),
			() -> ApplicationContextProvider.getApplicationContext().getBean(MultipleBatchesStatementETLService.class))
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
	private final Supplier<StatementETLService> etlServiceSupplier;

	StatementSource(String sourceName, String statementsUrl, StatementSpecifications specifications) {

		this(sourceName, statementsUrl, specifications,
				() -> ApplicationContextProvider.getApplicationContext().getBean(SingleBatchStatementETLService.class));
	}

	StatementSource(String sourceName, String statementsUrl, StatementSpecifications specifications, Supplier<StatementETLService> etlServiceSupplier) {
		this.sourceName = sourceName;
		this.statementsUrl = statementsUrl;
		this.specifications = specifications;
		this.etlServiceSupplier = etlServiceSupplier;
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

	public String extractTransformLoadStatements(String release, boolean load) throws IOException {

		return etlServiceSupplier.get().extractTransformLoadStatements(this, release, load);
	}
}
