package org.nextprot.api.core.service;

import org.junit.Test;
import org.nextprot.api.core.domain.ChromosomeReport;
import org.nextprot.api.core.service.export.io.ChromosomeReportTXTReader;

import java.io.InputStreamReader;
import java.net.URL;


public class ChromosomeEntryReportIntegrationTest {

	@Test
	public void chr1EntryReportsShouldMatchFTPReports() throws Exception {

		ChromosomeReportTXTReader reader = new ChromosomeReportTXTReader();

		URL chrReportsFTPUrl = new URL("ftp://ftp.nextprot.org/pub/current_release/chr_reports/nextprot_chromosome_1.txt");
		ChromosomeReport cr1FTP = reader.read(new InputStreamReader(chrReportsFTPUrl.openStream()));

		URL chrReportsURL = new URL("http://build-api.nextprot.org/export/reports/chromosome/1.txt");
		ChromosomeReport cr1 = reader.read(new InputStreamReader(chrReportsURL.openStream()));

		System.out.println(cr1FTP);
	}
}

