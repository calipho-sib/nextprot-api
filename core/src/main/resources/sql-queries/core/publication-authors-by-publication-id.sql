select pubauthor_id, last_name, fore_name, coalesce(suffix,'') as suffix, rank, publication_id, initials, is_person, is_editor
from nextprot.pubauthors where publication_id = (:publicationId)
order by rank
