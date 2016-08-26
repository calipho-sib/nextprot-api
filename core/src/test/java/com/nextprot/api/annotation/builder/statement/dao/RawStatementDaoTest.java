package com.nextprot.api.annotation.builder.statement.dao;

import com.nextprot.api.annotation.builder.AnnotationBuilderIntegrationBaseTest;
import org.junit.Test;
import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.core.domain.annotation.IsoformAnnotation;
import org.nextprot.commons.statements.Statement;
import org.nextprot.commons.statements.constants.AnnotationType;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RawStatementDaoTest extends AnnotationBuilderIntegrationBaseTest {

	@Autowired
	private StatementDao rawStatementDao;

	@Test
	public void findProteformStatementsForEntry() {
		List<?> rows = rawStatementDao.findProteoformStatements(AnnotationType.ENTRY, "NX_Q15858");
		assertTrue(rows.size() > 0);
	}
	

	@Test
	public void findAllNormalStatements() {
		List<Statement> statements = rawStatementDao.findNormalStatements(AnnotationType.ENTRY, "NX_Q15858");
		assertTrue(statements.size() > 0);
	}
	
	@Test
	public void findModificationEffectCategory(){
		AnnotationCategory category = AnnotationCategory.PHENOTYPIC_VARIATION;
		IsoformAnnotation isoAnnotation = new IsoformAnnotation();
		isoAnnotation.setCategory(category);
		
		assertEquals(isoAnnotation.getCategory(), "phenotypic-variation");
	}
}
