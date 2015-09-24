package org.nextprot.api.web.domain;

import java.text.SimpleDateFormat;
import java.util.Date;

public class NextProtNews implements Comparable<NextProtNews>{
	
	private String title;
	private Date publicationDate;
	private String url;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getPublicationDateFormatted() {
		return (new SimpleDateFormat("MMM dd, yyyy")).format(publicationDate);
	}
	
	public Date getPublicationDate() {
		return publicationDate;
	}

	public void setPublicationDate(Date publicationDate) {
		this.publicationDate = publicationDate;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public int compareTo(NextProtNews o) {
		return this.getPublicationDate().compareTo(o.getPublicationDate());
	}

}
