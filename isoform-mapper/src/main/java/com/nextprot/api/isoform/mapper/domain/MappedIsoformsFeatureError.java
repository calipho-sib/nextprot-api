package com.nextprot.api.isoform.mapper.domain;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Feature errors
 */
public abstract class MappedIsoformsFeatureError extends MappedIsoformsFeatureResult {

    private final ErrorValue error;

    public MappedIsoformsFeatureError(FeatureQuery query) {
        super(query);
        error = new ErrorValue();
    }

    @Override
    public boolean isSuccess() {
        return false;
    }

    public ErrorValue getError() {

        return error;
    }

    public static class ErrorValue {

        private final Map<String, Object> causes = new HashMap<>();
        private String message;

        public Map<String, Object> getCauses() {
            return causes;
        }

        public Object getCause(String key) {
            return causes.get(key);
        }

        public void addCause(String key, Object value) {

            causes.put(key, value);
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ErrorValue)) return false;
            ErrorValue value = (ErrorValue) o;
            return Objects.equals(causes, value.causes) &&
                    Objects.equals(message, value.message);
        }

        @Override
        public int hashCode() {
            return Objects.hash(causes, message);
        }
    }

}
