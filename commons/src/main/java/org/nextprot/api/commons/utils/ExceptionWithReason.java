package org.nextprot.api.commons.utils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ExceptionWithReason extends Exception implements Serializable {

    private static final long serialVersionUID = 20161209L;

    private final transient Reason reason;

    public ExceptionWithReason() {

        reason = new Reason();
    }

    public static ExceptionWithReason withReason(String cause, String causeMessage) {

        ExceptionWithReason e = new ExceptionWithReason();
        e.getReason().addCause(cause, causeMessage);

        return e;
    }

    public static ExceptionWithReason withMessage(String mainCauseMessage) {

        ExceptionWithReason e = new ExceptionWithReason();
        e.getReason().setMessage(mainCauseMessage);

        return e;
    }

    public Reason getReason() {

        return reason;
    }

    public static class Reason {

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
            if (!(o instanceof Reason)) return false;
            Reason value = (Reason) o;
            return Objects.equals(causes, value.causes) &&
                    Objects.equals(message, value.message);
        }

        @Override
        public int hashCode() {
            return Objects.hash(causes, message);
        }
    }
}
