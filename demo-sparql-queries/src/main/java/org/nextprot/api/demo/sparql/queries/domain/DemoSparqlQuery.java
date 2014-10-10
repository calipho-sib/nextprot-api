package org.nextprot.api.demo.sparql.queries.domain;



public class DemoSparqlQuery {

	private String query;
	private String title;
	private String tags;
	private String acs;

	public String getAcs() {
		return acs;
	}
	public void setAcs(String acs) {
		this.acs = acs;
	}
	public String getTags() {
		return tags;
	}
	public void setTags(String tags) {
		this.tags = tags;
	}
	public String getQuery() {
		return query;
	}
	public void setQuery(String query) {
		this.query = query;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String toString(){
		
		StringBuilder sb = new StringBuilder();
		sb.append("** query=");
		sb.append(query);
		sb.append(";\n");

		sb.append("** title=");
		sb.append(title);
		sb.append(";\n");

		sb.append("** tags=");
		sb.append(tags);
		sb.append(";\n");

		
		return sb.toString();

	}

}
