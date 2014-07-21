package org.nextprot.api.domain.export;

public interface ExportTemplate {

	public String getTemplateName();
	public String getVelocityTemplateName();

	public String getHeader();
	public String getFooter();

}
