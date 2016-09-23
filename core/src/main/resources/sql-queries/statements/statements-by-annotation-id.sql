select * 
from nxflat.entry_mapped_statements ms 
where ms.annotation_id in (:ids)