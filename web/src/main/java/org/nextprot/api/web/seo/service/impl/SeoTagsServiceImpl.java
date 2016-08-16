package org.nextprot.api.web.seo.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.commons.utils.RelativeUrlUtils;
import org.nextprot.api.core.domain.CvTerm;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.Publication;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.service.EntryBuilderService;
import org.nextprot.api.core.service.PublicationService;
import org.nextprot.api.core.service.TerminologyService;
import org.nextprot.api.core.service.fluent.EntryConfig;
import org.nextprot.api.web.domain.NextProtNews;
import org.nextprot.api.web.seo.domain.SeoTags;
import org.nextprot.api.web.seo.domain.SeoTagsAndUrl;
import org.nextprot.api.web.seo.service.SeoTagsService;
import org.nextprot.api.web.service.GitHubService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;


@Service
public class SeoTagsServiceImpl implements SeoTagsService {
	
	private final Log Logger = LogFactory.getLog(SeoTagsServiceImpl.class);

	@Autowired
	private EntryBuilderService entryBuilderService;
	@Autowired
	private TerminologyService terminologyService;
	@Autowired
	private PublicationService publicationService;
	@Autowired
	private GitHubService gitHubService;
	
	private List<SeoTagsAndUrl> hardCodedSeoTags=null;
	
	@Override
	public SeoTags getSeoTags(String url) {
		try {
			SeoTags tags = getHardCodedSeoTag(url);
			if (tags!=null) return tags;
			
			String[] urlElements = RelativeUrlUtils.getPathElements(url);
			
			if ("entry".equals(urlElements[0])) {
				return getEntrySeoTags(urlElements);
			}
			if ("term".equals(urlElements[0])) {
				return getTermSeoTags(urlElements);
			}
			if ("publication".equals(urlElements[0])) {
				return getPublicationSeoTags(urlElements);
			}
			if ("news".equals(urlElements[0])) {
				return getNewsSeoTags(urlElements);
			}
			// default behavior
			Logger.warn("No explicit SEO tags were found for this page: " + url );
			return getDefaultSeoTags(urlElements);
			
		} catch (Exception e) {
			throw new NextProtException(e);
		}
		
	}
	
	@Override
	public List<SeoTagsAndUrl> getHardCodedSeoTags()  {
		try {
			if (hardCodedSeoTags==null) {
				String content = gitHubService.getPage("json-config", "seotags");
				ObjectMapper mapper = new ObjectMapper();
				SeoTagsAndUrl[] tags =  mapper.readValue(content, SeoTagsAndUrl[].class);
				hardCodedSeoTags = Arrays.asList(tags);
			}
			return hardCodedSeoTags;
		} catch (Exception e) {
			Logger.error("Could not get hard coded SEO tags from github");
			return new ArrayList<SeoTagsAndUrl>();
		}
	}
	
	public SeoTags getHardCodedSeoTag(String url) throws Exception  {
		for (SeoTagsAndUrl t: getHardCodedSeoTags()) {
			if (t.getUrl().equals(url)) return (SeoTags)t;
		}
		return null;
	}
	
	
	
	private SeoTags getDefaultSeoTags(String[] urlElements) {
		StringBuilder sb = new StringBuilder();
		for (int i=0;i<urlElements.length; i++) {
			if (i>0) sb.append(" - ");
			sb.append(getPrettyName(urlElements[i]));
		}
		String info = sb.toString();
		return new SeoTags(info,info,info);
	}
	

	private SeoTags getNewsSeoTags(String[] urlElements) {
		
		List<NextProtNews> allNews = gitHubService.getNews();
		if (urlElements.length>1) {
			String pageUrl = urlElements[1];
			for (NextProtNews oneNews: allNews) {
				if (pageUrl.equals(oneNews.getUrl())) {
					return getOneNewsSeoTags(oneNews);
				}
			}
		} else  {
			NextProtNews mostRecentNews = allNews.get(allNews.size()-1);
			return getOneNewsSeoTags(mostRecentNews);
		}
		return null;
	}
	
	private SeoTags getOneNewsSeoTags(NextProtNews news) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String shortDate = sdf.format(news.getPublicationDate());
		String title = "News - " + news.getTitle();
		String h1 = "News - " + shortDate;
		String descr = "News - " + news.getTitle();
		return new SeoTags(title, descr,h1);
	}
	
	private SeoTags getPublicationSeoTags(String[] urlElements) {
		
		int id = Integer.valueOf(urlElements[1]);
		String subpage = "proteins"; // default subpage
		if (urlElements.length>2) subpage=urlElements[2]; 
		String prettySubpage = getPrettyName(subpage);
		Publication pub = publicationService.findPublicationById(id);
		String title = pub.getTitle() + " - "  + prettySubpage;
		String h1 = title;
		String descr = title;
		
		return new SeoTags(title,descr,h1);
	}

	private SeoTags getTermSeoTags(String[] urlElements) {
		
		String ac = urlElements[1];
		String subpage = "proteins"; // default subpage
		if (urlElements.length>2) subpage=urlElements[2]; 
		String prettySubpage = getPrettyName(subpage);
		CvTerm term = terminologyService.findCvTermByAccession(ac);
		String title = term.getAccession() + " - " + getPrettyName(term.getName()) + " - " + prettySubpage;
		String h1 = title;
		String descr = term.getOntology() + " " + term.getAccession() + " - " + term.getName() + " - " + prettySubpage;
		
		return new SeoTags(title,descr,h1);
	}

	
	
	private SeoTags getEntrySeoTags(String[] urlElements) {
		
		String ac = urlElements[1];
		String subpage = "function"; // default subpage
		if (urlElements.length>2) subpage=urlElements[2]; 
		String prettySubpage = getPrettyName(subpage);
		
		Entry entry = entryBuilderService.build(EntryConfig.newConfig(ac).withOverview().with("function-info"));
		String protName = entry.getOverview().getMainProteinName();
		String geneName = entry.getOverview().getMainGeneName();
		String title = geneName + " - " + protName + " - " + prettySubpage;  // decision NPC 15.08.2016
		String h1 = geneName + " - " + prettySubpage;                        // decision NPC 15.08.2016
		
		
		// TODO: send something different for each subpage... see with amos
		String descr = ac + " - " + title + ". " + getFirstFunctionInfo(entry);
		
		return new SeoTags(title,descr,h1);
	}
	
	private String getPrettyName(String subpage) {
		subpage = subpage.replace("_", " ").replace("-", " ");
		if (subpage.length()>0) subpage = subpage.substring(0,1).toUpperCase()+subpage.substring(1);
		return subpage;		
	}
	
	// TODO check existence of such a function somewhere else
	// TODO reuse sorting function of Alain
	private String getFirstFunctionInfo(Entry entry) {
		for (Annotation a : entry.getAnnotations()) {
			if (a.getAPICategory().equals(AnnotationCategory.FUNCTION_INFO)) {
				return a.getDescription();
			}
		}
		return "Unknown function";
	}

	
	
}
