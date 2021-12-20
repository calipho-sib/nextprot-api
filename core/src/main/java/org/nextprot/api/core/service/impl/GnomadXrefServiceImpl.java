package org.nextprot.api.core.service.impl;

import com.google.common.collect.ImmutableList;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.core.domain.CvDatabasePreferredLink;
import org.nextprot.api.core.domain.DbXref;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.service.AnnotationService;
import org.nextprot.api.core.service.DbXrefService;
import org.nextprot.api.core.service.GnomadXrefService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GnomadXrefServiceImpl implements GnomadXrefService {
    @Autowired
    private AnnotationService annotationService;

    @Autowired
    private DbXrefService xrefService;

    private static final Log LOGGER = LogFactory.getLog(GnomadXrefServiceImpl.class);

    @Override
    @Cacheable(value = "gnomadxrefs", sync = true)
    public List<DbXref> findGnomadDbXrefsByMaster(String entryName) {
        List<Annotation> annotations = annotationService.findAnnotations(entryName);
        List<DbXref> gnomADXrefs = new ArrayList<>();
        annotations.stream()
                .filter(annotation -> AnnotationCategory.VARIANT.getDbAnnotationTypeName().equals(annotation.getCategory()))
                .forEach(annotation -> {
                    // Generates dbxref for all evidence
                    annotation.getEvidences()
                            .stream()
                            .filter(annotationEvidence -> "gnomAD".equals(annotationEvidence.getResourceDb()))
                            .forEach(annotationEvidence -> {
                                DbXref gnomadXref = new DbXref();
                                try {
                                    long xrefId = xrefService.findXrefId("gnomAD", annotationEvidence.getResourceAccession());
                                    gnomadXref.setDbXrefId(xrefId);
                                    gnomadXref.setAccession(annotationEvidence.getResourceAccession());
                                    gnomadXref.setDatabaseCategory("Polymorphism and mutation databases");
                                    gnomadXref.setDatabaseName("gnomAD");
                                    gnomadXref.setUrl("https://gnomad.broadinstitute.org");
                                    gnomadXref.setLinkUrl(CvDatabasePreferredLink.GNOMAD.getLink());
                                    gnomadXref.setProperties(new ArrayList<>());
                                    gnomADXrefs.add(gnomadXref);
                                } catch(Exception e) {
                                    e.printStackTrace();
                                }
                            });
                });
        LOGGER.info("GNOMAD xrefs generated " + gnomADXrefs.size());
        return  new ImmutableList.Builder<DbXref>().addAll(gnomADXrefs).build();
    }
}
