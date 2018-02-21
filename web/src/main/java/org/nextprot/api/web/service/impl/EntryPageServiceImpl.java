package org.nextprot.api.web.service.impl;

import org.nextprot.api.core.domain.DbXref;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.service.EntryBuilderService;
import org.nextprot.api.core.service.annotation.ValidEntry;
import org.nextprot.api.core.service.fluent.EntryConfig;
import org.nextprot.api.core.domain.ui.page.PageView;
import org.nextprot.api.core.domain.ui.page.PageViewFactory;
import org.nextprot.api.web.service.EntryPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EntryPageServiceImpl implements EntryPageService {

	@Autowired EntryBuilderService entryBuilderService;

	@Cacheable(value="page-display", key="#entryName")
	@Override
	public Map<String, Boolean> hasContentForPageDisplay(@ValidEntry String entryName) {

		Entry entry = entryBuilderService.build(EntryConfig.newConfig(entryName).withEverything());

		Map<String, Boolean> map = new HashMap<>();

		for (PageViewFactory page : PageViewFactory.values()) {

			PageView pv = page.getPageView();
			map.put(pv.getLabel(), pv.doDisplayPage(entry));
		}

		return map;
	}

    @Override
    public Entry filterXrefInPageView(String entryName, String pageViewName) {

        Entry entry = entryBuilderService.build(EntryConfig.newConfig(entryName).withEverything());

        PageView pageView = PageViewFactory.valueOf(pageViewName.toUpperCase()).getPageView();

        List<DbXref> xrefs = pageView.getFurtherExternalLinksXrefs(entry);

        entry.setXrefs(xrefs);
        entry.setAnnotations(Collections.emptyList());
        entry.setPublications(Collections.emptyList());
        entry.setExperimentalContexts(Collections.emptyList());
        entry.setIdentifiers(Collections.emptyList());
        entry.setInteractions(Collections.emptyList());
        entry.setEnzymes(Collections.emptyList());

	    return entry;
    }
}
