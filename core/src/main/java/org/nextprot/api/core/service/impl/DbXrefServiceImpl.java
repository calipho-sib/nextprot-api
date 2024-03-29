package org.nextprot.api.core.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.nextprot.api.commons.constants.IdentifierOffset;
import org.nextprot.api.commons.constants.Xref2Annotation;
import org.nextprot.api.commons.utils.XRefProtocolId;
import org.nextprot.api.core.dao.DbXrefDao;
import org.nextprot.api.core.domain.CvDatabasePreferredLink;
import org.nextprot.api.core.domain.DbXref;
import org.nextprot.api.core.domain.DbXref.DbXrefProperty;
import org.nextprot.api.core.domain.Isoform;
import org.nextprot.api.core.domain.PublicationDbXref;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.domain.annotation.AnnotationEvidence;
import org.nextprot.api.core.service.AntibodyResourceIdsService;
import org.nextprot.api.core.service.DbXrefService;
import org.nextprot.api.core.service.IsoformService;
import org.nextprot.api.core.service.PeptideNamesService;
import org.nextprot.api.core.service.SimpleService;
import org.nextprot.api.core.service.StatementService;
import org.nextprot.api.core.service.annotation.AnnotationUtils;
import org.nextprot.api.core.service.dbxref.XrefDatabase;
import org.nextprot.api.core.service.dbxref.conv.DbXrefConverter;
import org.nextprot.api.core.service.dbxref.resolver.DbXrefURLResolverSupplier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Sets;

@Lazy
@Service
public class DbXrefServiceImpl implements DbXrefService {

	private static final Set<String> HIDDEN_PROPERTY_NAME_SET = Sets.newHashSet("match status", "organism ID", "organism name");

	@Autowired private DbXrefDao dbXRefDao;
	@Autowired private SimpleService simpleService;
	@Autowired private PeptideNamesService peptideNamesService;
	@Autowired private AntibodyResourceIdsService antibodyResourceIdsService;
	@Autowired private IsoformService isoService;
	@Autowired private StatementService statementService;
	

	@Override
	public List<PublicationDbXref> findDbXRefByPublicationId(Long publicationId) {
		return this.dbXRefDao.findDbXRefsByPublicationId(publicationId);
	}
	
	@Override
	public List<PublicationDbXref> findDbXRefByPublicationIds(List<Long> publicationIds) {
		return dbXRefDao.findDbXRefByPublicationIds(publicationIds);
	}
	

	@Override
	public List<DbXref> findDbXRefByIds(List<Long> resourceIds) {
		return dbXRefDao.findDbXRefByIds(resourceIds);
	}
	

	private List<Annotation> convertXrefsIntoAnnotations(List<DbXref> xrefs, String entryName) {

		List<Isoform> isoforms = isoService.findIsoformsByEntryName(entryName);

		List<Annotation> xrefAnnotations = new ArrayList<>();

		for (DbXref xref : xrefs) {

			xrefAnnotations.add(convertXrefIntoAnnotation(xref, entryName, isoforms));
		}

		return xrefAnnotations;
	}

	private Annotation convertXrefIntoAnnotation(DbXref xref, String entryName, List<Isoform> isoforms) {

		Preconditions.checkNotNull(xref.getProperties());

		Annotation annotation = new Annotation();

		annotation.setAnnotationId(xref.getDbXrefId() + IdentifierOffset.XREF_ANNOTATION_OFFSET);

		Xref2Annotation xam = Xref2Annotation.getByDatabaseName(xref.getDatabaseName());
		annotation.setCategory(xam.getAnnotCat());
		annotation.setDescription(xref.getPropertyValue(xam.getXrefPropName())); // copy of some xref property
		annotation.setQualityQualifier(xam.getQualityQualifier());

		annotation.setCvTermName(null);
		annotation.setCvTermAccessionCode(null);
		annotation.setSynonym(null);
		annotation.setUniqueName("AN_" + entryName.substring(3) + "_XR_" + String.valueOf(xref.getDbXrefId()));
		annotation.setParentXref(xref);
		
		if (xam.usesBioObject()) {
			annotation.setDescription(null);
			annotation.setBioObject(AnnotationUtils.newExternalChemicalBioObject(xref,"generic name"));
		}
		
		annotation.setEvidences(Collections.singletonList(newAnnotationEvidence(annotation)));
		annotation.addTargetingIsoforms(AnnotationUtils.newNonPositionalAnnotationIsoformSpecificityList(isoforms, annotation));

		return annotation;
	}

	private AnnotationEvidence newAnnotationEvidence(Annotation xrefAnnotation) {

		AnnotationEvidence evidence = new AnnotationEvidence();
		DbXref pxref = xrefAnnotation.getParentXref();

		evidence.setAnnotationId(xrefAnnotation.getAnnotationId());
		Xref2Annotation xam = Xref2Annotation.getByDatabaseName(pxref.getDatabaseName());
		evidence.setEvidenceId(xrefAnnotation.getAnnotationId() + IdentifierOffset.XREF_ANNOTATION_EVIDENCE_OFFSET);
		evidence.setAssignedBy(xam.getSrcName());
		evidence.setResourceId(pxref.getDbXrefId());
		evidence.setResourceAccession(pxref.getAccession());
		evidence.setResourceDb(pxref.getDatabaseName());
		evidence.setResourceAssociationType("evidence");
		evidence.setResourceType("database");
		evidence.setEvidenceCodeOntology(xam.getEcoOntology());
		evidence.setNegativeEvidence(false);
		evidence.setExperimentalContextId(null);
		evidence.setResourceDescription(null);
		evidence.setProperties(new ArrayList<>());
		evidence.setQualifierType(xam.getQualifierType());
		evidence.setQualityQualifier(xam.getQualityQualifier());
		evidence.setAssignmentMethod(xam.getAssignmentMethod());
		evidence.setEvidenceCodeAC(xam.getEcoAC());
		evidence.setEvidenceCodeName(xam.getEcoName());

		return evidence;
	}



	@Override
	/** Convert dbxrefs of type XrefAnnotationMapping into annotation for the given entry */
	public List<Annotation> findDbXrefsAsAnnotationsByEntry(String entryName) {

		List<DbXref> xrefsToConvert = findDbXrefsConvertibleIntoAnnotationByEntry(entryName);

		if(!xrefsToConvert.isEmpty())
			attachPropertiesToXrefs(xrefsToConvert, entryName, true);

		List<Annotation> xrefAnnotations = convertXrefsIntoAnnotations(xrefsToConvert, entryName);

		return new ImmutableList.Builder<Annotation>().addAll(xrefAnnotations).build();
	}

    /**
	 * Find dbxrefs convertible into Annotations (of type XrefAnnotationMapping)
	 * @param uniqueName the entry name
	 * @return a list of DbXref convertible to Annotation
	 */
	private List<DbXref> findDbXrefsConvertibleIntoAnnotationByEntry(String uniqueName) {

		return dbXRefDao.findDbXrefsAsAnnotByMaster(uniqueName);
	}

	@Override
	@Cacheable(value = "xrefs", sync = true)
	public List<DbXref> findDbXrefsByMaster(String entryName) {
		
		return this.findDbXrefsByMaster(entryName, false);
	}

	@Override
	public List<DbXref> findDbXrefsByMasterExcludingBed(String entryName) {
		
		return this.findDbXrefsByMaster(entryName, true);
	}

	
	@Override
	public DbXref createDecipherXref(String geneName) {
						
		DbXref xref = new DbXref();
		xref.setAccession(geneName);
		xref.setDatabaseCategory("Polymorphism and mutation databases");
		String db = XrefDatabase.DECIPHER.getName();
		xref.setDatabaseName(db);
		long id = generateXrefProtocolId(db, geneName);
		xref.setDbXrefId(id);
		xref.setLinkUrl(CvDatabasePreferredLink.DECIPHER.getLink());
		xref.setUrl("https://www.deciphergenomics.org");
		return xref;
	}
	
	
	
	private List<DbXref> findDbXrefsByMaster(String entryName, boolean ignoreStatements) {
		
		// build a comparator for the tree set: order by database name, accession, case insensitive
		Comparator<DbXref> comparator = (a, b) -> {
            int cmp1 = a.getDatabaseName().toUpperCase().compareTo(b.getDatabaseName().toUpperCase());
            if (cmp1!=0) return cmp1;
            return a.getAccession().toUpperCase().compareTo(b.getAccession().toUpperCase());
        };

		// now merge xrefs associated to the entry by annot, interact, mappings, etc. in the tree set 
		Set<DbXref> xrefs = new TreeSet<>(comparator);

		addPeptideXrefs(entryName, xrefs);
		addAntibodyXrefs(entryName, xrefs);
		xrefs.addAll(dbXRefDao.findEntryAnnotationsEvidenceXrefs(entryName));
		xrefs.addAll(dbXRefDao.findEntryAttachedXrefs(entryName));
		xrefs.addAll(dbXRefDao.findEntryIdentifierXrefs(entryName));
		xrefs.addAll(dbXRefDao.findEntryInteractionXrefs(entryName));                // xrefs of interactions evidences
		xrefs.addAll(dbXRefDao.findEntryInteractionInteractantsXrefs(entryName));    // xrefs of xeno interactants
		if (! ignoreStatements ) xrefs.addAll(statementService.findDbXrefs(entryName));   // xrefs of statements (but not gnomad ones)
		
		// turn the set into a list to match the signature expected elsewhere
		List<DbXref> xrefList = new ArrayList<>(xrefs);
		
		// get and attach the properties to the xrefs
		if (! xrefList.isEmpty()) {
			attachPropertiesToXrefs(xrefList, entryName, false);
		}

		//returns a immutable list when the result is cacheable (this prevents modifying the cache, since the cache returns a reference) copy on read and copy on write is too much time consuming
		return new ImmutableList.Builder<DbXref>().addAll(xrefList).build();
	}
	
	
	private void addPeptideXrefs(String entryName, Set<DbXref> xrefs) {

		List<String> names = peptideNamesService.findAllPeptideNamesByMasterId(entryName);
		xrefs.addAll(!names.isEmpty() ? dbXRefDao.findPeptideXrefs(names) : new HashSet<>());
	}

	private void addAntibodyXrefs(String entryName, Set<DbXref> xrefs) {

		List<Long> ids = antibodyResourceIdsService.findAllAntibodyIdsByMasterId(entryName);
		xrefs.addAll(!ids.isEmpty() ? dbXRefDao.findAntibodyXrefs(ids) : new HashSet<>());
	}

	private void attachPropertiesToXrefs(List<DbXref> xrefs, String uniqueName, boolean fetchXrefAnnotationMappingProperties) {

		List<Long> xrefIds = xrefs.stream().map(DbXref::getDbXrefId).collect(Collectors.toList());

		List<DbXrefProperty> shownProperties = dbXRefDao.findDbXrefsProperties(uniqueName, xrefIds).stream()
			.filter(p -> !HIDDEN_PROPERTY_NAME_SET.contains(p.getName()))
			.collect(Collectors.toList());

		Multimap<Long, DbXrefProperty> propsMap = Multimaps.index(shownProperties, DbXrefProperty::getDbXrefId);

		Map<Long, List<DbXrefProperty>> ensemblPropertiesMap = getDbXrefEnsemblInfos(xrefs);

		for (DbXref xref : xrefs) {
			if (!fetchXrefAnnotationMappingProperties)
				xref.setProperties((!Xref2Annotation.hasName(xref.getDatabaseName())) ? new ArrayList<>(propsMap.get(xref.getDbXrefId())) : new ArrayList<>());
			else
				xref.setProperties(new ArrayList<>(propsMap.get(xref.getDbXrefId())));

			/*if (xref.getLinkUrl().contains("%u")) {
				xref.setResolvedUrl(new DbXrefURLResolverDelegate().resolveWithAccession(xref, uniqueName));
			}*/

			if (ensemblPropertiesMap.containsKey(xref.getDbXrefId())) {

				xref.addProperties(ensemblPropertiesMap.get(xref.getDbXrefId()));
			}
		}

		xrefs.addAll(createMissingDbXrefs(xrefs));
	}


	private Map<Long, List<DbXrefProperty>> getDbXrefEnsemblInfos(List<DbXref> xrefs) {

		List<Long> ensemblRefIds = xrefs.stream().filter(xref -> xref.getAccession().startsWith("ENST")).map(DbXref::getDbXrefId).collect(Collectors.toList());

		List<DbXref.EnsemblInfos> ensemblXRefInfosList = dbXRefDao.findDbXrefEnsemblInfos(ensemblRefIds);

		Map<Long, List<DbXrefProperty>> map = new HashMap<>();
		for (DbXref.EnsemblInfos info : ensemblXRefInfosList) {
			Long enstXrefId = info.getEnstXrefId();
			if (!map.containsKey(enstXrefId)) map.put(enstXrefId, new ArrayList<>());
			
			DbXrefProperty prop = info.toDbXrefProperty();
			//System.out.println(prop);
			map.get(enstXrefId).add(prop);
		}

		return map;
	}

	/**
	 * Create dynamically missing xrefs from specific properties
	 *
	 * @param xrefs a list of xrefs
	 * @return the new created list
     */
	private List<DbXref> createMissingDbXrefs(List<DbXref> xrefs) {

		List<DbXref> newXrefs = new ArrayList<>();
		String refseqDbName = DbXrefURLResolverSupplier.REF_SEQ.getXrefDatabase().getName();
		String emblDbName = DbXrefURLResolverSupplier.EMBL.getXrefDatabase().getName();
		for (DbXref xref : xrefs) {
			if (refseqDbName.equals(xref.getDatabaseName()) || emblDbName.equals(xref.getDatabaseName())) {
				newXrefs.addAll(DbXrefConverter.getInstance().convert(xref));
			}
		}
		return newXrefs;
	}

	@Override
	public List<DbXref> findDbXrefByAccession(String accession) {

		return dbXRefDao.findDbXrefByAccession(accession);
	}

	@Override
	public List<DbXref> findAllDbXrefs() {

		return dbXRefDao.findAllDbXrefs();
	}

	@Override
	public List<DbXref> findDbXRefByResourceId(Long resourceId) {

		return dbXRefDao.findDbXrefByResourceId(resourceId);
	}

	@Override
	public List<Long> getAllDbXrefsIds() {

		return dbXRefDao.getAllDbXrefsIds();
	}

    @Override
	@Cacheable(value = "xrefid-by-dbac", sync = true)
    public long findXrefId(String database, String accession) {
    	// search db first
    	Long id = dbXRefDao.findXrefId(database, accession);
    	// if not found create one based on db and ac
    	if (id == null) id = generateXrefProtocolId(database, accession);
    	return id.longValue();
    }

    private long generateXrefProtocolId(String dbName, String accession)  {

    	long dbId = simpleService.getNameDatabaseMap().get(dbName).getId();
    	long xrefId = new XRefProtocolId(dbId, accession).id();
    	return xrefId;
    }

	@Override
	public Map<String, String> getGeneRifBackLinks(long pubId) {
		return dbXRefDao.getGeneRifBackLinks(pubId);
	}
}
