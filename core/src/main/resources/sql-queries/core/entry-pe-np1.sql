select props.pe as pe
from nextprot.master_identifier_name_view props
where props.unique_name = :uniqueName