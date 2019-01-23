package org.nextprot.api.web.service.impl;

import org.apache.commons.io.IOUtils;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.web.service.PDBProxyService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

@Service
public class PDBProxyServiceImpl implements PDBProxyService {

	private static final String PDB_URL = "https://files.rcsb.org/download/";

	@Override
	@Cacheable(value = "pdb-proxy", sync = true)
	public String findPdbEntry(String id) {

		try {

			 InputStream in = new URL(PDB_URL + id + ".pdb").openStream();
			 return IOUtils.toString( in);

		} catch (HttpClientErrorException e) {
			throw new NextProtException(e.getResponseBodyAsString());
		} catch (HttpServerErrorException e) {
			throw new NextProtException(e.getResponseBodyAsString());
		} catch (MalformedURLException e) {
			throw new NextProtException(e.getMessage());
		} catch (IOException e) {
			throw new NextProtException(e.getMessage());
		}
	}

	@Override
	@Cacheable(value = "pdbx-proxy", sync = true)
	public String findPdbxEntry(String id) {

		try {

			 InputStream in = new URL(PDB_URL + id + ".cif").openStream();
			 return IOUtils.toString( in );

		} catch (HttpClientErrorException e) {
			throw new NextProtException(e.getResponseBodyAsString());
		} catch (HttpServerErrorException e) {
			throw new NextProtException(e.getResponseBodyAsString());
		} catch (MalformedURLException e) {
			throw new NextProtException(e.getMessage());
		} catch (IOException e) {
			throw new NextProtException(e.getMessage());
		}
	}

}
