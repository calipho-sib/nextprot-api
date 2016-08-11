select lower(ms.gene_name)
from mapped_statements ms
group by lower(ms.gene_name)
