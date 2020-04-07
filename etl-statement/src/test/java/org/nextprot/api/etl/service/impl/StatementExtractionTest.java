package org.nextprot.api.etl.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.junit.Ignore;
import org.junit.Test;
import org.nextprot.api.core.app.StatementSource;
import org.nextprot.commons.statements.Statement;
import org.nextprot.commons.statements.specs.CustomStatementField;
import org.nextprot.commons.statements.specs.StatementField;

public class StatementExtractionTest {
	
	@Ignore
	@Test
	public void test1() throws Exception {
	/*
	 * This test was used to load statements having all ot the 
	 * annotation-category instanciated by the bioeditor and make sure that
	 * the statement id recomputed by the statement builder class is the same as the one
	 * created by the bioeditor itself
	 * See commented code in StatementBuilder with tag StatementExtractionTest
	 */
		
		StatementRemoteServiceImpl service = new StatementRemoteServiceImpl();
		Collection<Statement> statements = service.getStatementsForSource(StatementSource.BioEditor, "pam-test");
		System.out.println("stmt count:" + statements.size());
		Map<String,Integer> catMap = new TreeMap<>();
		for (Statement s : statements) {
			String cat = s.getAnnotationCategory();
			if (!catMap.containsKey(cat)) catMap.put(cat,  new Integer(0));
			catMap.put(cat, new Integer(catMap.get(cat)+1));
		}
		for (String k: catMap.keySet()) System.out.println(k + " : " + catMap.get(k).intValue());
	}

	public static class StatementFieldComparator implements Comparator<StatementField> {

		static StatementFieldComparator instance = new StatementFieldComparator();

		public static StatementFieldComparator getInstance() { return instance; }
		
		@Override
		public int compare(StatementField o1, StatementField o2) {
			String name1 = o1==null ? "" : o1.getName();
			String name2 = o2==null ? "" : o2.getName();
			return name1.compareTo(name2);	
		}

	}
	
	@Ignore
	@Test
	public void test2() {
		
		StatementField f1 = new CustomStatementField("ggg",true);
		StatementField f2 = new CustomStatementField("aaa",false);
		StatementField f3 = new CustomStatementField("ccc",true);
				
		Collection<StatementField> fcoll = new ArrayList<>();
		fcoll.add(f1);
		fcoll.add(f2);
		fcoll.add(f3);
		
		System.out.println("unsorted collection:");
		for (StatementField f: fcoll) System.out.println(f.getName());
		
		Collection<StatementField> sfcoll = fcoll.stream().sorted(StatementFieldComparator.getInstance()).collect(Collectors.toList());
		
		System.out.println("sorted collection:");
		for (StatementField f: sfcoll) System.out.println(f.getName());
		
		
	}

}
