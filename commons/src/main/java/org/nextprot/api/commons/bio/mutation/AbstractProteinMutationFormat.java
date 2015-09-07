package org.nextprot.api.commons.bio.mutation;

import org.nextprot.api.commons.bio.AminoAcidCode;

/**
 * Created by fnikitin on 07/09/15.
 */
public abstract class AbstractProteinMutationFormat implements ProteinMutationFormat {

    public String format(ProteinMutation mutation) {
        return format(mutation, AACodeType.ONE_LETTER);
    }

    @Override
    public String format(ProteinMutation mutation, AACodeType type) {

        StringBuilder sb = new StringBuilder();

        // affected amino acids
        getAffectedAAsFormat().format(sb, mutation, type);

        // mutation
        if (mutation.getMutation() instanceof Deletion) getDeletionFormat().format(sb, (Deletion) mutation.getMutation(), type);
        else if (mutation.getMutation() instanceof Substitution) getSubstitutionFormat().format(sb, (Substitution) mutation.getMutation(), type);
        else if (mutation.getMutation() instanceof DeletionAndInsertion) getDeletionInsertionFormat().format(sb, (DeletionAndInsertion) mutation.getMutation(), type);
        else if (mutation.getMutation() instanceof Insertion) getInsertionFormat().format(sb, (Insertion) mutation.getMutation(), type);
        else if (mutation.getMutation() instanceof Frameshift) getFrameshiftFormat().format(sb, (Frameshift) mutation.getMutation(), type);

        return sb.toString();
    }

    // delegated formatters
    protected abstract MutatedAAsFormat getAffectedAAsFormat();
    protected abstract MutationEffectFormat<Substitution> getSubstitutionFormat();
    protected abstract MutationEffectFormat<Insertion> getInsertionFormat();
    protected abstract MutationEffectFormat<Deletion> getDeletionFormat();
    protected abstract MutationEffectFormat<DeletionAndInsertion> getDeletionInsertionFormat();
    protected abstract MutationEffectFormat<Frameshift> getFrameshiftFormat();

    // delegated parsers


    static String formatAminoAcidCode(AACodeType type, AminoAcidCode... aas) {

        StringBuilder sb = new StringBuilder();

        for (AminoAcidCode aa : aas) {

            if (type == AACodeType.ONE_LETTER) sb.append(String.valueOf(aa.get1LetterCode()));
            else sb.append(String.valueOf(aa.get3LetterCode()));
        }

        return sb.toString();
    }
}
