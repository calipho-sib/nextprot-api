SELECT property_value as pe
FROM identifier_properties
WHERE cv_property_name_id = 10
AND identifier_id = (
  SELECT identifier_id
  FROM nextprot.sequence_identifiers
  WHERE unique_name = :uniqueName
)