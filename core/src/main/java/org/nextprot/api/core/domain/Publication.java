package org.nextprot.api.core.domain;

import org.jsondoc.core.annotation.ApiObject;
import org.jsondoc.core.annotation.ApiObjectField;
import org.nextprot.api.commons.utils.DateFormatter;
import org.nextprot.api.core.domain.publication.*;

import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.Set;
import java.util.SortedSet;

@ApiObject(name = "publication", description = "A publication")
public class Publication implements Serializable{

	private static final long serialVersionUID = 4404147147281845675L;

	@ApiObjectField(description = "The neXtProt identifier of the publication")
	private Long id;

	@ApiObjectField(description = "The MD5 of the publication")
	private String md5;

	@ApiObjectField(description = "The title of the publication")
	private String title;

	@ApiObjectField(description = "The abstract text")
	private String abstractText;

	@ApiObjectField(description = "The type")
	private String publicationType;

	@ApiObjectField(description = "The publication date")
	private Date publicationDate;

	@ApiObjectField(description = "The publication date in text")
	private String textDate;

	@ApiObjectField(description = "The submission to db text (EMBL, PDB, ...")
	private String submission;

	@ApiObjectField(description = "Publications related to 15 entries or more")
	private Boolean isLargeScale;

	@ApiObjectField(description = "Curated Publications")
	private Boolean isCurated;

	// TODO: reassess the way we define 'curared/computed' and get rid of the 'limit 1' in publication-by-ressource.sql
	@ApiObjectField(description = "Computed Publications")
	private Boolean isComputed;

	@ApiObjectField(description = "The list of authors")
	protected SortedSet<PublicationAuthor> authors;

	@ApiObjectField(description = "The associated cross references")
	protected Set<DbXref> dbXrefs;

	private PublicationMedium publicationMedium;

	public boolean isLocatedInPublicationMedium() {
		return publicationMedium != null;
	}

	public boolean isPublishedInScientificJournal() {
		return isLocatedInPublicationMedium() && publicationMedium instanceof Journal;
	}

	public boolean isPublishedInEditedVolumeBook() {
		return isLocatedInPublicationMedium() && publicationMedium instanceof EditedVolumeBook;
	}

	public boolean isLocalizableInBookMedium() {
		return isLocatedInPublicationMedium() && publicationMedium instanceof BookMediumLocator;
	}

	public Boolean getIsLargeScale() {
		return isLargeScale;
	}

	public void setIsLargeScale(Boolean isLargeScale) {
		this.isLargeScale = isLargeScale;
	}

	public Boolean getIsCurated() {
		return isCurated;
	}

	public void setIsCurated(Boolean isCurated) {
		this.isCurated = isCurated;
	}

	public Boolean getIsComputed() {
		return isComputed;
	}

	public void setIsComputed(Boolean isComputed) {
		this.isComputed = isComputed;
	}

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

	public String getSubmission() {
		return submission;
	}

	public void setSubmission(String submission) {
		this.submission = submission;
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

		if (publicationDate == null)
			return null;

		return new DateFormatter().format(publicationDate, DateFormatter.YEAR_PRECISION);
	}
	
	public void setPublicationDate(Date publicationDate) {
		this.publicationDate = publicationDate;
	}

	public String getVolume() {

		if (isPublishedInScientificJournal())
			return ((Journal) publicationMedium).getLocation().getVolume();
		else
			return "";
	}

	public String getIssue() {

		if (isPublishedInScientificJournal())
			return ((Journal) publicationMedium).getLocation().getIssue();
		else
			return "";
	}

	public String getFirstPage() {

		if (isLocalizableInBookMedium())
			return ((BookMediumLocator) publicationMedium).getLocation().getFirstPage();
		else
			return "";
	}

	public String getLastPage() {

		if (isLocalizableInBookMedium())
			return ((BookMediumLocator) publicationMedium).getLocation().getLastPage();
		else
			return "";
	}

	public String getPublisherName() {
		if (isPublishedInEditedVolumeBook())
			return ((EditedVolumeBook) publicationMedium).getPublisher();
		return "";
	}

	public String getPublisherCity() {
		if (isPublishedInEditedVolumeBook())
			return ((EditedVolumeBook) publicationMedium).getCity();
		return "";
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

	public Journal getJournal() {

		return (isPublishedInScientificJournal()) ? (Journal) publicationMedium : null;
	}

	public PublicationMedium getPublicationMedium() {
		return publicationMedium;
	}

	public String getPublicationMediumName() {
		return (isLocatedInPublicationMedium()) ? publicationMedium.getName() : null;
	}

	public void setJournal(Journal journal, String volume, String issue, String firstPage, String lastPage) {

		JournalLocation location = new JournalLocation();

		location.setFirstPage(firstPage);
		location.setLastPage(lastPage);
		location.setVolume(volume);
		location.setIssue(issue);

		journal.setLocation(location);

		this.publicationMedium = journal;
	}

	public void setEditedVolumeBook(String name, String publisher, String city, String firstPage, String lastPage) {

		EditedVolumeBook book = new EditedVolumeBook(PublicationType.valueOfName(publicationType));
		book.setName(name);
		book.setPublisher(publisher);
		book.setCity(city);

		BookLocation location = new BookLocation();

		location.setFirstPage(firstPage);
		location.setLastPage(lastPage);

		book.setLocation(location);

		this.publicationMedium = book;
	}

	public void setOnlineResource(String name, String url) {

		WebPublicationPage webPage = new WebPublicationPage(PublicationType.valueOfName(publicationType));
		webPage.setName(name);
		webPage.setUrl(url);

		this.publicationMedium = webPage;
	}

	public SortedSet<PublicationAuthor> getAuthors() {
		return authors;
	}

	public void setAuthors(SortedSet<PublicationAuthor> authors) {
		this.authors = authors;
	}

	public boolean hasEditors() {
		return isPublishedInEditedVolumeBook() && ((EditedVolumeBook) publicationMedium).hasEditors();
	}

	public Set<PublicationAuthor> getEditors() {
		if (isPublishedInEditedVolumeBook())
			return ((EditedVolumeBook) publicationMedium).getEditors();
		return Collections.emptySet();
	}

	public void setEditors(SortedSet<PublicationAuthor> editors) {

		if (isPublishedInEditedVolumeBook()) {
			((EditedVolumeBook) publicationMedium).addEditors(editors);
		}
	}

	public Set<DbXref> getDbXrefs() {
		return dbXrefs;
	}

	public void setDbXrefs(Set<DbXref> dbXrefs) {
		this.dbXrefs = dbXrefs;
	}

	public String toString() {
		
		StringBuilder sb = new StringBuilder();
		
		sb.append("id=");
		sb.append(this.id);
		sb.append("\n");
		sb.append("md5=");
		sb.append(this.md5);
		sb.append("\n");
		sb.append("title=");
		sb.append(this.title);
		sb.append("\n");
		sb.append("submission=");
		sb.append((this.submission != null) ? this.submission : "null");
		sb.append("\n");
		sb.append("volume=");
		sb.append(getVolume());
		sb.append("; issue=");
		sb.append(getIssue());
		sb.append("\n");
		sb.append("pub_type=");
		sb.append(this.publicationType);
		sb.append("\n");
		sb.append("journal=");
		sb.append(this.publicationMedium);
		sb.append("\n");
		sb.append("authorsCnt=");
		sb.append((this.authors != null) ? this.authors.size() : "null");
		sb.append("\n");

		return sb.toString();

	}
}
