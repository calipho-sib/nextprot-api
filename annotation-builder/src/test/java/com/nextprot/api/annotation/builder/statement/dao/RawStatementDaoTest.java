package com.nextprot.api.annotation.builder.statement.dao;

import java.util.List;

import org.junit.Test;
import org.nextprot.api.core.test.base.CoreUnitBaseTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import jdk.nashorn.internal.ir.annotations.Ignore;

@ActiveProfiles({ "dev" })
@Ignore
public class RawStatementDaoTest extends CoreUnitBaseTest {

	@Autowired
	private RawStatementDao rawStatementDao;

	@Test
	public void findAllMappedStatements() {
		List rows = rawStatementDao.findRawStatements();
		System.out.println(rows.size());
	}

}
