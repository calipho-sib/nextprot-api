package org.nextprot.api.commons.bio.mutation.hgv;

import org.nextprot.api.commons.bio.mutation.MutatedAAsFormat;
import org.nextprot.api.commons.bio.mutation.ProteinMutation;
import org.nextprot.api.commons.bio.mutation.ProteinMutationFormat;

import static org.nextprot.api.commons.bio.mutation.AbstractProteinMutationFormat.formatAminoAcidCode;

/**
 * HGV implementation of mutated aas
 *
 * Created by fnikitin on 07/09/15.
 */
public class MutatedAAsHGVFormat implements MutatedAAsFormat {

    @Override
    public void format(StringBuilder sb, ProteinMutation proteinMutation, ProteinMutationFormat.AACodeType type) {

        sb.append("p.");

        sb.append(formatAminoAcidCode(type, proteinMutation.getFirstAffectedAminoAcidCode()));
        sb.append(proteinMutation.getFirstAffectedAminoAcidPos());
        if (proteinMutation.isAminoAcidRange())
            sb.append("_").append(formatAminoAcidCode(type, proteinMutation.getLastAffectedAminoAcidCode())).append(proteinMutation.getLastAffectedAminoAcidPos());
    }
}
