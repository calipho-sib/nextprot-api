package org.nextprot.api.core.dao;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import org.junit.Test;
import org.nextprot.api.core.domain.VariantFrequency;
import org.nextprot.api.core.domain.annotation.AnnotationVariant;
import org.nextprot.api.core.test.base.CoreUnitBaseTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;

@ActiveProfiles({"unit", "unit-schema-nxflat"})
@DatabaseSetup(value="VariantFrequencyMVCTest.xml", type = DatabaseOperation.INSERT)
public class VariantFrequencyDaoTest extends CoreUnitBaseTest {

    @Autowired
    private VariantFrequencyDao variantFrequencyDao;

    @Test
    public void findVariantFrequencyGivenDBSNPIdTest() {
        AnnotationVariant variant = new AnnotationVariant("GLY", "GLU");

        List<VariantFrequency> variantFrequencies = variantFrequencyDao.findVariantFrequency("rs001", variant);
        assertEquals(variantFrequencies.size(),1);
        VariantFrequency variantFrequency = variantFrequencies.get(0);
        assertEquals("Allele frequency", 0.1, variantFrequency.getAllelFrequency(), 0);
        assertEquals("Original Nucleotide", "A", variantFrequency.getOriginalNucleotide());
        assertEquals("Variant Nucleotide", "T", variantFrequency.getVariantNucleotide());
    }

    @Test
    public void findVariantFrequenciesGivenDBSNPIdsTest() {
        Set<String> dbSNPIds = new HashSet<>();
        dbSNPIds.add("rs001");
        dbSNPIds.add("rs002");
        Map<String, List<VariantFrequency>> variantFrequencies = variantFrequencyDao.findVariantFrequency(dbSNPIds);
        // We should get three variants for these two dbsnp, one for rs002 and two for rs002
        assertEquals(variantFrequencies.keySet().size(),2);
        // We should have two variants for rs001
        List<VariantFrequency> variantList = variantFrequencies.get("rs001");
        assertEquals(variantList.size(), 2);

        // variant details
        assertEquals(variantList.get(0).getOriginalNucleotide(), "A");
        assertEquals(variantList.get(0).getVariantNucleotide(), "C");

        assertEquals(variantList.get(1).getOriginalNucleotide(), "A");
        assertEquals(variantList.get(1).getVariantNucleotide(), "G");
    }
}
