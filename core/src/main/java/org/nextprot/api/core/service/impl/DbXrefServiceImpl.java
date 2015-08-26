package org.nextprot.api.core.service.impl;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

import org.nextprot.api.commons.constants.IdentifierOffset;
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
import org.nextprot.api.core.service.PeptideMappingService;
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
	@Autowired private PeptideMappingService peptideMappingService;
	@Autowired private IsoformService isoService;
	@Autowired private PeptideNamesService peptideNamesService;
	
	private Set<String> dbXrefPropertyFilter;
	
	{
		this.dbXrefPropertyFilter = new HashSet<>();
		this.dbXrefPropertyFilter.add("status");
		this.dbXrefPropertyFilter.add("match status");
		this.dbXrefPropertyFilter.add("organism ID");
		this.dbXrefPropertyFilter.add("organism name");
	}
	
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
	
	private List<Annotation> createAdditionalAnnotationsFromXrefs(List<DbXref> xrefs, String entryName) {
		List<Annotation> annots = new ArrayList<>();
		for (DbXref xref: xrefs) {
			Annotation annotation = new Annotation();
			annotation.setProperties(new ArrayList<AnnotationProperty>());
			annotation.setAnnotationId(xref.getDbXrefId() + IdentifierOffset.XREF_ANNOTATION_OFFSET);
			XrefAnnotationMapping xam = XrefAnnotationMapping.getByDatabaseName(xref.getDatabaseName());
			annotation.setCategory(xam.getAnnotCat());
			annotation.setDescription(xref.getPropertyValue(xam.getXrefPropName())); // copy of some xref property 
			annotation.setQualityQualifier(xam.getQualityQualifier()); 
			annotation.setCvTermName(null);
			annotation.setCvTermAccessionCode(null);
			annotation.setSynonym(null);
			annotation.setUniqueName("AN_" + entryName.substring(3) + "_XR_" + String.valueOf(xref.getDbXrefId()) );
			annotation.setParentXref(xref);
			annots.add(annotation);
		}
		return annots;
	}
	
	
	@Override
	@Cacheable("xrefs-as-annot")
	public List<Annotation> findDbXrefsAsAnnotationsByEntry(String entryName) {
	    // build annotations from xrefs
		List<Isoform> isoforms = this.isoService.findIsoformsByEntryName(entryName);
		List<DbXref> xrefs = this.findDbXrefsAsAnnotByEntry(entryName);
		List<Annotation> annotations = this.createAdditionalAnnotationsFromXrefs(xrefs, entryName);
		
		// build evidences annotations and link them to annotations
		for (Annotation annotation : annotations) {
			List<AnnotationEvidence> evidences = new ArrayList<>();
			AnnotationEvidence evidence = new AnnotationEvidence();
			evidence.setAnnotationId(annotation.getAnnotationId());
			DbXref pxref = annotation.getParentXref();
			XrefAnnotationMapping xam = XrefAnnotationMapping.getByDatabaseName(pxref.getDatabaseName());
			evidence.setEvidenceId(annotation.getAnnotationId() + IdentifierOffset.XREF_ANNOTATION_EVIDENCE_OFFSET);
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
			evidences.add(evidence);
			annotation.setEvidences(evidences);
		}
		
		// build isoform specificity from isoforms and annotations and link them to annotations
		for (Annotation annotation : annotations) {
			List<AnnotationIsoformSpecificity> isospecs = new ArrayList<>();
			for (Isoform iso: isoforms) {
				AnnotationIsoformSpecificity isospec = new AnnotationIsoformSpecificity();
				isospec.setAnnotationId(annotation.getAnnotationId());
				isospec.setFirstPosition(0);
				isospec.setLastPosition(0);
				isospec.setIsoformName(iso.getUniqueName());
				isospec.setSpecificity("UNKNOWN");
				isospecs.add(isospec);
			}
			annotation.setTargetingIsoforms(isospecs);
			
		}

		//returns a immutable list when the result is cacheable (this prevents modifying the cache, since the cache returns a reference) copy on read and copy on write is too much time consuming
		return new ImmutableList.Builder<Annotation>().addAll(annotations).build();
		
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
		if (! xrefList.isEmpty()) attachPropertiesToXrefs(xrefList, entryName);

		//returns a immutable list when the result is cacheable (this prevents modifying the cache, since the cache returns a reference) copy on read and copy on write is too much time consuming
		return new ImmutableList.Builder<DbXref>().addAll(xrefList).build();
	}
	
	
	private List<DbXref> findDbXrefsAsAnnotByEntry(String uniqueName) {
		List<DbXref> xrefs = this.dbXRefDao.findDbXrefsAsAnnotByMaster(uniqueName);
		if(! xrefs.isEmpty()) attachPropertiesToXrefs(xrefs, uniqueName);
		return xrefs;
	}

	/**
	 * 	private propertyNotPrinted = [
		'status',
		'match status',
		'organism ID',
		'organism name',
	];
	 * @param xrefs
	 * @return
	 */
	private void attachPropertiesToXrefs(List<DbXref> xrefs, String uniqueName) {
		List<Long> xrefIds = Lists.transform(xrefs, new Function<DbXref, Long>() {
			public Long apply(DbXref xref) {
				return xref.getDbXrefId();
			}
		});

		List<DbXrefProperty> props = this.dbXRefDao.findDbXrefsProperties(xrefIds);
		
		Iterator<DbXrefProperty> it = props.iterator();
		
		while(it.hasNext()) {
			if(this.dbXrefPropertyFilter.contains(it.next().getName()))
				it.remove();
		}
				
		Multimap<Long, DbXrefProperty> propsMap = Multimaps.index(props, new Function<DbXrefProperty, Long>() {
			public Long apply(DbXrefProperty prop) {
				return prop.getDbXrefId();
			}
		});

		for (DbXref xref : xrefs) {
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
	
}
