package org.nextprot.api.core.utils.annot;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.commons.constants.PropertyApiModel;
import org.nextprot.api.core.domain.BioObject;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.Isoform;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.domain.annotation.AnnotationEvidence;
import org.nextprot.api.core.domain.annotation.AnnotationProperty;
import org.nextprot.api.core.service.EntryBuilderService;
import org.nextprot.api.core.service.fluent.EntryConfig;
import org.nextprot.api.core.test.base.CoreUnitBaseTest;
import org.nextprot.api.core.utils.IsoformUtils;
import org.nextprot.api.core.utils.seqmap.IsoformSequencePositionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GoDatasourceTest extends CoreUnitBaseTest {


    @Test
    public void shouldReturnAKnownSource()  {
				
		Assert.assertEquals("GOA curators", GoDatasource.getGoAssignedBy("GO_REF:0000107")); 
    }	

    @Test
    public void shouldReturnNullIfNotAKnownGoRef()  {
		
		Assert.assertNull(GoDatasource.getGoAssignedBy("tralala") );
    }	

}
