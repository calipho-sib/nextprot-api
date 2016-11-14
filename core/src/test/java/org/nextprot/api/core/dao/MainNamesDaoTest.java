package org.nextprot.api.core.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.nextprot.api.core.domain.MainNames;
import org.nextprot.api.core.domain.PublicationAuthor;
import org.nextprot.api.core.test.base.CoreUnitBaseTest;
import org.springframework.beans.factory.annotation.Autowired;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;



@DatabaseSetup(value = "MainNamesTest.xml", type = DatabaseOperation.INSERT)
public class MainNamesDaoTest extends CoreUnitBaseTest {

	@Autowired private MainNamesDAO mainNamesDao;
	
	@Test
	public void testMapKeys() {
		Map<String,MainNames> namesMap = this.mainNamesDao.getMainNamesMap();
		
		// 3 masters + 4 isoforms = 7 entries in the map
		
		assertEquals(7, namesMap.size());
		assertTrue(namesMap.containsKey("NX_12345"));
		assertTrue(namesMap.containsKey("NX_12346"));
		assertTrue(namesMap.containsKey("NX_12347"));
		assertTrue(namesMap.containsKey("NX_12345-1"));
		assertTrue(namesMap.containsKey("NX_12345-2"));
		assertTrue(namesMap.containsKey("NX_12346-1"));
		assertTrue(namesMap.containsKey("NX_12347-1"));
	}
	
	@Test
	public void testMapContentForMaster() {
		Map<String,MainNames> namesMap = this.mainNamesDao.getMainNamesMap();
		// 3 masters + 4 isoforms = 7 entries in the map
		MainNames names = namesMap.get("NX_12345");
		assertEquals("NX_12345", names.getAccession());
		assertEquals("https://www.nextprot.org/entry/NX_12345", names.getUrl());
		assertEquals("Master name 1", names.getName());
		assertEquals(1, names.getGeneNameList().size());
		assertEquals("GENE1", names.getGeneNameList().get(0));
	}
	
	@Test
	public void testMapContentForMasterWithMulitpleGeneNames() {
		Map<String,MainNames> namesMap = this.mainNamesDao.getMainNamesMap();
		MainNames names = namesMap.get("NX_12347");
		assertEquals(3, names.getGeneNameList().size());
		assertEquals("GENE3", names.getGeneNameList().get(0));
		assertEquals("GENE4", names.getGeneNameList().get(1));
		assertEquals("GENE5", names.getGeneNameList().get(2));
	}
	
	@Test
	public void testMapContentForIsoformShort() {
		Map<String,MainNames> namesMap = this.mainNamesDao.getMainNamesMap();
		MainNames names = namesMap.get("NX_12345-1");
		assertEquals("NX_12345-1", names.getAccession());
		assertEquals("https://www.nextprot.org/entry/NX_12345-1", names.getUrl());
		assertEquals("Short", names.getName());
		assertEquals(1, names.getGeneNameList().size());
		assertEquals("GENE1", names.getGeneNameList().get(0));
	}
	
	@Test
	public void testMapContentForIsoformLong() {
		Map<String,MainNames> namesMap = this.mainNamesDao.getMainNamesMap();
		MainNames names = namesMap.get("NX_12345-2");
		assertEquals("NX_12345-2", names.getAccession());
		assertEquals("https://www.nextprot.org/entry/NX_12345-2", names.getUrl());
		assertEquals("Long", names.getName());
		assertEquals(1, names.getGeneNameList().size());
		assertEquals("GENE1", names.getGeneNameList().get(0));
	}
	
	@Test
	public void testMapContentForIsoformWithNumericName() {
		Map<String,MainNames> namesMap = this.mainNamesDao.getMainNamesMap();
		MainNames names = namesMap.get("NX_12346-1");
		assertEquals("Iso 1", names.getName());
	}
	
}
