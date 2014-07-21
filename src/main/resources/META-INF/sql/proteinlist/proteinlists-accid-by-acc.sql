select identifier_id 
from np_users.np_accessions 
where unique_name in (:accessions)