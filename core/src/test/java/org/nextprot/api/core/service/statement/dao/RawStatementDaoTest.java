package org.nextprot.api.core.service.statement.dao;

import org.junit.Ignore;
import org.junit.Test;
import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.core.dao.StatementDao;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.service.AnnotationBuilderIntegrationBaseTest;
import org.nextprot.commons.statements.Statement;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RawStatementDaoTest extends AnnotationBuilderIntegrationBaseTest {

	@Autowired
	private StatementDao rawStatementDao;

	//TODO to unignore
	@Ignore
	@Test
	public void findProteformStatementsForEntry() {
		List<?> rows = rawStatementDao.findProteoformStatements("NX_Q15858");
		assertTrue(rows.size() > 0);
	}


	//TODO to unignore
	@Ignore
	@Test
	public void findAllNormalStatements() {
		List<Statement> statements = rawStatementDao.findNormalStatements("NX_Q15858");
		assertTrue(statements.size() > 0);
	}
	
	@Test
	public void findModificationEffectCategory(){
		AnnotationCategory category = AnnotationCategory.PHENOTYPIC_VARIATION;
		Annotation isoAnnotation = new Annotation();
		isoAnnotation.setAnnotationCategory(category);
		
		assertEquals(isoAnnotation.getCategory(), "phenotypic-variation");
	}
}
