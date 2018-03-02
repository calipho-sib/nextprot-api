package org.nextprot.api.core.service.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Sets;
import org.nextprot.api.commons.constants.IdentifierOffset;
import org.nextprot.api.commons.constants.Xref2Annotation;
import org.nextprot.api.core.dao.DbXrefDao;
import org.nextprot.api.core.domain.DbXref;
import org.nextprot.api.core.domain.DbXref.DbXrefProperty;
import org.nextprot.api.core.domain.Isoform;
import org.nextprot.api.core.domain.PublicationDbXref;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.domain.annotation.AnnotationEvidence;
import org.nextprot.api.core.domain.annotation.AnnotationIsoformSpecificity;
import org.nextprot.api.core.service.AntibodyResourceIdsService;
import org.nextprot.api.core.service.DbXrefService;
import org.nextprot.api.core.service.IsoformService;
import org.nextprot.api.core.service.PeptideNamesService;
import org.nextprot.api.core.utils.dbxref.conv.DbXrefConverter;
import org.nextprot.api.core.utils.dbxref.conv.EnsemblXrefPropertyConverter;
import org.nextprot.api.core.utils.dbxref.resolver.DbXrefURLResolverSupplier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Lazy
@Service
public class DbXrefServiceImpl implements DbXrefService {

	private static final Set<String> HIDDEN_PROPERTY_NAME_SET = Sets.newHashSet("match status", "organism ID", "organism name");

	@Autowired private DbXrefDao dbXRefDao;
	@Autowired private PeptideNamesService peptideNamesService;
	@Autowired private AntibodyResourceIdsService antibodyResourceIdsService;
	@Autowired private IsoformService isoService;

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

		annotation.setEvidences(Collections.singletonList(newAnnotationEvidence(annotation)));
		annotation.addTargetingIsoforms(newAnnotationIsoformSpecificityList(isoforms, annotation));

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

	// build isoform specificity from isoforms and annotations and link them to annotations
	private List<AnnotationIsoformSpecificity> newAnnotationIsoformSpecificityList(List<Isoform> isoforms, Annotation xrefAnnotation) {

		List<AnnotationIsoformSpecificity> isospecs = new ArrayList<>();

		for (Isoform iso: isoforms) {

			AnnotationIsoformSpecificity isospec = new AnnotationIsoformSpecificity();

			isospec.setAnnotationId(xrefAnnotation.getAnnotationId());
			isospec.setFirstPosition(null); //According to PAM on Tuesday th 16th in the presence of Pascale and Frederic Nikitin
			isospec.setLastPosition(null);
			isospec.setIsoformAccession(iso.getIsoformAccession());
			isospec.setSpecificity("UNKNOWN");

			isospecs.add(isospec);
		}

		return isospecs;
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
	@Cacheable("xrefs")
	public List<DbXref> findDbXrefsByMaster(String entryName) {
		
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
		xrefs.addAll(this.dbXRefDao.findEntryAnnotationsEvidenceXrefs(entryName));
		xrefs.addAll(this.dbXRefDao.findEntryAttachedXrefs(entryName));
		xrefs.addAll(this.dbXRefDao.findEntryIdentifierXrefs(entryName));
		xrefs.addAll(this.dbXRefDao.findEntryInteractionXrefs(entryName));             // xrefs of interactions evidences
		xrefs.addAll(this.dbXRefDao.findEntryInteractionInteractantsXrefs(entryName)); // xrefs of xeno interactants
		
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

		Map<Long, List<DbXrefProperty>> ensemblPropertiesMap = getDbXrefEnsemblInfos(uniqueName, xrefs);

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


	private Map<Long, List<DbXrefProperty>> getDbXrefEnsemblInfos(String uniqueName, List<DbXref> xrefs) {

		List<Long> ensemblRefIds = xrefs.stream().filter(xref -> xref.getAccession().startsWith("ENST")).map(DbXref::getDbXrefId).collect(Collectors.toList());

		List<DbXref.EnsemblInfos> ensemblXRefInfosList = dbXRefDao.findDbXrefEnsemblInfos(uniqueName, ensemblRefIds);

		EnsemblXrefPropertyConverter converter = EnsemblXrefPropertyConverter.getInstance();

		Map<Long, List<DbXrefProperty>> map = new HashMap<>();
		for (DbXref.EnsemblInfos infos : ensemblXRefInfosList) {

			map.put(infos.getTranscriptXrefId(), converter.convert(infos));
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

		for (DbXref xref : xrefs) {

			if (DbXrefURLResolverSupplier.REF_SEQ.getName().equals(xref.getDatabaseName()) ||
				DbXrefURLResolverSupplier.EMBL.getName().equals(xref.getDatabaseName())) {

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
}
