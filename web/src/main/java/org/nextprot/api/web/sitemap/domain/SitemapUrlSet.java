package org.nextprot.api.web.sitemap.domain;

import java.util.Set;
import java.util.TreeSet;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="urls")
public class SitemapUrlSet  {
	
	private Set<SitemapUrl> urls=new TreeSet<SitemapUrl>();
	
	@XmlElement(name="url")
	public Set<SitemapUrl> getUrls() {
		return urls;
	}
	
	public void add(SitemapUrl siturl) {
		urls.add(siturl);
	}
}
