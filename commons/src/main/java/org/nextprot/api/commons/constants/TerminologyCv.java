package org.nextprot.api.commons.constants;

import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.commons.utils.StringUtils;

public enum TerminologyCv {
	
	BgeeDevelopmentalStageCv,
	NextprotCellosaurusCv,
	EnzymeClassificationCv,
	EvidenceCodeOntologyCv,
	EvocDevelopmentalStageCv,
	GoBiologicalProcessCv,
	GoCellularComponentCv,
	GoMolecularFunctionCv,
	MeshAnatomyCv,
	MeshCv,
	NciMetathesaurusCv,
	NciThesaurusCv,
	NextprotAnatomyCv,
	NextprotAnnotationCv,
	NextprotCarbohydrateCv,
	NextprotDomainCv,
	NextprotFamilyCv,
	NextprotMetalCv,
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
	UniprotSubcellularTopologyCv,
	NextprotModificationEffectCv,
	NextprotProteinPropertyCv,
	MammalianPhenotypeCv;
	

	public static TerminologyCv getTerminologyOf(String terminology){
		
		String term = StringUtils.toCamelCase(terminology, false);
		
		for(TerminologyCv t : values()){
			if(t.name().equalsIgnoreCase(term))
				return t;
		}
		
		throw new NextProtException(terminology + " terminology is not found");
	}

}
