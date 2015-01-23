package org.nextprot.api.web.xstream.converters;

import org.nextprot.api.core.domain.Entry;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

@NextprotConverter
public class EntryConverter implements Converter {

	public EntryConverter() {
		super();
	}

	public boolean canConvert(@SuppressWarnings("rawtypes") Class clazz) {
		return Entry.class.isAssignableFrom(clazz);
	}

	public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
		Entry entry = (Entry) value;
		writer.startNode("fullname");
		writer.addAttribute("yo", "yeah");
		writer.setValue(entry.getUniqueName());
		writer.endNode();

		if(entry.getIsoforms() != null){
			writer.startNode("isoform-list");
			context.convertAnother(entry.getIsoforms());
			writer.endNode();
		}

		if(entry.getPublications() != null){
			writer.startNode("publication-list");
			context.convertAnother(entry.getPublications());
			writer.endNode();
		}
		
		if(entry.getXrefs() != null){
			writer.startNode("xref-list");
			context.convertAnother(entry.getXrefs());
			writer.endNode();
		}

		writer.startNode("annotation-list");
		context.convertAnother(entry.getAnnotations());
		writer.endNode();

	}

	@Override
	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
		return null;
	}

}
