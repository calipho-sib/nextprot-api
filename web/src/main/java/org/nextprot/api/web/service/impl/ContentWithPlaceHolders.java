package org.nextprot.api.web.service.impl;

import org.nextprot.api.core.service.StatisticsService;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.EnumSet;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Resolve text content with placeholders
 *
 * TODO: make this object immutable
 */
class ContentWithPlaceHolders {

	private static DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#,###");

	private static final String OPENED_TAG = "\\$\\{";
	private static final String CLOSED_TAG = "\\}";
	private static final Pattern OPENED_TAG_PATTERN = Pattern.compile(OPENED_TAG, Pattern.MULTILINE);

	private final String originalContent;
	private String updatedContent;
	private final String openedTag;
	private final String closedTag;

	ContentWithPlaceHolders(String contentWithPlaceHolders) {

		this.originalContent = contentWithPlaceHolders;
		this.updatedContent = originalContent;
		this.openedTag = OPENED_TAG;
		this.closedTag = CLOSED_TAG;
	}

	void resolveDatePHs() {

		String datePlaceHolder = openedTag + "COPYRIGHT_END_DATE" + closedTag;

		updatedContent = updatedContent.replaceAll(datePlaceHolder, String.valueOf(Calendar.getInstance().get(Calendar.YEAR)));
	}

	void resolveStatPHs(StatisticsService statisticsService) {

		Map<StatisticsService.Counter, Integer> counters = statisticsService.getStatsByPlaceholder(
				EnumSet.allOf(StatisticsService.Counter.class));

		for (Map.Entry<StatisticsService.Counter, Integer> entry : counters.entrySet()) {

			updatedContent = updatedContent.replaceAll("\\$\\{" + entry.getKey() + "\\}",
					DECIMAL_FORMAT.format(entry.getValue()));
		}
	}

	String resolvedContent() {

		return updatedContent;
	}

	static boolean foundPlaceHolders(String content) {

		return OPENED_TAG_PATTERN.matcher(content).find();
	}
}
