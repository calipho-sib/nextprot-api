package org.nextprot.api.web.sitemap.domain;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class SitemapUrl implements Comparable<SitemapUrl> {

	private String loc; // full URL: https://...
	private String changefreq; // values: always hourly daily weekly monthly
								// yearly never
	private String lastmod; // format = yyyyy-MM-dd
	private String priority; // values: [0.1 - 1.0], default = 0.5

	public SitemapUrl() {
		super();
	}
	
	public SitemapUrl(String loc) {
		this.loc = loc;
		this.changefreq = "weekly";
		this.lastmod = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
		this.priority = "0.5";
	}

	public String getLoc() {
		return loc;
	}

	public void setLoc(String loc) {
		this.loc = loc;
	}

	public String getChangefreq() {
		return changefreq;
	}

	public void setChangefreq(String changefreq) {
		this.changefreq = changefreq;
	}

	public String getLastmod() {
		return lastmod;
	}

	public void setLastmod(String lastmod) {
		this.lastmod = lastmod;
	}

	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	@Override
	public int compareTo(SitemapUrl o) {
		return this.getLoc().compareTo(o.getLoc());
	}

	@Override
	public int hashCode() {
		return this.getLoc().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof SitemapUrl))
			return false;
		if (obj == this)
			return true;
		SitemapUrl other = (SitemapUrl) obj;
		return this.getLoc().equals(other.getLoc());
	}

}
