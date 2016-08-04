package com.nextprot.api.annotation.builder.statement.dao;

import static org.junit.Assert.*;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.junit.Test;
import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.commons.utils.StringUtils;
import org.nextprot.api.core.domain.annotation.IsoformAnnotation;
import org.nextprot.commons.statements.Statement;
import org.nextprot.commons.statements.StatementField;
import org.nextprot.commons.statements.constants.AnnotationType;
import org.springframework.beans.factory.annotation.Autowired;

import com.nextprot.api.annotation.builder.AnnotationBuilderBaseTest;

public class RawStatementDaoTest extends AnnotationBuilderBaseTest {

	@Autowired
	private StatementDao rawStatementDao;

	@Test
	public void findProteformStatementsForEntry() {
		List<?> rows = rawStatementDao.findProteoformStatements(AnnotationType.ENTRY, "NX_Q15858");
		assertTrue(rows.size() > 0);
	}
	
	@Test
	public void findProteformStatementsForIsoform() {
		List<?> rows = rawStatementDao.findProteoformStatements(AnnotationType.ISOFORM, "NX_Q15858-1");
		assertTrue(rows.size() > 0);
	}
	

	@Test
	public void findAllNormalStatements() {
		List<Statement> statements = rawStatementDao.findNormalStatements(AnnotationType.ENTRY, "NX_Q15858");
		assertTrue(statements.size() > 0);
	}
	
	@Test
	public void findModificationEffectCategory(){
		AnnotationCategory category = AnnotationCategory.getDecamelizedAnnotationTypeName(StringUtils.camelToKebabCase("modification-effect"));
		IsoformAnnotation isoAnnotation = new IsoformAnnotation();
		isoAnnotation.setCategory(category);
		
		assertEquals(isoAnnotation.getCategory(), "modification-effect");


	}




}
