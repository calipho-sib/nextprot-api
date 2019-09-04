package org.nextprot.api.core.dao;

import org.nextprot.api.core.dao.impl.StatementSimpleWhereClauseQueryDSL;
import org.nextprot.commons.statements.Statement;
import org.nextprot.commons.statements.specs.StatementField;

import java.util.List;

public interface StatementDao {

	List<Statement> findNormalStatements(String entryName);

	List<Statement> findProteoformStatements(String entryName);

	List<Statement> findStatementsByAnnotIsoIds(List<String> ids);

	List<String> findAllDistinctValuesforField(StatementField field);

	List<String> findAllDistinctValuesforFieldWhereFieldEqualsValues(StatementField field, StatementSimpleWhereClauseQueryDSL... conditions);

}
