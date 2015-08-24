package org.nextprot.api.core.service.impl;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.*;
import org.nextprot.api.commons.constants.XrefAnnotationMapping;
import org.nextprot.api.core.dao.DbXrefDao;
import org.nextprot.api.core.domain.CvDatabasePreferredLink;
import org.nextprot.api.core.domain.DbXref;
import org.nextprot.api.core.domain.DbXref.DbXrefProperty;
import org.nextprot.api.core.domain.Isoform;
import org.nextprot.api.core.domain.PublicationDbXref;
import org.nextprot.api.core.domain.annotation.*;
import org.nextprot.api.core.service.DbXrefService;
import org.nextprot.api.core.service.IsoformService;
import org.nextprot.api.core.service.PeptideNamesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.*;

@Lazy
@Service
public class DbXrefServiceImpl implements DbXrefService {

	@Autowired private DbXrefDao dbXRefDao;
	@Autowired private PeptideNamesService peptideNamesService;
	@Autowired private IsoformService isoService;

	private static final Function<DbXref, Long> DB_XREF_LONG_FUNCTION = new Function<DbXref, Long>() {
		public Long apply(DbXref xref) {
			return xref.getDbXrefId();
		}
	};
	private static final Predicate<DbXrefProperty> DB_XREF_EXCLUDING_HIDDEN_PROPERTIES_PREDICATE = new DbXrefExcludedPropertyPredicate(Sets.newHashSet("status", "match status", "organism ID", "organism name"));

	@Override
	public List<DbXref> findDbXRefByPublicationId(Long publicationId) {
		return this.dbXRefDao.findDbXRefsByPublicationId(publicationId);
	}
	
	@Override
	public List<PublicationDbXref> findDbXRefByPublicationIds(List<Long> publicationIds) {
		List<PublicationDbXref> xrefs = this.dbXRefDao.findDbXRefByPublicationIds(publicationIds);
		
		for(PublicationDbXref xref : xrefs)
			xref.setResolvedUrl(xref.resolveLinkTarget());

		return xrefs;
	}
	

	@Override
	public List<DbXref> findDbXRefByIds(List<Long> resourceIds) {
		List<DbXref> xrefs = this.dbXRefDao.findDbXRefByIds(resourceIds);
		return xrefs;
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

		annotation.setProperties(new ArrayList<AnnotationProperty>());
		annotation.setAnnotationId(xref.getDbXrefId() + 10_000_000_000L);

		XrefAnnotationMapping xam = XrefAnnotationMapping.getByDatabaseName(xref.getDatabaseName());
		annotation.setCategory(xam.getAnnotCat());
		annotation.setDescription(xref.getPropertyValue(xam.getXrefPropName())); // copy of some xref property
		annotation.setQualityQualifier(xam.getQualityQualifier());

		annotation.setCvTermName(null);
		annotation.setCvTermAccessionCode(null);
		annotation.setSynonym(null);
		annotation.setUniqueName("AN_" + entryName.substring(3) + "_XR_" + String.valueOf(xref.getDbXrefId()));
		annotation.setParentXref(xref);

		annotation.setEvidences(Arrays.asList(newAnnotationEvidence(annotation)));
		annotation.setTargetingIsoforms(newAnnotationIsoformSpecificityList(isoforms, annotation));

		return annotation;
	}

	private AnnotationEvidence newAnnotationEvidence(Annotation xrefAnnotation) {

		AnnotationEvidence evidence = new AnnotationEvidence();
		DbXref pxref = xrefAnnotation.getParentXref();

		evidence.setAnnotationId(xrefAnnotation.getAnnotationId());
		XrefAnnotationMapping xam = XrefAnnotationMapping.getByDatabaseName(pxref.getDatabaseName());
		evidence.setEvidenceId(xrefAnnotation.getAnnotationId() + 20_000_000_000L);
		evidence.setAssignedBy(xam.getSrcName());
		evidence.setResourceId(pxref.getDbXrefId());
		evidence.setResourceAccession(pxref.getAccession());
		evidence.setResourceDb(pxref.getDatabaseName());
		evidence.setResourceAssociationType("evidence");
		evidence.setResourceType("database");
		evidence.setNegativeEvidence(false);
		evidence.setExperimentalContextId(null);
		evidence.setResourceDescription(null);
		evidence.setPublicationMD5(null);
		evidence.setProperties(new ArrayList<AnnotationEvidenceProperty>());
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
			isospec.setFirstPosition(0);
			isospec.setLastPosition(0);
			isospec.setIsoformName(iso.getUniqueName());
			isospec.setSpecificity("UNKNOWN");

			isospecs.add(isospec);
		}

		return isospecs;
	}

	@Override
	@Cacheable("xrefs-as-annot")
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

		return this.dbXRefDao.findDbXrefsAsAnnotByMaster(uniqueName);
	}

	@Override
	@Cacheable("xrefs")
	public List<DbXref> findDbXrefsByMaster(String entryName) {
		
		// build a comparator for the tree set: order by database name, accession, case insensitive
		Comparator<DbXref> comparator = new Comparator<DbXref>() {
			public int compare(DbXref a, DbXref b) {
				int cmp1 = a.getDatabaseName().toUpperCase().compareTo(b.getDatabaseName().toUpperCase());
				if (cmp1!=0) return cmp1;
				return a.getAccession().toUpperCase().compareTo(b.getAccession().toUpperCase());
			}
		};

		// now merge xrefs associated to the entry by annot, interact, mappings, etc. in the tree set 
		Set<DbXref> xrefs = new TreeSet<>(comparator);
		List<String> peptideNames = this.peptideNamesService.findAllPeptideNamesByMasterId(entryName);

		xrefs.addAll(peptideNames.size()>0 ? this.dbXRefDao.findPeptideXrefs(peptideNames) :  new HashSet<DbXref>());
		xrefs.addAll(this.dbXRefDao.findEntryAnnotationsEvidenceXrefs(entryName));
		xrefs.addAll(this.dbXRefDao.findEntryAttachedXrefs(entryName));
		xrefs.addAll(this.dbXRefDao.findEntryIdentifierXrefs(entryName));
		xrefs.addAll(this.dbXRefDao.findEntryInteractionXrefs(entryName));             // xrefs of interactions evidences
		xrefs.addAll(this.dbXRefDao.findEntryInteractionInteractantsXrefs(entryName)); // xrefs of xeno interactants
		
		// turn the set into a list to match the signature expected elsewhere
		List<DbXref> xrefList = new ArrayList<>(xrefs);
		
		// get and attach the properties to the xrefs
		if (! xrefList.isEmpty()) attachPropertiesToXrefs(xrefList, entryName, false);

		//returns a immutable list when the result is cacheable (this prevents modifying the cache, since the cache returns a reference) copy on read and copy on write is too much time consuming
		return new ImmutableList.Builder<DbXref>().addAll(xrefList).build();
	}

	private void attachPropertiesToXrefs(List<DbXref> xrefs, String uniqueName, boolean fetchXrefAnnotationMappingProperties) {

		List<Long> xrefIds = Lists.transform(xrefs, DB_XREF_LONG_FUNCTION);

		Collection<DbXrefProperty> shownProperties = Collections2.filter(dbXRefDao.findDbXrefsProperties(xrefIds), DB_XREF_EXCLUDING_HIDDEN_PROPERTIES_PREDICATE);

		Multimap<Long, DbXrefProperty> propsMap = Multimaps.index(shownProperties, new Function<DbXrefProperty, Long>() {
			public Long apply(DbXrefProperty prop) {
				return prop.getDbXrefId();
			}
		});

		for (DbXref xref : xrefs) {
			if (!fetchXrefAnnotationMappingProperties)
				xref.setProperties((!XrefAnnotationMapping.hasName(xref.getDatabaseName())) ? new ArrayList<>(propsMap.get(xref.getDbXrefId())) : new ArrayList<DbXrefProperty>());
			else
				xref.setProperties(new ArrayList<>(propsMap.get(xref.getDbXrefId())));
			xref.setResolvedUrl(resolveLinkTarget(uniqueName, xref));
		}
	}

	private String resolveLinkTarget(String primaryId, DbXref xref) {
		primaryId = primaryId.startsWith("NX_") ? primaryId.substring(3) : primaryId;
		if (! xref.getLinkUrl().contains("%u")) {
			return xref.resolveLinkTarget();
		}

		String templateURL = xref.getLinkUrl();
		if (!templateURL.startsWith("http")) {
			templateURL = "http://" + templateURL;
		}
		
		if (xref.getDatabaseName().equalsIgnoreCase("brenda")) {
			if (xref.getAccession().startsWith("BTO")) {
			    String accession = xref.getAccession().replace(":", "_");
				templateURL = CvDatabasePreferredLink.BRENDA_BTO.getLink().replace("%s", accession);
			}
			else {
				templateURL = templateURL.replaceFirst("%s1", xref.getAccession());
				String organismId = "247";
				// this.retrievePropertyByName("organism name").getPropertyValue();
				// organism always human: hardcode it
				templateURL = templateURL.replaceFirst("%s2", organismId);
			}
		}

		return templateURL.replaceAll("%u", primaryId);
	}

	@Override
	public List<DbXref> findDbXrefByAccession(String accession) {
		List<DbXref> xrefs = this.dbXRefDao.findDbXrefByAccession(accession);
		
//		for(DbXref xref : xrefs)
//			xref.setResolvedUrl(resolveLinkTarget(xref));

		return xrefs;	
	}

	@Override
	public List<DbXref> findAllDbXrefs() {
		List<DbXref> xrefs = this.dbXRefDao.findAllDbXrefs();
		
//		for(DbXref xref : xrefs)
//			xref.setResolvedUrl(resolveLinkTarget(xref));

		return xrefs;
	}

	@Override
	public List<DbXref> findDbXRefByResourceId(Long resourceId) {
		return this.dbXRefDao.findDbXrefByResourceId(resourceId);
	}

	@Override
	public List<Long> getAllDbXrefsIds() {
		return this.dbXRefDao.getAllDbXrefsIds();
	}

	private static class DbXrefExcludedPropertyPredicate implements Predicate<DbXrefProperty> {

		private Set<String> excludedProperties;

		public DbXrefExcludedPropertyPredicate(Set<String> excludedProperties) {

			this.excludedProperties = excludedProperties;
		}

		@Override
		public boolean apply(DbXrefProperty property) {

			return !excludedProperties.contains(property.getName());
		}
	}

}
