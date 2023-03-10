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
	NextprotCellosaurusCv,
	NextprotDomainCv(false),
	NextprotFamilyCv,
	NextprotIcepoCv,
	NextprotMetalCv(false),
	NextprotModificationEffectCv,
	NextprotProteinPropertyCv(false),
	NextprotTopologyCv(false),
	NonStandardAminoAcidCv(false),
	OmimCv(false),
	OrganelleCv(false),
	SequenceOntologyCv(false),
	UnipathwayCv,
	UniprotDiseaseCv(false),
	UniprotFamilyCv,
	UniprotKeywordCv,
	UniprotPtmCv(false),
	UniprotSubcellularLocationCv,
	UniprotSubcellularOrientationCv(false),
	UniprotSubcellularTopologyCv,
	PsiMiCv
    ;

	private final boolean isHierarchical;

    TerminologyCv() {

        this(true);
    }

    TerminologyCv(boolean isHierarchical) {

        this.isHierarchical = isHierarchical;
    }

    public boolean isHierarchical() {

        return isHierarchical;
    }

	public static TerminologyCv getTerminologyOf(String terminology){
		
		String term = StringUtils.toCamelCase(terminology, false);
		
		for(TerminologyCv t : values()){
			if(t.name().equalsIgnoreCase(term))
				return t;
		}
		
		throw new NextProtException(terminology + " terminology is not found");
	}

}
