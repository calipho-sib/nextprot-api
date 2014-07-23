package org.nextprot.api.core.service.export.format;

public interface ExportTemplate {

	public String getTemplateName();
	public String getVelocityTemplateName();

	public String getHeader();
	public String getFooter();

}
