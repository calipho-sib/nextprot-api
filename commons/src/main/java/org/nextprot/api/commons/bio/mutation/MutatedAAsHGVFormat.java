package org.nextprot.api.commons.bio.mutation;

import static org.nextprot.api.commons.bio.mutation.AbstractProteinMutationFormat.formatAminoAcidCode;

/**
 * Created by fnikitin on 07/09/15.
 */
public class MutatedAAsHGVFormat implements MutatedAAsFormat {

    @Override
    public void format(StringBuilder sb, ProteinMutation proteinMutation, ProteinMutationFormat.AACodeType type) {

        sb.append("p.");

        // affected amino-acid(s)
        sb.append(formatAminoAcidCode(type, proteinMutation.getFirstAffectedAminoAcidCode()));
        sb.append(proteinMutation.getFirstAffectedAminoAcidPos());
        if (proteinMutation.isAminoAcidRange())
            sb.append("_").append(formatAminoAcidCode(type, proteinMutation.getLastAffectedAminoAcidCode())).append(proteinMutation.getLastAffectedAminoAcidPos());
    }
}
