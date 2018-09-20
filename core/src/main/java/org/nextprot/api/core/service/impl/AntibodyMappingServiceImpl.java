package org.nextprot.api.core.service.impl;

import org.nextprot.api.commons.constants.PropertyApiModel;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.core.dao.AntibodyMappingDao;
import org.nextprot.api.core.domain.SequenceUnicity;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.domain.annotation.AnnotationProperty;
import org.nextprot.api.core.service.AntibodyMappingService;
import org.nextprot.api.core.service.MasterIdentifierService;
import org.nextprot.api.core.service.SequenceUnicityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Service
class AntibodyMappingServiceImpl implements AntibodyMappingService {

	@Autowired private MasterIdentifierService masterIdentifierService;
	@Autowired private AntibodyMappingDao antibodyMappingDao;
	@Autowired private SequenceUnicityService sequenceUnicityService;

	@Override
	public List<Annotation> findAntibodyMappingAnnotationsByUniqueName(String entryName) {
		Long masterId = this.masterIdentifierService.findIdByUniqueName(entryName);
		List<Annotation> annotations = antibodyMappingDao.findAntibodyMappingAnnotationsById(masterId);

		attachAntibodyPropertiesToAnnotations(annotations, sequenceUnicityService.getAntibodyNameUnicityMap());

		return annotations;
	}

	private void attachAntibodyPropertiesToAnnotations(List<Annotation> annotations, Map<String, SequenceUnicity> unicityMap) {

		for (Annotation annot: annotations) {
			String abName = getMappingAnnotationAntibodyName(annot);
			SequenceUnicity pu = unicityMap.get(abName);
			String unicityValue = pu.getValue().name();

			AnnotationProperty prop = new AnnotationProperty();
			prop.setAnnotationId(annot.getAnnotationId());
			prop.setAccession(abName);
			prop.setName(PropertyApiModel.NAME_ANTIBODY_UNICITY);
			prop.setValue(unicityValue);

			annot.addProperty(prop);
		}
	}

	private String getMappingAnnotationAntibodyName(Annotation annot) {
		// retrieve antibody acc from annotation properties
		Collection<AnnotationProperty> annotationProperties = annot.getPropertiesMap().get(PropertyApiModel.NAME_ANTIBODY_NAME);
		if (annotationProperties == null) {
			throw new NextProtException("Cannot find property name " + PropertyApiModel.NAME_ANTIBODY_NAME);
		}
		// We only need the first property to retrieve the "antibody name"!
		AnnotationProperty abAccProperty = annot.getPropertiesByKey(PropertyApiModel.NAME_ANTIBODY_NAME).iterator().next();
		return abAccProperty.getValue();
	}
}
