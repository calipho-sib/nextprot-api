select distinct nextprot.cv_term_categories.cv_api_name
from nextprot.cv_terms
inner join nextprot.cv_term_categories on (nextprot.cv_terms.cv_category_id = nextprot.cv_term_categories.cv_id)
where nextprot.cv_terms.cv_status_id=1 
order by nextprot.cv_term_categories.cv_api_name