#Test a term
curl -H "Content-Type: application/json" -X POST http://localhost:8080/nextprot-api/search/term.json -d '{"quality": "gold", "query":"adenocarcinoma"}'
