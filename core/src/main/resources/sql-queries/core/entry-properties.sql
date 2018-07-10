select props.max_seq_length as maxlen,
  coalesce(props.disease, -1) as disease,
  coalesce(props.structure, -1) as structure,
  coalesce(props.expression_num, -1) as expression,
  coalesce(props.interaction_num, -1) as intcnt
from nextprot.master_identifier_name_view props
where props.unique_name = :uniqueName
