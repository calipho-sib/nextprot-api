select cv_name, description, url, release_version, to_char(internal_update_date, 'yyyy-MM-DD') as last_import
from nextprot.data_releases r
inner join nextprot.cv_data_releases cr on (r.cv_data_release_id = cr.cv_id)
where r.cv_status_id = 1
and cv_name in(:cvNames)
--or cv_name like '%PeptideAtlas%'
order by internal_update_date desc
