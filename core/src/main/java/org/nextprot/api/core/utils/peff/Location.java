package org.nextprot.api.core.utils.peff;

/**
 * A location with a start and an end
 *
 * Created by fnikitin on 05/05/15.
 */
public interface Location<T extends Location<T>> extends Comparable<T> {

    Value getStart();
    Value getEnd();

    class Value {

        private static final Value UNKNOWN = new Value(0);
        private final int value;
        private final boolean isDefined;

        private Value(int value) {

            this.value = value;
            isDefined = value != 0;
        }

        public static Value Unknown() {

            return UNKNOWN;
        }

        public static Value of(int value) {

            if (value <= 0) return UNKNOWN;

            return new Value(value);
        }

        public boolean isDefined() {

            return isDefined;
        }

        public int getValue() {

            return value;
        }

        public String toString() {

            return (isDefined) ? String.valueOf(value) : "?";
        }
    }
}
