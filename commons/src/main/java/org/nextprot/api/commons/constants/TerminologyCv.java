package org.nextprot.api.commons.constants;

import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.commons.utils.StringUtils;

public enum TerminologyCv {
	
	BgeeDevelopmentalStageCv,
	EnzymeClassificationCv,
	EvidenceCodeOntologyCv,
	EvocDevelopmentalStageCv,
	GoBiologicalProcessCv,
	GoCellularComponentCv,
	GoMolecularFunctionCv,
	MammalianPhenotypeCv,
	MeshAnatomyCv,
	MeshCv,
	NciMetathesaurusCv,
	NciThesaurusCv,
	NextprotAnatomyCv,
	NextprotAnnotationCv,
	NextprotCarbohydrateCv,
	NextprotCellosaurusCv,
	NextprotDomainCv,
	NextprotFamilyCv,
	NextprotIcepoCv,
	NextprotMetalCv,
	NextprotModificationEffectCv,
	NextprotProteinPropertyCv,
	NextprotTopologyCv,
	NonStandardAminoAcidCv,
	OmimCv,
	OrganelleCv,
	SequenceOntologyCv,
	UnipathwayCv,
	UniprotDiseaseCv,
	UniprotFamilyCv,
	UniprotKeywordCv,
	UniprotPtmCv,
	UniprotSubcellularLocationCv,
	UniprotSubcellularOrientationCv,
	UniprotSubcellularTopologyCv;

	

	public static TerminologyCv getTerminologyOf(String terminology){
		
		String term = StringUtils.toCamelCase(terminology, false);
		
		for(TerminologyCv t : values()){
			if(t.name().equalsIgnoreCase(term))
				return t;
		}
		
		throw new NextProtException(terminology + " terminology is not found");
	}

}
