package org.nextprot.api.domain.export;

import java.util.List;


public class ExportFormat {
	
	private String name;
	private String extension;
	private List<?> templates;
	
	public ExportFormat(String name, String extension, List<?> templates) {
		super();
		this.name = name;
		this.extension = extension;
		this.templates = templates;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getExtension() {
		return extension;
	}
	public void setExtension(String extension) {
		this.extension = extension;
	}
	public List<?> getTemplates() {
		return templates;
	}
	public void setTemplates(List<ExportTemplate> templates) {
		this.templates = templates;
	}
	
}
