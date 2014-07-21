package org.nextprot.api.domain.export;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class ExportTemplateJsonSerializer extends JsonSerializer<ExportTemplate> {

	@Override
	public void serialize(ExportTemplate value, JsonGenerator generator, SerializerProvider provider) throws IOException, JsonProcessingException {
		
		generator.writeStartObject();
	    generator.writeFieldName("name");
	    generator.writeString(value.getTemplateName());
	    generator.writeEndObject();
	}


}
