package org.nextprot.api.core.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.ModifiedEntry;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.service.EntryModifiedAnnotationService;
import org.nextprot.api.core.utils.AnnotationUtils;
import org.springframework.stereotype.Service;

@Service
public class FakeEntryModifiedAnnotationServiceImp implements EntryModifiedAnnotationService {

	@Override
	public List<ModifiedEntry> findAnnotationsForModifiedEntry(Entry entry) {

		List<ModifiedEntry> modifiedEntries = new ArrayList<>();
		List<Long> normalAnnotationReferedIds = new ArrayList<Long>();
		
		for (int i = 0; i < 10; i++){
			modifiedEntries.add(getRandomModifiedEntry(entry, normalAnnotationReferedIds));
			List<Annotation> filteredAnnotations = entry.getAnnotations().stream().
					filter(a -> normalAnnotationReferedIds.contains(a.getAnnotationId())).collect(Collectors.toList());
			entry.setAnnotations(filteredAnnotations);
		}
		return modifiedEntries;
	}

	private ModifiedEntry getRandomModifiedEntry(Entry entry, List<Long> ids) {

		ModifiedEntry me = new ModifiedEntry();
		me.annotations = new ArrayList<>();
		me.subjectName = entry.getOverview().getMainGeneName() + "Val " + getRandomPosition() + " Ser";

		
		for (int i = 0; i < 3; i++) {
			Annotation a = new Annotation();
			a.setCategory(AnnotationCategory.IMPACT);
			a.setCvTermName(getRandomImpact());

			AnnotationCategory category = getRandomCategory();
			List<Annotation> categoryAnnotations = AnnotationUtils.filterAnnotationsByCategory(entry, category);
			Annotation randomAnnotation = getRandomCategoryAnnotation(categoryAnnotations);
			a.setNormalAnnotationReferenceId(randomAnnotation.getAnnotationId());
			ids.add(randomAnnotation.getAnnotationId());
			me.annotations.add(a);

		}

		return me;
	}
	
	

	private Annotation getRandomCategoryAnnotation(List<Annotation> categoryAnnotations) {
		
		Random random = new Random();
		int index = random.nextInt(categoryAnnotations.size());
		return categoryAnnotations.get(index);
		
	}

	private int getRandomPosition() {
		Random rand = new Random();
		return 1 + rand.nextInt((2000 - 1) + 1);

	}

	private AnnotationCategory getRandomCategory() {

		final AnnotationCategory[] proper_noun = { AnnotationCategory.GO_BIOLOGICAL_PROCESS, AnnotationCategory.GO_MOLECULAR_FUNCTION, AnnotationCategory.GO_CELLULAR_COMPONENT };
		Random random = new Random();
		int index = random.nextInt(proper_noun.length);
		return proper_noun[index];
	}

	private String getRandomImpact() {

		final String[] proper_noun = { "Increase", "Decrease", "NoImpact", "Impact", "Gain" };
		Random random = new Random();
		int index = random.nextInt(proper_noun.length);
		return proper_noun[index];
	}

}
