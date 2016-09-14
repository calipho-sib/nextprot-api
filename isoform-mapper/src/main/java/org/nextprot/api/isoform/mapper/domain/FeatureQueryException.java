package org.nextprot.api.isoform.mapper.domain;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public abstract class FeatureQueryException extends Exception {

    private final FeatureQuery query;
    private final transient ErrorReason error;

    public FeatureQueryException(FeatureQuery query) {

        this.query = query;
        error = new ErrorReason();
    }

    public FeatureQuery getQuery() {

        return query;
    }

    public ErrorReason getError() {

        return error;
    }

    public static class ErrorReason {

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
            if (!(o instanceof ErrorReason)) return false;
            ErrorReason value = (ErrorReason) o;
            return Objects.equals(causes, value.causes) &&
                    Objects.equals(message, value.message);
        }

        @Override
        public int hashCode() {
            return Objects.hash(causes, message);
        }
    }
}
