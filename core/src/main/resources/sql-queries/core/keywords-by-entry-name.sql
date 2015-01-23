select x.accession, t.cv_name as keyword_name
from nextprot.sequence_identifiers si,  
nextprot.annotations a, 
nextprot.cv_terms t,  
nextprot.cv_term_properties prop, 
nextprot.db_xrefs x 
where si.unique_name = :uniqueName 
and si.identifier_id = a.identifier_id 
and x.resource_id = t.db_xref_id 
and a.cv_term_id = t.cv_id 
and t.cv_category_id = 13 -- UniprotKW
and t.cv_id = prop.cv_term_id  
and x.accession not in ('KW-0181', 'KW-1185') -- Complete proteome KW-0181 and Reference proteome KW-1185 are banned because they are irrelavant for nextProt (Amos said) 
and prop.property_name = 'Category' 
and prop.property_value in ('Biological process','Cellular component', 'Coding sequence diversity', 'Disease', 'Domain', 'Developmental stage', 'Ligand','Molecular function', 'PTM', 'Technical term')

/*
Amos email from (Thu, Jul 11, 2013 at 12:11 PM)

Tres bonne question:

En effet on ne voulait pas le KW "Complete proteome" car il est inutile dans le contexte de NP.
Et du coup, il y a eu un "nouveau" KW qui est apparu depuis la création de NP et que l'on a oublié de demander de bannir.
Il  s'agit de:

ID   Reference proteome.
AC   KW-1185

A+
*/