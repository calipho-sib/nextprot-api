package org.nextprot.api.web.service;

import org.nextprot.api.commons.service.MasterIdentifierService;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.service.EntryBuilderService;
import org.nextprot.api.core.service.annotation.ValidEntry;
import org.nextprot.api.core.service.fluent.EntryConfig;
import org.nextprot.api.web.ui.PageDisplayTester;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;

@Service
class EntryPageServiceImpl implements EntryPageService {

	@Autowired EntryBuilderService entryBuilderService;
	@Autowired MasterIdentifierService masterIdentifierService;

	@Override
	public Set<String> getAllTestingPageNames() {

		return PageDisplayTester.getAllTestingPageNames();
	}

	// TODO: configure eh-cache
	@Cacheable("page-content")
	@Override
	public Map<String, Boolean> testEntryContentForPageDisplay(@ValidEntry String entryName) {

		Entry entry = this.entryBuilderService.build(EntryConfig.newConfig(entryName).withEverything());

		return PageDisplayTester.allPageRequirements(entry).testPageContent();
	}
}
