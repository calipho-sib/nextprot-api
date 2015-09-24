package org.nextprot.api.commons.bio.mutation;

import java.text.ParseException;

/**
 * Created by fnikitin on 10/07/15.
 */
public interface MutationEffectFormat<M extends Mutation> {

    void format(StringBuilder sb, M mutation, ProteinMutationFormat.AACodeType type);

    ProteinMutation parseWithMode(String source, ProteinMutation.FluentBuilder builder, AbstractProteinMutationFormat.ParsingMode mode) throws ParseException;
}
