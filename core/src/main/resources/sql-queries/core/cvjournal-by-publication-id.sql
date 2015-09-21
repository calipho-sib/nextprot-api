SELECT cv_journals.cv_id, cv_journals.journal_name, cv_journals.iso_abbrev, cv_journals.nlmid
FROM nextprot.cv_journals, nextprot.publications 
WHERE cv_journals.cv_id = publications.cv_journal_id and publications.resource_id = :publicationId