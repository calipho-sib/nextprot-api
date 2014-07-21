package org.nextprot.api.domain;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;
import java.util.SortedSet;

import org.jsondoc.core.annotation.ApiObject;
import org.jsondoc.core.annotation.ApiObjectField;

@ApiObject(name = "publication", description = "A publication")
public class Publication implements Serializable{

	private static final long serialVersionUID = 4404147147281845675L;

	@ApiObjectField(description = "The neXtProt identifier of the publication")
	private Long id;

	@ApiObjectField(description = "The MD5 of the publication")
	private String md5;
	
	@ApiObjectField(description = "The title of the publication")
	private String title;

	@ApiObjectField(description = "The asbtract text")
	private String abstractText;

	@ApiObjectField(description = "The journal volume")
	private String volume;

	@ApiObjectField(description = "The journal issue")
	private String issue;

	@ApiObjectField(description = "The first page")
	private String firstPage;

	@ApiObjectField(description = "The last page")
	private String lastPage;

	@ApiObjectField(description = "The type")
	private String publicationType;

	@ApiObjectField(description = "The publication date")
	private Date publicationDate;

	@ApiObjectField(description = "The publication date in text")
	private String textDate;

	@ApiObjectField(description = "The journal")
	protected CvJournal cvJournal;

	@ApiObjectField(description = "The list of authors")
	protected SortedSet<PublicationAuthor> authors;

	@ApiObjectField(description = "The associated cross references")
	protected Set<DbXref> dbXrefs;
	
	private final SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");  
	

	public long getPublicationId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getMD5() {
		return md5;
	}

	public void setMD5(String md5) {
		this.md5 = md5;
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAbstractText() {
		return abstractText;
	}

	public void setAbstractText(String abstractText) {
		this.abstractText = abstractText;
	}

	public Date getPublicationDate() {
		return publicationDate;
	}

	public String getPublicationYear() {
		return this.yearFormat.format(this.publicationDate);
	}
	
	public void setPublicationDate(Date publicationDate) {
		this.publicationDate = publicationDate;
	}

	public String getVolume() {
		return volume;
	}

	public void setVolume(String volume) {
		this.volume = volume;
	}

	public String getIssue() {
		return issue;
	}

	public void setIssue(String issue) {
		this.issue = issue;
	}

	public String getFirstPage() {
		return firstPage;
	}

	public void setFirstPage(String firstPage) {
		this.firstPage = firstPage;
	}

	public String getLastPage() {
		return lastPage;
	}

	public void setLastPage(String lastPage) {
		this.lastPage = lastPage;
	}
	
	public String getPublicationType() {
		return publicationType;
	}

	public void setPublicationType(String publicationType) {
		this.publicationType = publicationType;
	}

	public String getTextDate() {
		return textDate;
	}

	public void setTextDate(String textDate) {
		this.textDate = textDate;
	}
	
	public CvJournal getCvJournal() {
		return cvJournal;
	}

	public void setCvJournal(CvJournal cvJournal) {
		this.cvJournal = cvJournal;
	}

	public SortedSet<PublicationAuthor> getAuthors() {
		return authors;
	}

	public void setAuthors(SortedSet<PublicationAuthor> authors) {
		this.authors = authors;
	}

	public Set<DbXref> getDbXrefs() {
		return dbXrefs;
	}

	public void setDbXrefs(Set<DbXref> dbXrefs) {
		this.dbXrefs = dbXrefs;
	}


}
