package org.nextprot.api.web.service;

import org.nextprot.api.commons.service.MasterIdentifierService;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.service.EntryBuilderService;
import org.nextprot.api.core.service.annotation.ValidEntry;
import org.nextprot.api.core.service.fluent.EntryConfig;
import org.nextprot.api.web.ui.page.PageDisplayReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
class EntryPageServiceImpl implements EntryPageService {

	@Autowired EntryBuilderService entryBuilderService;
	@Autowired MasterIdentifierService masterIdentifierService;

	@Cacheable(value="page-display", key="#entryName")
	@Override
	public Map<String, Boolean> testEntryContentForPageDisplay(@ValidEntry String entryName) {

		Entry entry = entryBuilderService.build(EntryConfig.newConfig(entryName).withEverything());

		return PageDisplayReport.allPages(entry).reportDisplayPageStatus();
	}
}
