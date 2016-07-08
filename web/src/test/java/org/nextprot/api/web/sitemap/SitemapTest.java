package org.nextprot.api.web.sitemap;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.junit.Test;
import org.junit.Assert;
import org.nextprot.api.web.sitemap.domain.SitemapUrl;
import org.nextprot.api.web.sitemap.domain.SitemapUrlSet;

public class SitemapTest {

	@Test
	public void testSitemapUrlCreation() {

		String url = "http://aaa.bbb.com/toto";
		SitemapUrl siturl = new SitemapUrl(url);
		String expectedDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
		System.out.println("expectedDate: " + expectedDate);
		Assert.assertEquals(url, siturl.getLoc());
		Assert.assertEquals(expectedDate, siturl.getLastmod());
		Assert.assertEquals("weekly", siturl.getChangefreq());
		Assert.assertEquals("0.5", siturl.getPriority());

	}

	@Test
	public void testSitemapUrlSetCreation() {
		String base = "https://search.nextprot.org";
		SitemapUrlSet urlSet = new SitemapUrlSet();
		urlSet.add(new SitemapUrl(base + "/about"));
		urlSet.add(new SitemapUrl(base + "/copyright"));
		urlSet.add(new SitemapUrl(base + "/news"));
		urlSet.add(new SitemapUrl(base + "/help"));
		urlSet.add(new SitemapUrl(base + "/copyright"));
		urlSet.add(new SitemapUrl(base + "/help/simple-search"));
		for (SitemapUrl siturl : urlSet.getUrls()) System.out.println(siturl.getLoc());
		Assert.assertEquals(5, urlSet.getUrls().size());
	}

	@Test
	public void test1() {

		try {

			String base = "https://search.nextprot.org";
			SitemapUrlSet urlSet = new SitemapUrlSet();
			urlSet.add(new SitemapUrl(base + "/about"));
			urlSet.add(new SitemapUrl(base + "/copyright"));
			urlSet.add(new SitemapUrl(base + "/news"));


			
			//SitemapUrl siturl = new SitemapUrl(base + "/toto");
			
			JAXBContext jaxbContext = JAXBContext.newInstance(SitemapUrlSet.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			jaxbMarshaller.marshal(urlSet, System.out);

		} catch (JAXBException e) {
			e.printStackTrace();
		}

	}

}
