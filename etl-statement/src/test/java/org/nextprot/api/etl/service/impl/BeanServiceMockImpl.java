package org.nextprot.api.etl.service.impl;

import com.google.common.collect.Sets;
import org.nextprot.api.core.domain.*;
import org.nextprot.api.core.service.BeanService;
import org.nextprot.api.core.service.EntryBuilderService;
import org.nextprot.api.core.service.IsoformService;
import org.nextprot.api.core.service.MasterIdentifierService;
import org.nextprot.api.core.service.fluent.EntryConfig;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.nextprot.api.etl.statement.StatementETLBaseUnitTest.mockIsoform;

public class BeanServiceMockImpl implements BeanService {

    @Override
    public <T> T getBean(Class<T> beanClass) {

        if (beanClass.isAssignableFrom(MasterIdentifierService.class)) {
            return (T) new MasterIdentifierServiceMockImpl();
        }
        else if (beanClass.isAssignableFrom(IsoformService.class)) {
            return (T) new IsoformServiceMockImpl();
        }
        else if (beanClass.isAssignableFrom(EntryBuilderService.class)) {
            return (T) new EntryBuilderServiceMockImpl();
        }

        return null;
    }

    public static class MasterIdentifierServiceMockImpl implements MasterIdentifierService {

        @Override
        public Long findIdByUniqueName(String uniqueName) {
            return null;
        }

        @Override
        public List<String> findUniqueNamesOfChromosome(String chromosome) {
            return null;
        }

        @Override
        public Set<String> findUniqueNames() {
            return null;
        }

        @Override
        public Set<String> findEntryAccessionByGeneName(String geneName, boolean withSynonyms) {

            if (geneName.equals("SCN9A")) {
                return Sets.newHashSet("NX_Q15858");
            }
            return new HashSet<>();
        }

        @Override
        public List<String> findEntryAccessionsByProteinExistence(ProteinExistence proteinExistence) {
            return null;
        }
    }

    public static class IsoformServiceMockImpl implements IsoformService {
        @Override
        public List<Isoform> findIsoformsByEntryName(String entryName) {
            return null;
        }

        @Override
        public Isoform findIsoformByName(String entryAccession, String name) {

            if (entryAccession.equals("NX_Q15858")) {
                return mockIsoform("NX_Q15858-3", name, false);
            }
            return null;
        }

        @Override
        public List<Isoform> getOtherIsoforms(String isoformUniqueName) {
            return null;
        }

        @Override
        public IsoformPEFFHeader formatPEFFHeader(String isoformAccession) {
            return null;
        }

        @Override
        public List<Set<String>> getSetsOfEquivalentIsoforms() {
            return null;
        }

        @Override
        public List<Set<String>> getSetsOfEntriesHavingAnEquivalentIsoform() {
            return null;
        }

        @Override
        public List<SlimIsoform> findListOfIsoformAcMd5Sequence() {
            return null;
        }

        @Override
        public Isoform getIsoformByNameOrCanonical(String entryNameOrIsoformName) {

            if (entryNameOrIsoformName.equals("NX_Q15858")) {
                return mockIsoform("NX_Q15858", "Iso 3", false);
            }
            return null;
        }
    }

    public static class EntryBuilderServiceMockImpl implements EntryBuilderService {
        @Override
        public Entry build(EntryConfig entryConfig) {
            return null;
        }

        @Override
        public Entry buildWithEverything(String entryName) {
            return null;
        }
    }
}