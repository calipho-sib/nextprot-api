package com.nextprot.api.annotation.builder.statement.dao;

import java.util.List;

import org.junit.Test;
import org.nextprot.api.core.domain.annotation.IsoformAnnotation;
import org.springframework.beans.factory.annotation.Autowired;

import com.nextprot.api.annotation.builder.AnnotationBuilderBaseTest;

public class RawStatementDaoTest extends AnnotationBuilderBaseTest {

	@Autowired
	private RawStatementDao rawStatementDao;

	@Test
	public void findAllPhenotypesStatements() {
		List rows = rawStatementDao.findPhenotypeRawStatements("NX_Q9BX63");
		System.out.println(rows.size());
	}
	

	@Test
	public void findAllNormalStatements() {
		List rows = rawStatementDao.findNormalRawStatements("NX_Q9BX63");
		System.out.println(rows.size());
	}




}
