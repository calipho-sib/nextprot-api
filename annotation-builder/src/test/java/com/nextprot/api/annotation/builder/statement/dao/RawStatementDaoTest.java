package com.nextprot.api.annotation.builder.statement.dao;

import static org.junit.Assert.*;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.junit.Test;
import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.commons.utils.StringUtils;
import org.nextprot.api.core.domain.annotation.IsoformAnnotation;
import org.nextprot.commons.statements.RawStatement;
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
		AtomicInteger i = new AtomicInteger(0);
		List<RawStatement> statements = rawStatementDao.findNormalRawStatements("NX_Q9BX63");
		statements.stream().forEach(s -> System.out.println(i.getAndIncrement() + " - " + s.getAnnot_hash()));
		List<RawStatement> statement = statements.stream().filter(s -> s.getAnnot_hash().equals("c075d4a6b44e95faec7d8b109166744b")).collect(Collectors.toList());
	}
	
	@Test
	public void findPhenotypes(){
		AnnotationCategory category = AnnotationCategory.getDecamelizedAnnotationTypeName(StringUtils.camelToKebabCase("phenotype"));
		IsoformAnnotation isoAnnotation = new IsoformAnnotation();
		isoAnnotation.setCategory(category);
		
		assertEquals(isoAnnotation.getKebabCategoryName(), "phenotype");


	}




}
