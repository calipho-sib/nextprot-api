package org.nextprot.api.core.domain;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.nextprot.commons.utils.EnumConstantDictionary;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@JsonSerialize(using = ProteinExistence.ProteinExistenceSerializer.class)
public enum ProteinExistence {

    PROTEIN_LEVEL() {
        @Override
        public String getDescription() {
            return "Evidence at protein level";
        }
    },
    TRANSCRIPT_LEVEL() {
        @Override
        public String getDescription() {
            return "Evidence at transcript level";
        }
    },
    HOMOLOGY() {
        @Override
        public String getDescription() {
            return "Inferred from homology";
        }
    },
    PREDICTED() {
        @Override
        public String getDescription() {
            return "Predicted";
        }
    },
    UNCERTAIN() {
        @Override
        public String getDescription() {
            return "Uncertain";
        }
    };

    public abstract String getDescription();

    private static EnumConstantDictionary<ProteinExistence> dictionaryOfConstants = new EnumConstantDictionary<ProteinExistence>(ProteinExistence.class, values()) {

        @Override
        protected void updateDictionaryOfConstants(Map<String, ProteinExistence> dictionary) {

            for (ProteinExistence pe : ProteinExistence.values()) {
                computeAlternativeKeys(pe).forEach(k -> dictionary.put(k, pe));
            }
        }

        private List<String> computeAlternativeKeys(ProteinExistence pe) {

            List<String> keys = new ArrayList<>(3);

            keys.add(pe.getDescription());
            keys.add(pe.getDescription().replace(" ", "_"));

            if (pe.ordinal() > 1) {
                keys.add(pe.getName().toLowerCase());
            }
            else {
                String[] words = pe.getDescription().split(" ");
                keys.add(words[words.length-2]+ " " +words[words.length-1]);
            }

            return keys;
        }
    };

    private final String name;
    private final String descriptionName;
    private final int level;

    ProteinExistence() {
        this.name = this.name().toLowerCase().replace("_", " ");
        this.descriptionName = this.getDescription().replace(" ", "_");
        this.level = this.ordinal()+1;
    }

    public String getName() {
        return name;
    }

    public String getDescriptionName() {
        return descriptionName;
    }

    public int getLevel() {
        return level;
    }

    public static ProteinExistence valueOfLevel(int level) {

        if (level < 1 || level > 5) {
            throw new IllegalArgumentException("Invalid ProteinExistenceLevel: "+level);
        }
        return ProteinExistence.values()[level-1];
    }

    public static ProteinExistence valueOfKey(String value) {

        return dictionaryOfConstants.valueOfKey(value);
    }

    public static class ProteinExistenceSerializer extends JsonSerializer<ProteinExistence> {

        @Override
        public void serialize(ProteinExistence pe, JsonGenerator generator, SerializerProvider serializerProvider) throws IOException {

            generator.writeStartObject();
            generator.writeFieldName("name");
            generator.writeString(pe.getName());
            generator.writeFieldName("description");
            generator.writeString(pe.getDescription());
            generator.writeFieldName("descriptionName");
            generator.writeString(pe.getDescriptionName());
            generator.writeFieldName("level");
            generator.writeNumber(pe.getLevel());
            generator.writeEndObject();
        }
    }

    public enum Source {

        PROTEIN_EXISTENCE_UNIPROT, PROTEIN_EXISTENCE_NEXTPROT1, PROTEIN_EXISTENCE_NEXTPROT2
    }
}
