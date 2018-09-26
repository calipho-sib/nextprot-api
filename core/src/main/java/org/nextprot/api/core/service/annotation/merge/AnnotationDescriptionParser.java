package org.nextprot.api.core.service.annotation.merge;

import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import org.nextprot.api.core.domain.StatementAnnotDescription;

import java.text.ParseException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

public class AnnotationDescriptionParser {

    private static final Logger LOGGER = Logger.getLogger(AnnotationDescriptionParser.class.getName());

    public StatementAnnotDescription parse(String description) throws ParseException {

        TokenListParser tokenListParser = new TokenListParser(description);

        StatementAnnotDescription desc = new StatementAnnotDescription();

        desc.setPtm(tokenListParser.consumeNextToken());
        desc.setAlternate(tokenListParser.consumeNextTokenIfMatch(new TokenListParser.Equals("alternate")).isPresent());
        desc.addAllEnzymes(buildEnzymeList(tokenListParser.consumeNextTokenIfMatch(new TokenListParser.StartsWith("by")).orElse("")));
        desc.setInVitro(tokenListParser.consumeNextTokenIfMatch(new TokenListParser.Equals("in vitro")).isPresent());

        if (!tokenListParser.isEmpty()) {

            LOGGER.warning("all description tokens have not been totally consumed: "+ tokenListParser.getTokens());
        }

        return desc;
    }

    private List<String> buildEnzymeList(String byEnzymes) {

        return Lists.newArrayList(Splitter
                .onPattern("([,]|and|by)")
                .trimResults()
                .omitEmptyStrings()
                .split(byEnzymes));
    }

    private static class TokenListParser {

        private final LinkedList<String> tokens;

        private TokenListParser(String description) {

            Preconditions.checkNotNull(description);

            this.tokens = new LinkedList<>();
            Splitter.onPattern(";")
                    .trimResults()
                    .omitEmptyStrings()
                    .split(description)
                    .forEach(field -> tokens.add(field));
        }

        private boolean isEmpty() {

            return tokens.isEmpty();
        }

        private List<String> getTokens() {

            return Collections.unmodifiableList(tokens);
        }

        private String consumeNextToken() throws ParseException {

            if (tokens.isEmpty()) {
                throw new ParseException("No more tokens: cannot consume more element", -1);
            }

            return tokens.remove();
        }

        private Optional<String> consumeNextTokenIfMatch(TokenListParser.Operator operator) {

            if (!tokens.isEmpty() && operator.matches(tokens.get(0))) {
                return Optional.of(tokens.remove());
            }

            return Optional.empty();
        }

        private interface Operator {

            boolean matches(String token);
        }

        private static class Equals implements TokenListParser.Operator {

            private final String expectedValue;

            Equals(String expectedValue) {
                this.expectedValue = expectedValue;
            }

            @Override
            public boolean matches(String token) {
                return token.equals(expectedValue);
            }
        }

        private static class StartsWith implements TokenListParser.Operator {

            private final String startedValue;

            StartsWith(String startedValue) {
                this.startedValue = startedValue;
            }

            @Override
            public boolean matches(String token) {
                return token.startsWith(startedValue);
            }
        }
    }
}
