package org.nextprot.api.etl.service.impl;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.commons.utils.StringUtils;
import org.nextprot.api.core.app.StatementSource;
import org.nextprot.api.etl.service.StatementTransformerService;
import org.nextprot.api.etl.service.impl.StatementETLServiceImpl.ReportBuilder;
import org.nextprot.commons.statements.Statement;
import org.nextprot.commons.statements.TargetIsoformSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.junit.Assert.fail;
import static org.nextprot.api.commons.constants.AnnotationCategory.VARIANT;
import static org.nextprot.commons.statements.specs.CoreStatementField.ANNOTATION_CATEGORY;
import static org.nextprot.commons.statements.specs.CoreStatementField.TARGET_ISOFORMS;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles({"dev", "build"})
@ContextConfiguration("classpath:spring/core-context.xml")
public class StatementTransformServiceTest {

	@Autowired
	private StatementTransformerService statementTransformerService;

	/**
	 * It is not allowed to have a subject composed by variants in different genes 
	 */
	@Test
	public void shouldThrowAnExceptionWhenMultipleMutantsAreLocatedOnDifferentGenes() throws IOException {
	
		try {
			StatementsExtractorLocalMockImpl sle = new StatementsExtractorLocalMockImpl();
			Collection<Statement> rawStatements = sle.getStatementsFromJsonFile(StatementSource.BioEditor, null, "msh2-msh6-multiple-mutants-on-different-genes");

			statementTransformerService.transformStatements(rawStatements, new ReportBuilder());
			
			fail();
			
		}catch(NextProtException e){
			
			Assert.assertEquals("Mixing iso numbers for subjects is not allowed", e.getMessage());
			Assert.assertEquals(NextProtException.class, e.getClass());
		}
	}
	
	/**
	 * Specification: A variant should always be propagated to all possible isoforms.
	 * If a variant can not be propagated to an isoform, then the 'phenotypic variation' should not exist for that isoform neiher.
	 * However the object annotation should be propagate to all isoforms (if not positional)
	 * 
	 * In this test we check if the propagation of the variant MSH6-p.Thr1219Asp is not propagated to the isoform 2, because the Isoform 2 can not contain this exons.
	 * Additionally we check that the phenotypic variation annotation is well propagated according to the variant.
	 * Finally we check that the object is propagate to all isoforms.
	 * 
	 */
	@Test
	public void shouldPropagateVariantsOnlyToMappableIsoforms() throws IOException {

		StatementsExtractorLocalMockImpl sle = new StatementsExtractorLocalMockImpl();
		Collection<Statement> rawStatements = sle.getStatementsFromJsonFile(StatementSource.BioEditor, null, "msh6-variant-on-iso1-but-not-on-iso2");

		//Variant 
		Collection<Statement> mappedStatements = statementTransformerService.transformStatements(rawStatements, new ReportBuilder());
		
		Statement variantMappedStatement = mappedStatements.stream()
				.filter(new AnnotationCategoryPredicate(VARIANT))
				.findFirst()
				.orElseThrow(RuntimeException::new);
		
		String variantMappedStatementIsoformsJson = variantMappedStatement.getValue(TARGET_ISOFORMS);
		String isoPropagationsWithoutIsoform2 = "[{\"isoformAccession\":\"NX_P52701-1\",\"specificity\":\"UNKNOWN\",\"begin\":1219,\"end\":1219,\"name\":\"MSH6-isoGTBP-N-p.Thr1219Asp\"},{\"isoformAccession\":\"NX_P52701-3\",\"specificity\":\"UNKNOWN\",\"begin\":1089,\"end\":1089,\"name\":\"MSH6-iso3-p.Thr1089Asp\"},{\"isoformAccession\":\"NX_P52701-4\",\"specificity\":\"UNKNOWN\",\"begin\":917,\"end\":917,\"name\":\"MSH6-iso4-p.Thr917Asp\"}]";
		
		Assert.assertEquals(variantMappedStatementIsoformsJson, isoPropagationsWithoutIsoform2);
	
		//Phenotypic variation
		Statement phentypicMappedStatement = mappedStatements.stream().filter(new AnnotationCategoryPredicate(AnnotationCategory.PHENOTYPIC_VARIATION)).findFirst().orElseThrow(RuntimeException::new);
		String phenotypicMappedStatementIsoformJson = phentypicMappedStatement.getValue(TARGET_ISOFORMS);
		Assert.assertEquals(TargetIsoformSet.deSerializeFromJsonString(phenotypicMappedStatementIsoformJson).size(), 3);
		
		String phenotypicWithoutIsoform2 = "[{\"isoformAccession\":\"NX_P52701-1\",\"specificity\":\"UNKNOWN\",\"name\":\"MSH6-isoGTBP-N-p.Thr1219Asp\"},{\"isoformAccession\":\"NX_P52701-3\",\"specificity\":\"UNKNOWN\",\"name\":\"MSH6-iso3-p.Thr1089Asp\"},{\"isoformAccession\":\"NX_P52701-4\",\"specificity\":\"UNKNOWN\",\"name\":\"MSH6-iso4-p.Thr917Asp\"}]";
		
		Assert.assertEquals(TargetIsoformSet.deSerializeFromJsonString(phenotypicWithoutIsoform2).size(), 3);
		Assert.assertEquals(phenotypicMappedStatementIsoformJson, phenotypicWithoutIsoform2);
		

		//Object
		Statement objectStatement = mappedStatements.stream().filter(new AnnotationCategoryPredicate(AnnotationCategory.GO_MOLECULAR_FUNCTION)).findFirst().orElseThrow(RuntimeException::new);
		String objectMappedStatementIsoformJson = phentypicMappedStatement.getValue(TARGET_ISOFORMS);
		
		//TODO what to do??? System.err.println(objectMappedStatementIsoformJson);
	}
	
	
	/**
	 * When one receive a phenotypic variation whereby the subject is specific,
	 */
	@Test
	public void shouldPropagateVariantButNotPhenotypicVariationOnIsoSpecificVPAnnotations() throws IOException {
		
		StatementsExtractorLocalMockImpl sle = new StatementsExtractorLocalMockImpl();
		Collection<Statement> rawStatements = sle.getStatementsFromJsonFile(StatementSource.BioEditor, null, "scn9a-variant-iso-spec");

		//Variant 
		Collection<Statement> mappedStatements = statementTransformerService.transformStatements(rawStatements, new ReportBuilder());
		Statement variantMappedStatement = mappedStatements.stream()
                .filter(new AnnotationCategoryPredicate(AnnotationCategory.VARIANT))
                .findFirst()
                .orElseThrow(RuntimeException::new);
		String variantMappedStatementIsoformJson = variantMappedStatement.getValue(TARGET_ISOFORMS);

		Assert.assertEquals(4, TargetIsoformSet.deSerializeFromJsonString(variantMappedStatementIsoformJson).size());
		Assert.assertEquals("[{\"isoformAccession\":\"NX_Q15858-1\",\"specificity\":\"UNKNOWN\",\"begin\":1460,\"end\":1460,\"name\":\"SCN9A-iso1-p.Phe1460Val\"},{\"isoformAccession\":\"NX_Q15858-2\",\"specificity\":\"UNKNOWN\",\"begin\":1460,\"end\":1460,\"name\":\"SCN9A-iso2-p.Phe1460Val\"},{\"isoformAccession\":\"NX_Q15858-3\",\"specificity\":\"UNKNOWN\",\"begin\":1449,\"end\":1449,\"name\":\"SCN9A-iso3-p.Phe1449Val\"},{\"isoformAccession\":\"NX_Q15858-4\",\"specificity\":\"UNKNOWN\",\"begin\":1449,\"end\":1449,\"name\":\"SCN9A-iso4-p.Phe1449Val\"}]", variantMappedStatementIsoformJson);
		
		//Phenotypic variation
		Statement phenotypicMappedStatement = mappedStatements.stream()
                .filter(new AnnotationCategoryPredicate(AnnotationCategory.PHENOTYPIC_VARIATION))
                .findFirst()
                .orElseThrow(RuntimeException::new);

		String phenotypicMappedStatementIsoformJson = phenotypicMappedStatement.getValue(TARGET_ISOFORMS);

		Assert.assertEquals(1, TargetIsoformSet.deSerializeFromJsonString(phenotypicMappedStatementIsoformJson).size());
		Assert.assertEquals("[{\"isoformAccession\":\"NX_Q15858-3\",\"specificity\":\"SPECIFIC\",\"name\":\"SCN9A-iso3-p.Phe1449Val\"}]", phenotypicMappedStatementIsoformJson);
	}

	@Test
	public void shouldPropagatePropagableRegionToOtherIsoform() throws IOException{
		StatementsExtractorLocalMockImpl sle = new StatementsExtractorLocalMockImpl();
		Collection<Statement> rawStatements = sle.getStatementsFromJsonFile(StatementSource.ENYO, null, "enyo-statements");

		//Interaction mapping
		Collection<Statement> mappedStatements = statementTransformerService.transformStatements(rawStatements, new ReportBuilder());
		List<Statement> regionalMappedStatements = mappedStatements.stream()
				.filter(new AnnotationCategoryPredicate(AnnotationCategory.INTERACTION_MAPPING))
				.filter(statement ->  "NX_Q9Y6Q6".equals(statement.getEntryAccession()))
				.collect(Collectors.toList());

		Assert.assertTrue(!regionalMappedStatements.isEmpty());
		for(Statement regionalMappedStatement : regionalMappedStatements) {
			String regionalMappedStatementIsoformJson = regionalMappedStatement.getValue(TARGET_ISOFORMS);
			if("NX_Q9Y6Q6-5".equals(regionalMappedStatement.getOptionalIsoformAccession().toString())) {
				Assert.assertEquals(2, TargetIsoformSet.deSerializeFromJsonString(regionalMappedStatementIsoformJson).size());
				Assert.assertEquals("[{\"isoformAccession\":\"NX_Q9Y6Q6-5\",\"specificity\":\"UNKNOWN\",\"begin\":340,\"end\":421},{\"isoformAccession\":\"NX_Q9Y6Q6-6\",\"specificity\":\"UNKNOWN\",\"begin\":326,\"end\":407}]", regionalMappedStatementIsoformJson);
			} else if("NX_Q9Y6Q6-2".equals(regionalMappedStatement.getOptionalIsoformAccession().toString())) {
				Assert.assertEquals(3, TargetIsoformSet.deSerializeFromJsonString(regionalMappedStatementIsoformJson).size());
				Assert.assertEquals("[{\"isoformAccession\":\"NX_Q9Y6Q6-2\",\"specificity\":\"UNKNOWN\",\"begin\":227,\"end\":299},{\"isoformAccession\":\"NX_Q9Y6Q6-3\",\"specificity\":\"UNKNOWN\",\"begin\":265,\"end\":337},{\"isoformAccession\":\"NX_Q9Y6Q6-6\",\"specificity\":\"UNKNOWN\",\"begin\":530,\"end\":602}]", regionalMappedStatementIsoformJson);
			}
		}
	}

	@Test
	public void shouldReturnSameTargetIsoformWhenSingleIsoform() throws IOException{
		StatementsExtractorLocalMockImpl sle = new StatementsExtractorLocalMockImpl();
		Collection<Statement> rawStatements = sle.getStatementsFromJsonFile(StatementSource.ENYO, null, "enyo-statements");

		//Interaction mapping
		Collection<Statement> mappedStatements = statementTransformerService.transformStatements(rawStatements, new ReportBuilder());
		Optional<Statement> mappedStatementWithOneIsoform = mappedStatements.stream()
				.filter(statement ->  "NX_P05412".equals(statement.getEntryAccession()))
				.findFirst();

		if(mappedStatementWithOneIsoform.isPresent()) {
			String regionalMappedStatementIsoformJson1 = mappedStatementWithOneIsoform.get().getValue(TARGET_ISOFORMS);
			Assert.assertEquals(1, TargetIsoformSet.deSerializeFromJsonString(regionalMappedStatementIsoformJson1).size());
			Assert.assertEquals("[{\"isoformAccession\":\"NX_P05412-1\",\"specificity\":\"SPECIFIC\",\"begin\":1,\"end\":89}]", regionalMappedStatementIsoformJson1);
		}
	}

	static class AnnotationCategoryPredicate implements Predicate<Statement>{

		private AnnotationCategory category = null;
		public AnnotationCategoryPredicate(AnnotationCategory category){
			this.category = category;
		}

		@Override
		public boolean test(Statement s) {
			String sCat = s.getValue(ANNOTATION_CATEGORY);
			AnnotationCategory sCategory = AnnotationCategory.getDecamelizedAnnotationTypeName(StringUtils.camelToKebabCase(sCat));
			return sCategory.equals(category);
		}
	}
}
