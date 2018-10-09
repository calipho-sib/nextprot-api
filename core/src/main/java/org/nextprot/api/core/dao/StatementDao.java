package org.nextprot.api.core.dao;

import org.nextprot.api.core.dao.impl.StatementSimpleWhereClauseQueryDSL;
import org.nextprot.commons.statements.Statement;
import org.nextprot.commons.statements.StatementField;
import org.nextprot.commons.statements.constants.AnnotationType;

import java.util.List;

public interface StatementDao {

	List<Statement> findNormalStatements(AnnotationType annotationType, String entryName);

	List<Statement> findProteoformStatements(AnnotationType annotationType, String entryName);

	List<Statement> findStatementsByAnnotIsoIds(AnnotationType annotationType, List<String> ids);

	List<String> findAllDistinctValuesforField(StatementField field);

	List<String> findAllDistinctValuesforFieldWhereFieldEqualsValues(StatementField field, StatementSimpleWhereClauseQueryDSL... conditions);

}
