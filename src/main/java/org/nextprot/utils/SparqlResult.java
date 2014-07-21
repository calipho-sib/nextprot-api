package org.nextprot.utils;


public class SparqlResult implements KeyValueRepresentation{

	private int numRows;
	private String output;
	private String format;

	public int getNumRows() {
		return numRows;
	}

	public void setNumRows(int numRows) {
		this.numRows = numRows;
	}

	public String getOutput() {
		return output;
	}

	public void setOutput(String output) {
		this.output = output;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	@Override
	public String toKeyValueString() {
		return "numRows=" + numRows + ";";
	}

}
