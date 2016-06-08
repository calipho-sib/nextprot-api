package com.nextprot.api.annotation.builder.statement.dao;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.nextprot.api.annotation.builder.AnnotationBuilderBaseTest;

public class RawStatementDaoTest extends AnnotationBuilderBaseTest {

	@Autowired
	private RawStatementDao rawStatementDao;

	@Test
	public void findAllMappedStatements() {
		List rows = rawStatementDao.findPhenotypeRawStatements("NX_Q9BX63");
		System.out.println(rows.size());
	}

}
