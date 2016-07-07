package com.nextprot.api.isoform.mapper.domain.impl;

import com.nextprot.api.isoform.mapper.domain.IsoformFeature;
import org.nextprot.api.commons.bio.AminoAcidCode;
import org.nextprot.api.commons.bio.AminoAcidModification;


/**
 * /share/common/Calipho/bioeditor/_tech_docs/be_tech_note_05_molecular_entities.txt
 *
 * <List of defined PTM labels>
 * Ac     Acetyl            KINASE-Ac-Ser123
 * carb   Carbohydrate      KINASE-carb-Asn123
 * dimethyl Dimethylation   KINASE-dimethyl-Arg123
 * ger    Geranylgeranyl    KINASE-ger-Cys123
 * SFarn  Farnesylation     KINASE-SFarn-Cys123      PTM-0277
 * myr    Myristate         KINASE-myr-Gly123
 * nitro  Nitration         KINASE-nitro-Tyr123     PTM-0213
 * P      Phospho           KINASE-P-Ser123
 * palm   Palmitate         KINASE-palm-Cys123
 * PAR    PolyADP-ribose    KINASE-PAR-Glu123
 * SNO    S-nitrosocysteine KINASE-SNO-Cys123      PTM-0280
 * sumo   SUMO              KINASE-sumo-Lys123
 * ubi    Ubiquitin         KINASE-ubi-Lys123
 */
public class PtmFeature implements IsoformFeature {

    private final AminoAcidCode aa;
    private final int position;
    private final AminoAcidModification modification;

    public PtmFeature(AminoAcidCode aa, int position, AminoAcidModification modification) {
        this.aa = aa;
        this.position = position;
        this.modification = modification;
    }

    @Override
    public AminoAcidCode getFirstChangingAminoAcid() {
        return aa;
    }

    @Override
    public int getFirstChangingAminoAcidPos() {
        return position;
    }

    @Override
    public AminoAcidCode getLastChangingAminoAcid() {
        return aa;
    }

    @Override
    public int getLastChangingAminoAcidPos() {
        return position;
    }
}
