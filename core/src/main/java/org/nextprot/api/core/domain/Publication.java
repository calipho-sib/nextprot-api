package org.nextprot.api.core.domain;

import com.google.common.base.Preconditions;
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

	private PublicationLocation publicationLocation;

	public boolean isLocalizable() {
		return publicationLocation != null;
	}

	public boolean isLocatedInScientificJournal() {
		return isLocalizable() && publicationLocation instanceof JournalLocation;
	}

	/**
	 * @return true if found in a edited volume book
     */
	public boolean isLocatedInEditedVolumeBook() {
		return isLocalizable() && publicationLocation instanceof EditedVolumeBookLocation;
	}

	/**
	 * @return true if found in a standard book (a journal or a edited volume book)
     */
	public boolean isLocalizableInBookMedium() {
		return isLocalizable() && publicationLocation instanceof BookLocation;
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

	public boolean hasTitle() {
		return title != null && !title.isEmpty();
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

		if (isLocatedInScientificJournal())
			return ((JournalLocation) publicationLocation).getVolume();
		else
			return "";
	}

	public String getIssue() {

		if (isLocatedInScientificJournal())
			return ((JournalLocation) publicationLocation).getIssue();
		else
			return "";
	}

	public String getFirstPage() {

		if (isLocalizableInBookMedium())
			return ((BookLocation) publicationLocation).getFirstPage();
		else
			return "";
	}

	public String getLastPage() {

		if (isLocalizableInBookMedium())
			return ((BookLocation) publicationLocation).getLastPage();
		else
			return "";
	}

	public String getPublisherName() {
		if (isLocatedInEditedVolumeBook())
			return ((EditedVolumeBookLocation) publicationLocation).getPublisher();
		return "";
	}

	public String getPublisherCity() {
		if (isLocatedInEditedVolumeBook())
			return ((EditedVolumeBookLocation) publicationLocation).getCity();
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

	public JournalLocation getJournalLocation() {

		return (isLocatedInScientificJournal()) ? (JournalLocation) publicationLocation : null;
	}

	public PublicationLocation getPublicationLocation() {
		return publicationLocation;
	}

	public String getPublicationLocationName() {
		return (isLocalizable()) ? publicationLocation.getName() : null;
	}

	public void setJournalLocation(JournalLocation journalLocation, String volume, String issue, String firstPage, String lastPage) {

		Preconditions.checkNotNull(journalLocation);

		journalLocation.setFirstPage(firstPage);
		journalLocation.setLastPage(lastPage);
		journalLocation.setVolume(volume);
		journalLocation.setIssue(issue);

		this.publicationLocation = journalLocation;
	}

	public void setEditedVolumeBookLocation(String name, String publisher, String city, String firstPage, String lastPage) {

		EditedVolumeBookLocation book = new EditedVolumeBookLocation(PublicationType.valueOfName(publicationType));

		book.setName(name);
		book.setPublisher(publisher);
		book.setCity(city);
		book.setFirstPage(firstPage);
		book.setLastPage(lastPage);

		this.publicationLocation = book;
	}

	public void setOnlineResourceLocation(String name, String url) {

		WebPublicationPage webPage = new WebPublicationPage(PublicationType.valueOfName(publicationType));
		webPage.setName(name);
		webPage.setUrl(url);

		this.publicationLocation = webPage;
	}

	public boolean hasAuthors() {
		return authors != null && !authors.isEmpty();
	}

	public SortedSet<PublicationAuthor> getAuthors() {
		return authors;
	}

	public void setAuthors(SortedSet<PublicationAuthor> authors) {
		this.authors = authors;
	}

	public boolean hasEditors() {
		return isLocatedInEditedVolumeBook() && ((EditedVolumeBookLocation) publicationLocation).hasEditors();
	}

	public Set<PublicationAuthor> getEditors() {
		if (isLocatedInEditedVolumeBook())
			return ((EditedVolumeBookLocation) publicationLocation).getEditors();
		return Collections.emptySet();
	}

	public void setEditors(SortedSet<PublicationAuthor> editors) {

		if (isLocatedInEditedVolumeBook()) {
			((EditedVolumeBookLocation) publicationLocation).addEditors(editors);
		}
	}

	public boolean hasDbXrefs() {
		return dbXrefs != null && !dbXrefs.isEmpty();
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
		sb.append(this.publicationLocation);
		sb.append("\n");
		sb.append("authorsCnt=");
		sb.append((this.authors != null) ? this.authors.size() : "null");
		sb.append("\n");

		return sb.toString();

	}
}
