select *
from nextprot.stats_view
where sort_order > 0 and tag is not null and tag != '' -- untagged and unsorted records are not shown in NP2, thus not in NP2 
order by sort_order