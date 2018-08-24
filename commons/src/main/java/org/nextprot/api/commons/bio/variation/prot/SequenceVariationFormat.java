package org.nextprot.api.commons.bio.variation.prot;

import org.nextprot.api.commons.bio.AminoAcidCode;
import org.nextprot.api.commons.bio.variation.prot.seqchange.SequenceChange;
import org.nextprot.api.commons.bio.variation.prot.seqchange.SequenceChangeFormat;
import org.nextprot.api.commons.bio.variation.prot.varseq.VaryingSequenceFormatter;

import java.text.ParseException;
import java.util.Collection;

/**
 * Provides contract for formatting and parsing <code>SequenceVariation</code>s
 *
 * Created by fnikitin on 07/09/15.
 */
public abstract class SequenceVariationFormat implements SequenceVariationFormatter<String>, SequenceVariationParser {

    @Override
    public String format(SequenceVariation variation, AminoAcidCode.CodeType type) {

        StringBuilder sb = new StringBuilder(prefixFormatter());

        // format changing amino acids part
        getChangingSequenceFormatter()
                .format(variation, type, sb);

        // format change part
        //noinspection unchecked
        getSequenceChangeFormat(variation.getSequenceChange().getType())
                .format(sb, variation.getSequenceChange(), type);

        return sb.toString();
    }

    @Override
    public SequenceVariation parse(String source, SequenceVariationBuilder.Start builder) throws ParseException, SequenceVariationBuildException {

        for (SequenceChange.Type changeType : getAvailableChangeTypes()) {

            SequenceChangeFormat format = getSequenceChangeFormat(changeType);

            if (format.matches(source))
                return format.parse(source, builder);
        }

        throw new ParseException(source + ": not a valid protein sequence variation", 0);
    }

    // prefix the protein sequence format
    protected String prefixFormatter() { return ""; }

    // get the changing sequence formatter
    protected abstract VaryingSequenceFormatter getChangingSequenceFormatter();

    // get the specific object handling formatting and parsing of sequence change
    protected abstract SequenceChangeFormat getSequenceChangeFormat(SequenceChange.Type changeType);
    protected abstract Collection<SequenceChange.Type> getAvailableChangeTypes();
}
