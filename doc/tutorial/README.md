#How to rock in SDCSB Hackaton using neXtProt

![Hackaton](https://dl.dropboxusercontent.com/u/2037400/hackaton.png "hackaton" )

Note: You will need a password to access this alpha version (if you are at Hackathon) someone should have given it to you.

To give you a few concrete cases of what you can do with the advanced search engine (SPARQL based):
* Search the entries with a sequence containing "FF*QYD" like [this](https://search.nextprot.org/proteins/search?mode=advanced&order=desc&sparql=%23Q93%20with%20a%20sequence%20containing%20%22FF*QYD%22%20%20is%20any%20peptide%20of%20any%20length%0A%3Fentry%20:isoform%20%2F%20:sequence%20%2F%20:chain%20%3Fchain%20.%0A%09%20%20%20filter(regex(%3Fchain,%20%22FF.%2BQYD%22))&rows=50)
* Search for entries containing peptide sequence with Isoleucine/Leucine like [this](https://search.nextprot.org/proteins/search?mode=advanced&order=desc&sparql=%23Search%20for%20entry%20containing%20peptide%20sequence%20with%20Isoleucine%2FLeucine%0A%23Test%20peptide%20from%20insulin%0ASELECT%20distinct%20%3Fentry%0AWHERE%20%7B%0A%20%20%3Fentry%20rdf:type%20:Entry%20.%0A%20%20%3Fentry%20:isoform%20%2F%20:sequence%20%2F%20:chain%20%3Fchain%20.%0A%20%20filter%20(%20regex(%3Fchain,%20%22FVNQH%5BI%7CL%5DCGSH%22%5E%5Exsd:string))%0A%7D&rows=50)
* Search proteins with a variant of the type "C->"and linked to a disease, like [this](https://search.nextprot.org/proteins/search?mode=advanced&order=desc&sparql=%23Q048%20with%20a%20variant%20of%20the%20type%20%22C-%3E%22%20(Cys%20to%20anything%20else)%20and%20the%20variant%20is%20linked%20to%20a%20disease%0A%3Fentry%20:isoform%20%2F%20:variant%20%3Fvariant%20.%0A%3Fvariant%20:original%20%22C%22%5E%5Exsd:string%3B:variation%20%3Fv.%0A%3Fvariant%20:disease%20%3FsomeDisease%20.&rows=50).
* You can search for proteins located on chromosome 2 and having known variants on a phosphotyrosine like [this](https://search.nextprot.org/proteins/search?mode=advanced&order=desc&sparql=%23Q097%20located%20on%20chromosome%202%20and%20having%20known%20variants%20on%20a%20phosphotyrosine%0A%3Fentry%20:isoform%20%3Fiso%20%3B%0A%20%20%20%20%20%20%20:gene%20%2F%20:chromosome%20%222%22%5E%5Exsd:string%20.%0A%20%20%3Fiso%20:modifiedResidue%20%20%3Fptm%20%3B%0A%20%20%20%20%20%20%20:variant%20%3Fvar%20.%0A%20%20%3Fptm%20:in%20term:PTM-0255%20%3B%20%23phosphotyrosine%0A%20%20%20%20%20%20%20:in%20%2F%20rdfs:label%20%3FptmName%20%3B%0A%20%20%20%20%20%20%20:start%20%3Fposition%20.%0A%20%20%3Fvar%20rdf:type%20:SequenceVariant%20%3B%0A%20%20%20%20%20%20%20:start%20%3Fposition%20.&rows=50).
* and search way more things... look at the [RDF help](https://search.nextprot.org/rdf-help) to get familiarised with the vocabulary 

After playing a bit with the interface you will likely want to access the search engine programatically, for this you need to access the sparql endpoint directly with the good prefixes and the query (after testing in the search engine), like [this](https://api.nextprot.org/sparql/?query=PREFIX+rdf:+%3Chttp:%2F%2Fwww.w3.org%2F1999%2F02%2F22-rdf-syntax-ns%23%3E%0APREFIX+rdfs:+%3Chttp:%2F%2Fwww.w3.org%2F2000%2F01%2Frdf-schema%23%3E%0APREFIX+owl:+%3Chttp:%2F%2Fwww.w3.org%2F2002%2F07%2Fowl%23%3E%0APREFIX+dc:+%3Chttp:%2F%2Fpurl.org%2Fdc%2Felements%2F1.1%2F%3E%0APREFIX+dcterms:+%3Chttp:%2F%2Fpurl.org%2Fdc%2Fterms%2F%3E%0APREFIX+foaf:+%3Chttp:%2F%2Fxmlns.com%2Ffoaf%2F0.1%2F%3E%0APREFIX+sim:+%3Chttp:%2F%2Fpurl.org%2Fontology%2Fsimilarity%2F%3E%0APREFIX+mo:+%3Chttp:%2F%2Fpurl.org%2Fontology%2Fmo%2F%3E%0APREFIX+ov:+%3Chttp:%2F%2Fopen.vocab.org%2Fterms%2F%3E%0APREFIX+xsd:+%3Chttp:%2F%2Fwww.w3.org%2F2001%2FXMLSchema%23%3E%0APREFIX+:+%3Chttp:%2F%2Fnextprot.org%2Frdf%23%3E%0APREFIX+entry:+%3Chttp:%2F%2Fnextprot.org%2Frdf%2Fentry%2F%3E%0APREFIX+isoform:+%3Chttp:%2F%2Fnextprot.org%2Frdf%2Fisoform%2F%3E%0APREFIX+annotation:+%3Chttp:%2F%2Fnextprot.org%2Frdf%2Fannotation%2F%3E%0APREFIX+evidence:+%3Chttp:%2F%2Fnextprot.org%2Frdf%2Fevidence%2F%3E%0APREFIX+xref:+%3Chttp:%2F%2Fnextprot.org%2Frdf%2Fxref%2F%3E%0APREFIX+publication:+%3Chttp:%2F%2Fnextprot.org%2Frdf%2Fpublication%2F%3E%0APREFIX+term:+%3Chttp:%2F%2Fnextprot.org%2Frdf%2Fterminology%2F%3E%0APREFIX+gene:+%3Chttp:%2F%2Fnextprot.org%2Frdf%2Fgene%2F%3E%0APREFIX+source:+%3Chttp:%2F%2Fnextprot.org%2Frdf%2Fsource%2F%3E%0APREFIX+db:+%3Chttp:%2F%2Fnextprot.org%2Frdf%2Fdb%2F%3E%0APREFIX+context:+%3Chttp:%2F%2Fnextprot.org%2Frdf%2Fcontext%2F%3E%0A%0Aselect%20distinct%20%3Fentry%20where%7B%0A%20%3Fentry%20:isoform%2F:localisation%2F:in%2F:childOf%20term:SL-0173%20.%0A%20filter%20not%20exists%20%7B%0A%20%20%20%3Fentry%20:isoform%20%2F%20:processing%20%3Ftp%20.%0A%20%20%20%3Ftp%20a%20:TransitPeptide%20.%0A%20%20%20%3Ftp%20rdfs:comment%20%3Fcom.%0A%20%20%20filter%20(contains%20(%3Fcom,%27Mitochondrion%27))%0A%20%20%7D%0A%7D). 

If you [decode the above URL](http://meyerweb.com/eric/tools/dencoder/) you will see the following:

1) You have the base URL
```
https://api.nextprot.org/sparql/?query=
```

2) The SPARQL prefixes
```
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX owl: <http://www.w3.org/2002/07/owl#>
PREFIX dc: <http://purl.org/dc/elements/1.1/>
PREFIX dcterms: <http://purl.org/dc/terms/>
PREFIX foaf: <http://xmlns.com/foaf/0.1/>
PREFIX sim: <http://purl.org/ontology/similarity/>
PREFIX mo: <http://purl.org/ontology/mo/>
PREFIX ov: <http://open.vocab.org/terms/>
PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>
PREFIX : <http://nextprot.org/rdf#>
PREFIX entry: <http://nextprot.org/rdf/entry/>
PREFIX name: <http://nextprot.org/rdf/name/>
PREFIX isoform: <http://nextprot.org/rdf/isoform/>
PREFIX annotation: <http://nextprot.org/rdf/annotation/>
PREFIX evidence: <http://nextprot.org/rdf/evidence/>
PREFIX xref: <http://nextprot.org/rdf/xref/>
PREFIX publication: <http://nextprot.org/rdf/publication/>
PREFIX term: <http://nextprot.org/rdf/terminology/>
PREFIX gene: <http://nextprot.org/rdf/gene/>
PREFIX source: <http://nextprot.org/rdf/source/>
PREFIX db: <http://nextprot.org/rdf/db/>
PREFIX context: <http://nextprot.org/rdf/context/>
```

3) The SPARQL query
```
select distinct ?entry where{
 ?entry :isoform/:localisation/:in/:childOf term:SL-0173 .
 filter not exists {
   ?entry :isoform / :processing ?tp .
   ?tp a :TransitPeptide .
   ?tp rdfs:comment ?com.
   filter (contains (?com,'Mitochondrion'))
  }
}
```


After parsing the result and identifying the proteins of interest you can query the data of these proteins using our REST API and you can export a whole entry in different formats:
* XML - [https://api.nextprot.org/entry/NX_P01308.xml]
* JSON - [https://api.nextprot.org/entry/NX_P01308.json]
* TTL - [https://api.nextprot.org/entry/NX_P01308.ttl]

Also you can export only a subpart of the entries, for instance only the sequences:
* [https://api.nextprot.org/entry/NX_P01308/protein-sequence.xml]  (XML)

Look at [the api documentation](https://search.nextprot.org/api-info) for more export options.

Be aware that this is development version and a more stable version will be released in beginning of the year 2015. But it might be nice for you to use it in hakkaton since it has a powerful search engine.
