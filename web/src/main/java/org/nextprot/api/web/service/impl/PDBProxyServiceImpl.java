package org.nextprot.api.web.service.impl;

import org.apache.commons.io.IOUtils;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.web.service.PDBProxyService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

@Service
public class PDBProxyServiceImpl implements PDBProxyService {

	private static final String PDB_URL = "https://files.rcsb.org/download/";

	@Override
	@Cacheable(value = "pdb-proxy", sync = true)
	public String findPdbEntry(String id) {

		return readUrl(PDB_URL + id + ".pdb");
	}

	@Override
	@Cacheable(value = "pdbx-proxy", sync = true)
	public String findPdbxEntry(String id) {

		return readUrl(PDB_URL + id + ".cif");
	}

	private String readUrl(String url) {

		try {
			return IOUtils.toString(new InputStreamReader(new URL(url).openStream()));
		} catch (HttpStatusCodeException e) {
			throw new NextProtException(e.getResponseBodyAsString());
		} catch (IOException e) {
			throw new NextProtException(e.toString());
		}
	}
}
