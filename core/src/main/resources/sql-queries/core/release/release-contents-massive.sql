select cv_name, description, url, release_version, to_char(internal_update_date, 'yyyy-MM-DD') as last_import
from nextprot.data_releases r
inner join nextprot.cv_data_releases cr on (r.cv_data_release_id = cr.cv_id)
where r.cv_status_id = 1
and lower(cv_name) like '%massive%'
order by internal_update_date desc
limit 1 -- take only the most recent