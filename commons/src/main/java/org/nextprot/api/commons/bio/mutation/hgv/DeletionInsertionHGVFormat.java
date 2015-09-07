package org.nextprot.api.commons.bio.mutation.hgv;

import org.nextprot.api.commons.bio.AminoAcidCode;
import org.nextprot.api.commons.bio.mutation.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by fnikitin on 07/09/15.
 */
public class DeletionInsertionHGVFormat implements MutationEffectFormat<DeletionAndInsertion> {

    private static final Pattern DELETION_INSERTION_PATTERN = Pattern.compile("^p\\.([A-Z])([a-z]{2})?(\\d+)(?:_([A-Z])([a-z]{2})?(\\d+))?delins((?:[A-Z\\*]([a-z]{2})?)+)$");
    private static final Pattern DELETION_INSERTION_PATTERN_PERMISSIVE = Pattern.compile("^p\\.([A-Z])([a-z]{2})?(\\d+)(?:_([A-Z])([a-z]{2})?(\\d+))?(?:delins|>)((?:[A-Z\\*]([a-z]{2})?)+)$");

    @Override
    public ProteinMutation parseWithMode(String source, ProteinMutation.FluentBuilder builder, AbstractProteinMutationFormat.ParsingMode mode) {

        Matcher m = (mode == AbstractProteinMutationFormat.ParsingMode.STRICT) ? DELETION_INSERTION_PATTERN.matcher(source) : DELETION_INSERTION_PATTERN_PERMISSIVE.matcher(source);

        if (m.matches()) {

            AminoAcidCode affectedAAFirst = AbstractProteinMutationFormat.valueOfAminoAcidCode(m.group(1), m.group(2));
            int affectedAAPosFirst = Integer.parseInt(m.group(3));

            AminoAcidCode[] insertedAAs = AminoAcidCode.valueOfCodeSequence(m.group(7));

            if (m.group(4) == null) return builder.aminoAcid(affectedAAFirst, affectedAAPosFirst)
                    .deletedAndInserts(insertedAAs).build();

            AminoAcidCode affectedAALast = AbstractProteinMutationFormat.valueOfAminoAcidCode(m.group(4), m.group(5));
            int affectedAAPosLast = Integer.parseInt(m.group(6));

            return builder.aminoAcids(affectedAAFirst, affectedAAPosFirst, affectedAALast, affectedAAPosLast)
                    .deletedAndInserts(insertedAAs).build();
        }

        return null;
    }

    @Override
    public void format(StringBuilder sb, DeletionAndInsertion mutation, ProteinMutationFormat.AACodeType type) {

        sb.append("delins").append(AbstractProteinMutationFormat.formatAminoAcidCode(type, mutation.getValue()));
    }
}
