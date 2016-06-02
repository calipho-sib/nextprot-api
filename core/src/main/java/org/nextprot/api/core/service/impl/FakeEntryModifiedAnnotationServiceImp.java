package org.nextprot.api.core.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.core.domain.ModifiedEntry;
import org.nextprot.api.core.domain.Overview;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.service.EntryModifiedAnnotationService;
import org.nextprot.api.core.service.OverviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FakeEntryModifiedAnnotationServiceImp implements EntryModifiedAnnotationService {

	@Autowired
	private OverviewService overviewService;
	
	
	@Override
	public List<ModifiedEntry> findAnnotationsForModifiedEntry(String entryName) {
	
		Overview overview = overviewService.findOverviewByEntry(entryName);
		List<ModifiedEntry> modifiedEntries = new ArrayList<>();
		for(int i=0; i<10; i++)
		modifiedEntries.add(getRandomModifiedEntry(overview.getMainGeneName()));
		return modifiedEntries;
	}

	private ModifiedEntry getRandomModifiedEntry(String geneName) {
		
		ModifiedEntry me = new ModifiedEntry();
		me.annotations = new ArrayList<>();
		me.subjectName = geneName + "Val " + getRandomPosition() + " Ser";
		
		for (int i=0; i<3; i++){
			Annotation a = new Annotation();
			a.setCvTermName(getRandomImpact());
		}
		
		return null;
	}
	
	private int getRandomPosition (){
		Random rand = new Random();
		return 1 + rand.nextInt((2000 - 1) + 1);
		
	}

	
	private String getRandomCategory() {
		
		final String[] proper_noun = {AnnotationCategory.GO_BIOLOGICAL_PROCESS.name(), AnnotationCategory.GO_MOLECULAR_FUNCTION.name(), AnnotationCategory.GO_CELLULAR_COMPONENT.name()};
		Random random = new Random();
		int index = random.nextInt(proper_noun.length);
		return proper_noun[index];
	}

	
	private String getRandomImpact (){
		
		final String[] proper_noun = {"Increase", "Decrease", "NoImpact", "Impact", "Gain"};
		Random random = new Random();
		int index = random.nextInt(proper_noun.length);
		return proper_noun[index];
	}

}
