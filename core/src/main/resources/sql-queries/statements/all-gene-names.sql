select lower(ms.gene_name)
from nxflat.mapped_statements ms
group by lower(ms.gene_name)
