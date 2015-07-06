select  to_char(max(internal_update_date), 'yyyy-MM-DD') as date  --apparently it is as simple as getting the max date
from nextprot.data_releases -- taken from ant script of Anne