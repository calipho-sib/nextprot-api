package org.nextprot.api.web.xstream.converters;

import org.nextprot.api.core.domain.annotation.Annotation;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

@NextprotConverter
public class AnnotationConverter implements Converter {

	public AnnotationConverter() {
		super();
	}

	public boolean canConvert(@SuppressWarnings("rawtypes") Class clazz) {
		return Annotation.class.isAssignableFrom(clazz);
	}

	public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {

		
		Annotation annotation = (Annotation) value;
		
		writer.startNode("unique-name");
		writer.setValue(annotation.getUniqueName());
		writer.endNode();


		if(annotation.getCvTermAccessionCode() != null){
			writer.startNode("cvterm-accession-code");
			writer.setValue(annotation.getCvTermAccessionCode());
			writer.endNode();
		}

		
		writer.startNode("category");
		writer.setValue(annotation.getCategory());
		writer.endNode();

		
		writer.startNode("target-isoform-list");
		context.convertAnother(annotation.getTargetingIsoformsMap());
		writer.endNode();

	}

	@Override
	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
		return null;
	}

}
