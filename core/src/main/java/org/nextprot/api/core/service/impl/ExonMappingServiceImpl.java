package org.nextprot.api.core.service.impl;

import org.nextprot.api.core.domain.GenomicMapping;
import org.nextprot.api.core.domain.IsoformGeneMapping;
import org.nextprot.api.core.domain.TranscriptGeneMapping;
import org.nextprot.api.core.domain.exon.ExonMapping;
import org.nextprot.api.core.service.ExonMappingService;
import org.nextprot.api.core.service.GenomicMappingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ExonMappingServiceImpl implements ExonMappingService {

	@Autowired
	private GenomicMappingService genomicMappingService;

	@Override
	public List<ExonMapping> findExonMappings(String entryAccession, String geneName) {

		Map<String, GenomicMapping> gmm = genomicMappingService.findGenomicMappingsByEntryName(entryAccession);

		if (gmm.containsKey(geneName)) {

			GenomicMapping gm = gmm.get(geneName);

            for (IsoformGeneMapping isoformGeneMapping : gm.getIsoformGeneMappings()) {

				String isoformMainName = isoformGeneMapping.getIsoformMainName();

				for (TranscriptGeneMapping transcriptGeneMapping : isoformGeneMapping.getTranscriptGeneMappings()) {

					String enstAccession = transcriptGeneMapping.getDatabaseAccession();

					ExonMapping exonMapping = new ExonMapping();
				}
            }
		}

		return null;
	}
}