select coalesce(props.ptm_num, -1) as ptmcnt, props.isoform_num as isocnt, coalesce(props.var_num, -1) as varcnt, coalesce(props.mutagenesis_num, -1) as mutcnt,
props.max_seq_length as maxlen, coalesce(props.disease, -1) as disease, coalesce(props.structure, -1) as structure, coalesce(props.proteomic, -1) as proteomic,
coalesce(props.expression_num, -1) as expression, coalesce(props.interaction_num, -1) as intcnt
from nextprot.master_identifier_name_view props
where props.unique_name = :uniqueName
