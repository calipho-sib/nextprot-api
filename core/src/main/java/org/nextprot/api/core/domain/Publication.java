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

	private static final long serialVersionUID = 3L;

	@ApiObjectField(description = "The neXtProt identifier of the publication")
	private Long id;

	@ApiObjectField(description = "The MD5 of the publication")
	private String md5;

	@ApiObjectField(description = "The title of the publication")
	private String title;

	@ApiObjectField(description = "The abstract text")
	private String abstractText;

	@ApiObjectField(description = "The type")
	private PublicationType publicationType;

	@ApiObjectField(description = "The publication date")
	private Date publicationDate;

	@ApiObjectField(description = "The publication date in text")
	private String textDate;

	@ApiObjectField(description = "The submission to db text (EMBL, PDB, ...")
	private String submission;

    @ApiObjectField(description = "The list of authors")
	private SortedSet<PublicationAuthor> authors;

	@ApiObjectField(description = "The associated cross references")
	private Set<DbXref> dbXrefs;

	private PublicationResourceLocator publicationResourceLocator;

    public boolean isLocalizable() {
		return publicationResourceLocator != null;
	}

	public boolean isLocatedInScientificJournal() {
		return isLocalizable() && publicationResourceLocator instanceof JournalResourceLocator;
	}

	/**
	 * @return true if found in a edited volume book
     */
	public boolean isLocatedInEditedVolumeBook() {
		return isLocalizable() && publicationResourceLocator instanceof EditedVolumeBookResourceLocator;
	}

	/**
	 * @return true if found in a standard book (a journal or a edited volume book)
     */
	public boolean isLocalizableInBookMedium() {
		return isLocalizable() && publicationResourceLocator instanceof BookResourceLocator;
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

	public boolean hasPublicationDate() {
		return publicationDate != null;
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
			return ((JournalResourceLocator) publicationResourceLocator).getVolume();
		else
			return "";
	}

	public String getIssue() {

		if (isLocatedInScientificJournal())
			return ((JournalResourceLocator) publicationResourceLocator).getIssue();
		else
			return "";
	}

	public String getFirstPage() {

		if (isLocalizableInBookMedium())
			return ((BookResourceLocator) publicationResourceLocator).getFirstPage();
		else
			return "";
	}

	public String getLastPage() {

		if (isLocalizableInBookMedium())
			return ((BookResourceLocator) publicationResourceLocator).getLastPage();
		else
			return "";
	}

	public String getPublisherName() {
		if (isLocatedInEditedVolumeBook())
			return ((EditedVolumeBookResourceLocator) publicationResourceLocator).getPublisher();
		return "";
	}

	public String getPublisherCity() {
		if (isLocatedInEditedVolumeBook())
			return ((EditedVolumeBookResourceLocator) publicationResourceLocator).getCity();
		return "";
	}

	public PublicationType getPublicationType() {
		return publicationType;
	}

	public void setPublicationType(PublicationType publicationType) {
		this.publicationType = publicationType;
	}

	public String getTextDate() {
		return textDate;
	}

	public void setTextDate(String textDate) {
		this.textDate = textDate;
	}

	public JournalResourceLocator getJournalResourceLocator() {

		return isLocatedInScientificJournal() ? (JournalResourceLocator) publicationResourceLocator : null;
	}

	public PublicationResourceLocator getPublicationResourceLocator() {
		return publicationResourceLocator;
	}

	public String getPublicationLocatorName() {
		return isLocalizable() ? publicationResourceLocator.getName() : null;
	}

	public void setJournalResourceLocator(JournalResourceLocator journalLocation, String volume, String issue, String firstPage, String lastPage) {

		Preconditions.checkNotNull(journalLocation);

		journalLocation.setFirstPage(firstPage);
		journalLocation.setLastPage(lastPage);
		journalLocation.setVolume(volume);
		journalLocation.setIssue(issue);

		this.publicationResourceLocator = journalLocation;
	}

	public void setEditedVolumeBookLocation(String name, String publisher, String city, String firstPage, String lastPage) {

		EditedVolumeBookResourceLocator book = new EditedVolumeBookResourceLocator();

		book.setName(name);
		book.setPublisher(publisher);
		book.setCity(city);
		book.setFirstPage(firstPage);
		book.setLastPage(lastPage);

		this.publicationResourceLocator = book;
	}

	public void setOnlineResourceLocation(String name, String url) {

		WebPublicationPage webPage = new WebPublicationPage();
		webPage.setName(name);
		webPage.setUrl(url);

		this.publicationResourceLocator = webPage;
	}

    public void setThesisResourceLocation(String institute, String country) {

        ThesisResourceLocator locator = new ThesisResourceLocator();
        locator.setInstitute(institute);
        locator.setCountry(country);

        this.publicationResourceLocator = locator;
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
		return isLocatedInEditedVolumeBook() && ((EditedVolumeBookResourceLocator) publicationResourceLocator).hasEditors();
	}

	public Set<PublicationAuthor> getEditors() {
		if (isLocatedInEditedVolumeBook())
			return ((EditedVolumeBookResourceLocator) publicationResourceLocator).getEditors();
		return Collections.emptySet();
	}

	public void setEditors(SortedSet<PublicationAuthor> editors) {

		if (isLocatedInEditedVolumeBook()) {
			((EditedVolumeBookResourceLocator) publicationResourceLocator).addEditors(editors);
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

	@Override
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
		sb.append(this.publicationResourceLocator);
		sb.append("\n");
		sb.append("authorsCnt=");
		sb.append((this.authors != null) ? this.authors.size() : "null");
		sb.append("\n");
		// date is not defined for online publication type
		if (hasPublicationDate()) {
			sb.append("date=");
			sb.append(this.publicationDate.toString());
			sb.append("\n");
		}

		return sb.toString();
	}
}
