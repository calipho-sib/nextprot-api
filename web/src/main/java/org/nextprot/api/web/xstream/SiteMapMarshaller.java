package org.nextprot.api.web.xstream;

import org.nextprot.api.web.seo.domain.SitemapUrl;
import org.nextprot.api.web.seo.domain.SitemapUrlSet;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.http.converter.xml.MarshallingHttpMessageConverter;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

public class SiteMapMarshaller extends MarshallingHttpMessageConverter implements InitializingBean {

	@Override
	public boolean supports(Class<?> clazz) {
		return (clazz.equals(SitemapUrlSet.class) || clazz.equals(SitemapUrl.class));
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		Jaxb2Marshaller m = new Jaxb2Marshaller();
		m.setClassesToBeBound(SitemapUrlSet.class, SitemapUrl.class);
		this.setMarshaller(m);
	}

}
