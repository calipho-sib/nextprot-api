package org.nextprot.api.commons.bio.mutation.hgv;

import org.nextprot.api.commons.bio.AminoAcidCode;
import org.nextprot.api.commons.bio.mutation.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by fnikitin on 07/09/15.
 */
public class InsertionHGVFormat implements MutationEffectFormat<Insertion> {

    private static final Pattern INSERTION_PATTERN = Pattern.compile("^p\\.([A-Z])([a-z]{2})?(\\d+)_([A-Z])([a-z]{2})?(\\d+)ins((?:[A-Z\\*]([a-z]{2})?)+)$");

    @Override
    public ProteinMutation parseWithMode(String source, ProteinMutation.FluentBuilder builder, AbstractProteinMutationFormat.ParsingMode mode) {

        Matcher m =  INSERTION_PATTERN.matcher(source);

        if (m.matches()) {

            AminoAcidCode affectedAAFirst = AbstractProteinMutationFormat.valueOfAminoAcidCode(m.group(1), m.group(2));
            int affectedAAPosFirst = Integer.parseInt(m.group(3));

            AminoAcidCode affectedAALast = AbstractProteinMutationFormat.valueOfAminoAcidCode(m.group(4), m.group(5));
            int affectedAAPosLast = Integer.parseInt(m.group(6));

            AminoAcidCode[] insertedAAs = AminoAcidCode.valueOfCodeSequence(m.group(7));

            return builder.aminoAcids(affectedAAFirst, affectedAAPosFirst, affectedAALast, affectedAAPosLast)
                    .inserts(insertedAAs).build();
        }

        return null;
    }

    @Override
    public void format(StringBuilder sb, Insertion mutation, ProteinMutationFormat.AACodeType type) {

        sb.append("ins").append(AbstractProteinMutationFormat.formatAminoAcidCode(type, mutation.getValue()));
    }
}
