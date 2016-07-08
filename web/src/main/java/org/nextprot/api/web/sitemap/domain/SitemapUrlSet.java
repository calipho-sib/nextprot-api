package org.nextprot.api.web.sitemap.domain;

import java.util.Set;
import java.util.TreeSet;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="urlset")
public class SitemapUrlSet  {
	
	private Set<SitemapUrl> urls=new TreeSet<SitemapUrl>();
	
	@XmlAttribute
	public String getXmlns() {
		return "http://www.sitemaps.org/schemas/sitemap/0.9";
	}
	
	@XmlElement(name="url")
	public Set<SitemapUrl> getUrls() {
		return urls;
	}
	
	public void add(SitemapUrl siturl) {
		urls.add(siturl);
	}
}
