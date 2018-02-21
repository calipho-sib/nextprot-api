package org.nextprot.api.core.dao;

import java.util.List;

import org.nextprot.commons.statements.Statement;
import org.nextprot.commons.statements.StatementField;
import org.nextprot.commons.statements.constants.AnnotationType;

public interface StatementDao {

	List<Statement> findNormalStatements(AnnotationType annotationType, String entryName);

	List<Statement> findProteoformStatements(AnnotationType annotationType, String entryName);

	List<Statement> findStatementsByAnnotIsoIds(AnnotationType annotationType, List<String> ids);

	List<Statement> findStatementsByAnnotEntryId(AnnotationType annotationType, String annotEntryId);

	List<String> findAllDistinctValuesforField(StatementField field);

	List<String> findAllDistinctValuesforFieldWhereFieldEqualsValues(StatementField field, StatementSimpleWhereClauseQueryDSL... conditions);

}
