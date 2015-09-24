package org.nextprot.api.commons.bio.mutation;

/**
 * Formats mutated amino-acids
 *
 * Created by fnikitin on 10/07/15.
 */
public interface MutatedAAsFormat {

    void format(StringBuilder sb, ProteinMutation proteinMutation, ProteinMutationFormat.AACodeType type);
}
