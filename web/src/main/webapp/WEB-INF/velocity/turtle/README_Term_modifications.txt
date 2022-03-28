REPORT DOCUMENT ON THE NEXTPROT RDF MODEL MODIFICATIONS FOR THE TERM and TERMINOLOGY CLASS
Author: Vincenzo Daponte.

The modifications have affected the velocity templates that produces the .ttl files referring to the Terms and Terminologies (CVs),
in particular the ones contributing to the production of the terms and the linking to the corresponding Terminology (or Controlled Vocabulary).

The velocity template files concerned by this modifications are:
- schema-annotation-list-old.ttl.vm
- schema-ontology-list.ttl.vm
- term.ttl.vm 

In particular, to provide the means to implement all the scenarios proposed three versions of the term.ttl.vm file have been created.

- term_1.ttl.vm contains the solution where each term is a Class (subClassOf :Term) and it is related to the Terminology (Cv subclassOf Terminology) through the "refersTo" relationship.
    The relationship between parent/child terms is expressed through the "childOf" relationship.

- term_2.ttl.vm contains the solution where each term is a Class (subClassOf :Term) and it is related to the Terminology (Cv subclassOf Terminology) through the "refersTo" relationship.
    The relationship between parent/child terms is expressed through the "subClassOf" relationship.

- term_3.ttl.vm contains the solution where each term is an Individual (rdf:type :Term) and it is related to the Terminology (Cv subclassOf Terminology) through the "refersTo" relationship.
    The relationship between parent/child terms is expressed through the "childOf" relationship.

The desired solution can be tested by renaming the corresponding file as term.ttl.vm and produce the terms.
The files "schema-annotation-list-old.ttl.vm" and "schema-ontology-list.ttl.vm" contain the modifications to create the missing classes and relationships ("Terminology", "refersTo")
and the declaration of the Controlled Vocabularies as subClassOf Terminology ("schema-ontology-list.ttl.vm").
