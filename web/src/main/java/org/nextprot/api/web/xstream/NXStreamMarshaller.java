package org.nextprot.api.web.xstream;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.nextprot.api.web.xstream.converters.NextprotConverter;
import org.reflections.Reflections;
import org.springframework.oxm.xstream.XStreamMarshaller;

import com.thoughtworks.xstream.converters.Converter;

public class NXStreamMarshaller extends XStreamMarshaller {

	// http://xstream.codehaus.org/converter-tutorial.html
	public NXStreamMarshaller() {
		super();
	}

	@PostConstruct
	public void init() {
		setAliases();
		setConverters();
	}

	private void setAliases() {

		this.getXStream().alias("entry", org.nextprot.api.core.domain.Entry.class);
		this.getXStream().alias("isoform", org.nextprot.api.core.domain.Isoform.class);
		this.getXStream().alias("publication", org.nextprot.api.core.domain.Publication.class);
		this.getXStream().alias("xref", org.nextprot.api.core.domain.DbXref.class);
		this.getXStream().alias("target-isoform",org.nextprot.api.core.domain.annotation.AnnotationIsoformSpecificity.class);
		this.getXStream().alias("annotation",org.nextprot.api.core.domain.annotation.Annotation.class);
		
	}

	private void setConverters() {

		List<Converter> converters = new ArrayList<Converter>();
		Reflections reflections = new Reflections("org.nextprot.api.web.xstream.converters");
		Set<Class<? extends Object>> converterClasses = reflections.getTypesAnnotatedWith(NextprotConverter.class);
		for (Class<?> cl : converterClasses) {
			try {
				converters.add((Converter) cl.newInstance());
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}

		this.setConverters(converters.toArray(new Converter[0]));

	}

}
