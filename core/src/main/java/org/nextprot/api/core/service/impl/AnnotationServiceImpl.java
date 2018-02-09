package org.nextprot.api.core.service.impl;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import org.apache.commons.lang.StringUtils;
import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.commons.constants.IdentifierOffset;
import org.nextprot.api.commons.constants.TerminologyCv;
import org.nextprot.api.core.dao.AnnotationDAO;
import org.nextprot.api.core.dao.BioPhyChemPropsDao;
import org.nextprot.api.core.dao.PtmDao;
import org.nextprot.api.core.domain.CvTerm;
import org.nextprot.api.core.domain.ExperimentalContext;
import org.nextprot.api.core.domain.Feature;
import org.nextprot.api.core.domain.Isoform;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.domain.annotation.AnnotationEvidence;
import org.nextprot.api.core.domain.annotation.AnnotationEvidenceProperty;
import org.nextprot.api.core.domain.annotation.AnnotationIsoformSpecificity;
import org.nextprot.api.core.domain.annotation.AnnotationProperty;
import org.nextprot.api.core.service.AnnotationService;
import org.nextprot.api.core.service.AntibodyMappingService;
import org.nextprot.api.core.service.DbXrefService;
import org.nextprot.api.core.service.ExperimentalContextDictionaryService;
import org.nextprot.api.core.service.InteractionService;
import org.nextprot.api.core.service.IsoformService;
import org.nextprot.api.core.service.MdataService;
import org.nextprot.api.core.service.PeptideMappingService;
import org.nextprot.api.core.service.TerminologyService;
import org.nextprot.api.core.utils.TerminologyUtils;
import org.nextprot.api.core.utils.annot.AnnotationUtils;
import org.nextprot.api.core.utils.graph.CvTermGraph;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.nextprot.api.annotation.builder.statement.service.StatementService;

@Service
public class AnnotationServiceImpl implements AnnotationService {

	@Autowired private AnnotationDAO annotationDAO;
	@Autowired private MdataService mdataService;
	@Autowired private PtmDao ptmDao;
	@Autowired private DbXrefService xrefService;
	@Autowired private InteractionService interactionService;
	@Autowired private BioPhyChemPropsDao bioPhyChemPropsDao;
	@Autowired private IsoformService isoformService;
	@Autowired private PeptideMappingService peptideMappingService;
	@Autowired private AntibodyMappingService antibodyMappingService;
	@Autowired private StatementService statementService;
	@Autowired private TerminologyService terminologyService;
	@Autowired private ExperimentalContextDictionaryService experimentalContextDictionaryService;
	
	@Override
	@Cacheable("annotations")
	public List<Annotation> findAnnotations(String entryName) {
		return findAnnotations(entryName,false);
	}

	/**
	 * pam: useful for test AnnotationServiceTest to work and for other tests
	 */
	@Override
	public List<Annotation> findAnnotationsExcludingBed(String entryName) {
		return findAnnotations(entryName,true);
	}

	private List<Annotation> findAnnotations(String entryName, boolean ignoreStatements) {

		Preconditions.checkArgument(entryName != null, "The annotation name should be set wit #withName(...)");
		
		List<Annotation> annotations = annotationDAO.findAnnotationsByEntryName(entryName);
		if (! annotations.isEmpty()) {

			List<Long> annotationIds = Lists.transform(annotations, new AnnotationFunction());
	
			// Evidences
			List<AnnotationEvidence> evidences = annotationDAO.findAnnotationEvidencesByAnnotationIds(annotationIds);
			Multimap<Long, AnnotationEvidence> evidencesByAnnotationId = Multimaps.index(evidences, new AnnotationEvidenceFunction());
			for (Annotation annotation : annotations) {
				annotation.setEvidences(new ArrayList<>(evidencesByAnnotationId.get(annotation.getAnnotationId())));
			}
	
			// Evidences properties
			if(!evidences.isEmpty()){
				List<Long> evidencesIds = Lists.transform(evidences, new AnnotationEvidenceIdFunction());
				List<AnnotationEvidenceProperty> evidenceProperties = annotationDAO.findAnnotationEvidencePropertiesByEvidenceIds(evidencesIds);
				Multimap<Long, AnnotationEvidenceProperty> propertiesByEvidenceId = Multimaps.index(evidenceProperties, new AnnotationEvidencePropertyFunction());
				for (AnnotationEvidence evidence : evidences) {
					evidence.setProperties(new ArrayList<>(propertiesByEvidenceId.get(evidence.getEvidenceId())));
				}
			}
	
			// Isoforms
			List<AnnotationIsoformSpecificity> isoforms = annotationDAO.findAnnotationIsoformsByAnnotationIds(annotationIds);
			Multimap<Long, AnnotationIsoformSpecificity> isoformsByAnnotationId = Multimaps.index(isoforms, new AnnotationIsoformFunction());
	
			for (Annotation annotation : annotations) {
				annotation.addTargetingIsoforms(new ArrayList<>(isoformsByAnnotationId.get(annotation.getAnnotationId())));
			}
	
			// Properties
			List<AnnotationProperty> properties = annotationDAO.findAnnotationPropertiesByAnnotationIds(annotationIds);
			Multimap<Long, AnnotationProperty> propertiesByAnnotationId = Multimaps.index(properties, new AnnotationPropertyFunction());
	
			for (Annotation annotation : annotations) {
				annotation.addProperties(propertiesByAnnotationId.get(annotation.getAnnotationId()));
			}
			// Removes annotations which do not map to any isoform, 
			// this may happen in case where the annotation has been seen in a peptide and the annotation was propagated to the master, 
			// but we were not able to map to any isoform
			Iterator<Annotation> annotationsIt = annotations.iterator();
			while(annotationsIt.hasNext()){
				Annotation a = annotationsIt.next();
				if(a.getTargetingIsoformsMap().size() == 0){
					annotationsIt.remove();
				}
			}
	
			
			AnnotationUtils.convertRelativeEvidencesToProperties(annotations); // CALIPHOMISC-277

			for (Annotation annot : annotations) {
				refactorDescription(annot);
			}
		}
		
		annotations.addAll(this.xrefService.findDbXrefsAsAnnotationsByEntry(entryName));
		annotations.addAll(this.interactionService.findInteractionsAsAnnotationsByEntry(entryName));
		annotations.addAll(this.peptideMappingService.findNaturalPeptideMappingAnnotationsByMasterUniqueName(entryName));
		annotations.addAll(this.peptideMappingService.findSyntheticPeptideMappingAnnotationsByMasterUniqueName(entryName));		
		annotations.addAll(this.antibodyMappingService.findAntibodyMappingAnnotationsByUniqueName(entryName));		

		annotations.addAll(bioPhyChemPropsToAnnotationList(entryName, this.bioPhyChemPropsDao.findPropertiesByUniqueName(entryName)));

		if (!ignoreStatements) annotations = AnnotationUtils.mapReduceMerge(statementService.getAnnotations(entryName), annotations);

		// post-processing of annotations
		updateIsoformsDisplayedAsSpecific(annotations, entryName);
		updateVariantsRelatedToDisease(annotations);
		updateSubcellularLocationTermNameWithAncestors(annotations);
		updateMiscRegionsRelatedToInteractions(annotations);
		updatePtmAndPeptideMappingWithMdata(annotations, entryName);
		
		//returns a immutable list when the result is cache-able (this prevents modifying the cache, since the cache returns a reference)
		return new ImmutableList.Builder<Annotation>().addAll(annotations).build();
	}

	private void updateSubcellularLocationTermNameWithAncestors(List<Annotation> annotations) {
		
		//long t0 = System.currentTimeMillis(); System.out.println("updateSubcellularLocationTermNameWithAncestors...");

		for (Annotation annot: annotations) {
			if (AnnotationCategory.SUBCELLULAR_LOCATION == annot.getAPICategory()) {
				CvTerm t = terminologyService.findCvTermByAccession(annot.getCvTermAccessionCode());
				List<CvTerm> terms = TerminologyUtils.getOnePathToRootTerm(t.getAccession(), terminologyService);
				String longName = AnnotationUtils.getTermNameWithAncestors(annot, terms);
				AnnotationProperty prop = new AnnotationProperty();
				prop.setAnnotationId(annot.getAnnotationId());
				prop.setName("long-name");
				prop.setValue(String.valueOf(longName));
				annot.addProperty(prop);
				String descr = annot.getDescription();
				if (descr != null && !annot.getCvTermName().equals(descr)) {
					// 3 cases: "Main location", "Additional localtion" or "Note=..."
					if (descr.startsWith("Note=")) descr=descr.substring(5); 
					prop = new AnnotationProperty();
					prop.setAnnotationId(annot.getAnnotationId());
					prop.setName("name-modifier");
					prop.setValue(String.valueOf(descr));
					annot.addProperty(prop);
				}
			}
		}
		
		//System.out.println("updateSubcellularLocationTermNameWithAncestors DONE in " + (System.currentTimeMillis() - t0) + "ms");
		
	}
	
	private void updateVariantsRelatedToDisease(List<Annotation> annotations) {
		
		Map<Long,ExperimentalContext> ecMap = experimentalContextDictionaryService.getAllExperimentalContexts();
		
		//long t0 = System.currentTimeMillis(); System.out.println("updateVariantsRelatedToDisease...");

		// add property if annotation is a variant related to disease
		for (Annotation annot: annotations) {
			if (AnnotationCategory.VARIANT == annot.getAPICategory()) {
				boolean result = AnnotationUtils.isVariantRelatedToDiseaseProperty(annot, ecMap);
				AnnotationProperty prop = new AnnotationProperty();
				prop.setAnnotationId(annot.getAnnotationId());
				prop.setName("disease-related");
				prop.setValue(String.valueOf(result));
				annot.addProperty(prop);
			}
		}

		//System.out.println("updateVariantsRelatedToDisease DONE in " + (System.currentTimeMillis() - t0) + "ms");

	}
	
	
	private void updatePtmAndPeptideMappingWithMdata(List<Annotation> annotations, String entryName) {
		
		Map<Long,Long> evidenceMdataMap = mdataService.findEvidenceIdMdataIdMapByEntryName(entryName);
		annotations.stream().filter(a -> isAnnotationWithPotentialMdata(a)).forEach(a -> { 
			a.getEvidences().forEach(e -> {
				e.setMdataId(evidenceMdataMap.get(e.getEvidenceId()));
			});
		});
	}
	
	private boolean isAnnotationWithPotentialMdata(Annotation a) {
		if (a.getAPICategory()==AnnotationCategory.PEPTIDE_MAPPING) return true;
		if (a.getAPICategory()==AnnotationCategory.MODIFIED_RESIDUE) return true;
		if (a.getAPICategory()==AnnotationCategory.GLYCOSYLATION_SITE) return true;
		if (a.getAPICategory()==AnnotationCategory.CROSS_LINK ) return true;
		return false;
	}
	
	private void updateMiscRegionsRelatedToInteractions(List<Annotation> annotations) {

		// add property if annotation is a misc region and it is related to interaction
		for (Annotation annot: annotations) {
			if (AnnotationCategory.MISCELLANEOUS_REGION == annot.getAPICategory()) {
				boolean result = AnnotationUtils.isMiscRegionRelatedToInteractions(annot);
				AnnotationProperty prop = new AnnotationProperty();
				prop.setAnnotationId(annot.getAnnotationId());
				prop.setName("interaction-related");
				prop.setValue(String.valueOf(result));
				annot.addProperty(prop);
			}
		}
	}
	
	
	
	private void updateIsoformsDisplayedAsSpecific(List<Annotation> annotations, String entryName) {
		
		//long t0 = System.currentTimeMillis(); System.out.println("updateIsoformsDisplayedAsSpecific...");
		
		List<Isoform> isoforms = isoformService.findIsoformsByEntryName(entryName);
		int entryIsoformCount=isoforms.size();
		for (Annotation annot: annotations) {
			annot.setIsoformsDisplayedAsSpecific(AnnotationUtils.computeIsoformsDisplayedAsSpecific(annot, entryIsoformCount));
		}
		//System.out.println("updateIsoformsDisplayedAsSpecific DONE in " + (System.currentTimeMillis() - t0) + "ms");

	}
	
	private List<Annotation> bioPhyChemPropsToAnnotationList(String entryName, List<AnnotationProperty> props) {

		List<Annotation> annotations = new ArrayList<>(props.size());

		List<Isoform> isoforms = isoformService.findIsoformsByEntryName(entryName);

		for(AnnotationProperty property :  props){

			Annotation annotation = new Annotation();

			AnnotationCategory model = AnnotationCategory.getByDbAnnotationTypeName(property.getName());
			String description = property.getValue();

			
			annotation.setAnnotationId(property.getAnnotationId() + IdentifierOffset.BIOPHYSICOCHEMICAL_ANNOTATION_OFFSET);
			annotation.setCategory(model.getDbAnnotationTypeName());
			annotation.setDescription(description);
			annotation.setEvidences(new ArrayList<>());

			annotation.setQualityQualifier("GOLD");
			annotation.setUniqueName(entryName + "_" + model.getApiTypeName());
			annotation.addTargetingIsoforms(newAnnotationIsoformSpecificityList(annotation.getAnnotationId(), isoforms));

			annotations.add(annotation);
		}

		return annotations;
	}

	private List<AnnotationIsoformSpecificity> newAnnotationIsoformSpecificityList(long annotationId, List<Isoform> isoforms) {

		List<AnnotationIsoformSpecificity> specs = new ArrayList<>(isoforms.size());

		for (Isoform isoform : isoforms) {

			AnnotationIsoformSpecificity spec = new AnnotationIsoformSpecificity();

			spec.setAnnotationId(annotationId);
			spec.setIsoformAccession(isoform.getIsoformAccession());
			spec.setSpecificity("UNKNOWN");

			specs.add(spec);
		}

		return specs;
	}

	@Override
	public List<Feature> findPtmsByMaster(String uniqueName) {
		return this.ptmDao.findPtmsByEntry(uniqueName);
	}

	@Override
	public List<Feature> findPtmsByIsoform(String isoformUniqueName) {
		String masterUniqueName = extractMasterUniqueName(isoformUniqueName);
		List<Feature> ptms = this.ptmDao.findPtmsByEntry(masterUniqueName);
		return filterByIsoform(isoformUniqueName, ptms);
	}

	@Override
	public Predicate<Annotation> createDescendantTermPredicate(String ancestorAccession) {

		return new BaseCvTermAncestorPredicate<Annotation>(terminologyService.findCvTermByAccession(ancestorAccession)) {

			@Override
			public boolean test(Annotation annotation) {
				try {
					return annotation.getCvTermAccessionCode() != null &&
							(annotation.getCvTermAccessionCode().equals(ancestor.getAccession()) ||
									graph.isAncestorOf(ancestor.getId().intValue(), graph.getCvTermIdByAccession(annotation.getCvTermAccessionCode())));
				} catch (CvTermGraph.NotFoundNodeException e) {
					return false;
				}
			}
		};
	}

	@Override
	public Predicate<AnnotationEvidence> createDescendantEvidenceTermPredicate(String ancestorEvidenceCode) {

		return new BaseCvTermAncestorPredicate<AnnotationEvidence>(terminologyService.findCvTermByAccession(ancestorEvidenceCode)) {

			@Override
			public boolean test(AnnotationEvidence annotationEvidence) {

				try {
					return annotationEvidence.getEvidenceCodeAC() != null &&
							(annotationEvidence.getEvidenceCodeAC().equals(ancestor.getAccession()) ||
									graph.isAncestorOf(ancestor.getId().intValue(), graph.getCvTermIdByAccession(annotationEvidence.getEvidenceCodeAC())));
				} catch (CvTermGraph.NotFoundNodeException e) {
					return false;
				}
			}
		};
	}

	@Override
	public Predicate<Annotation> buildPropertyPredicate(String propertyName, @Nullable String propertyValueOrAccession) {

		if (propertyName != null && !propertyName.isEmpty()) {

			Predicate<Annotation> propExistencePredicate = annotation -> annotation.getPropertiesMap().containsKey(propertyName);

			if (propertyValueOrAccession != null && !propertyValueOrAccession.isEmpty()) {

				return propExistencePredicate.and(annotation -> {

					Collection<AnnotationProperty> props = annotation.getPropertiesByKey(propertyName);

					return props.stream().anyMatch(annotationProperty ->
							propertyValueOrAccession.equals(annotationProperty.getValue()) ||
									propertyValueOrAccession.equals(annotationProperty.getAccession()));
				});
			}
			return propExistencePredicate;
		}

		return annotation -> true;
	}

	private abstract class BaseCvTermAncestorPredicate<T> implements Predicate<T> {

		protected final CvTerm ancestor;
		protected final CvTermGraph graph;

		private BaseCvTermAncestorPredicate(CvTerm ancestor) {

			this.ancestor = ancestor;
			graph = terminologyService.findCvTermGraph(TerminologyCv.valueOf(ancestor.getOntology()));
		}
	}


	// Function class to filter using guava ///////////////////////////////////////////////////////////////////////////////

	private class AnnotationPropertyFunction implements Function<AnnotationProperty, Long> {
		public Long apply(AnnotationProperty annotation) {
			return annotation.getAnnotationId();
		}
	}

	private class AnnotationIsoformFunction implements Function<AnnotationIsoformSpecificity, Long> {
		public Long apply(AnnotationIsoformSpecificity annotation) {
			return annotation.getAnnotationId();
		}
	}

	private class AnnotationEvidenceFunction implements Function<AnnotationEvidence, Long> {
		public Long apply(AnnotationEvidence annotation) {
			return annotation.getAnnotationId();
		}
	}

	private class AnnotationEvidencePropertyFunction implements Function<AnnotationEvidenceProperty, Long> {
		public Long apply(AnnotationEvidenceProperty property) {
			return property.getEvidenceId();
		}
	}

	private class AnnotationFunction implements Function<Annotation, Long> {
		public Long apply(Annotation annotation) {
			return annotation.getAnnotationId();
		}
	}

	private class AnnotationEvidenceIdFunction implements Function<AnnotationEvidence, Long> {
		public Long apply(AnnotationEvidence evidence) {
			return evidence.getEvidenceId();
		}
	}
	
	// Refactor the descriptions  ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	private static void refactorDescription(Annotation annotation) {

		String category = annotation.getCategory();

		if (annotation.getDescription() == null || annotation.getDescription().indexOf(':') == 1) {
			annotation.setDescription(annotation.getCvTermName());
		}

		if (category != null) {
			if ("sequence caution".equals(category)) {
				setSequenceCautionDescription(annotation);
			} else if ("go molecular function".equals(category) || "go cellular component".equals(category) || "go biological process".equals(category)) {
				setGODescription(annotation);
			} else if ("sequence conflict".equals(category) || "sequence variant".equals(category) || "mutagenesis site".equals(category)) {
				setVariantDescription(annotation);
			}
		}
	}

	private static void setSequenceCautionDescription(Annotation annotation) {

		SortedSet<String> acs = new TreeSet<>();

		for (AnnotationProperty ap : annotation.getProperties()) {
			if ("differing sequence".equals(ap.getName()))
				acs.add(ap.getAccession());
		}


		StringBuilder sb = new StringBuilder("The sequence").append(acs.size() > 1 ? "s" : "");
		for (String emblAc : acs) {
			sb.append(" ").append(emblAc);
		}
		sb.append(" differ").append(acs.size() == 1 ? "s" : "").append(" from that shown.");

		// Beginning of the sentence finish, then:
		List<AnnotationProperty> conflictTypeProps = new ArrayList<>();
		for (AnnotationProperty ap : annotation.getProperties()) {
			if ("conflict type".equals(ap.getName()))
				conflictTypeProps.add(ap);
		}

		SortedSet<AnnotationProperty> sortedPositions = getSortedPositions(annotation);
		if (conflictTypeProps != null && !conflictTypeProps.isEmpty()) {
			sb.append(" Reason:");
			for (AnnotationProperty prop : conflictTypeProps) {
				sb.append(" ").append(prop.getValue());
			}
			if (!sortedPositions.isEmpty()) {
				sb.append(" at position").append(sortedPositions.size() > 1 ? "s" : "");
				for (AnnotationProperty p : sortedPositions) {
					sb.append(" ").append(p.getValue());
				}
			}
			sb.append(".");
		}
		if (StringUtils.isNotEmpty(annotation.getDescription())) {
			sb.append(" ").append(annotation.getDescription());
		}

		annotation.setDescription(sb.toString());
	}

	private static SortedSet<AnnotationProperty> getSortedPositions(Annotation annotation) {
		SortedSet<AnnotationProperty> sortedPositions = new TreeSet<>((p1, p2) -> Integer.valueOf(p1.getValue()).compareTo(Integer.valueOf(p2.getValue())));

		List<AnnotationProperty> conflictPositions = new ArrayList<>();
		for (AnnotationProperty ap : annotation.getProperties()) {
			if ("position".equals(ap.getName()))
				conflictPositions.add(ap);
		}

		if (conflictPositions != null && !conflictPositions.isEmpty()) {
			sortedPositions.addAll(conflictPositions);
		}
		return sortedPositions;
	}

	private static void setVariantDescription(Annotation annotation) {

		if (annotation.getVariant() != null && annotation.getVariant().getVariant().isEmpty()) {
			String description = annotation.getDescription();
			annotation.setDescription("Missing " + (description==null ? "": description));
		}
	}

	private static void setGODescription(Annotation annotation) {
		
		//Example if the cv term is : nuclear proteasome complex and there is a GO term of go_qualifier, the description changes to:
		//Then the description Colocalizes with nuclear proteasome complex

		for (AnnotationEvidence evidence : annotation.getEvidences()) {
			if ("evidence".equals(evidence.getResourceAssociationType())) {
				String goqualifier=evidence.getGoQualifier();
				if (goqualifier != null && !goqualifier.isEmpty()) {
					String description = StringUtils.capitalize(goqualifier.replaceAll("_", " ") + " ") + annotation.getDescription();
					annotation.setDescription(description);
					break;
				}
			}
		}
	}

	private List<Feature> filterByIsoform(String isoformUniqueName, List<Feature> annotations) {
		List<Feature> filteredFeatures = new ArrayList<>();
		for (Feature f : annotations) {
			if (f.getIsoformAccession().equalsIgnoreCase(isoformUniqueName)) {
				filteredFeatures.add(f);
			}
		}
		return filteredFeatures;
	}
	
	private String extractMasterUniqueName(String isoformUniqueName) {
		if (isoformUniqueName.indexOf('-') < 1) {
			throw new InvalidParameterException(String.format(
					"Invalid isoform accession [%s]", isoformUniqueName));
		}
		return isoformUniqueName.substring(0, isoformUniqueName.indexOf('-'));
	}
}
