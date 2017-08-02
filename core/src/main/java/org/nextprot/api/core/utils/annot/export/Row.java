package org.nextprot.api.core.utils.annot.export;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class Row {

    private final List<String> row;

    public Row(List<String> row) {

        this.row = (row != null) ? row : new ArrayList<>();
    }

    public String get(int index) {

        return row.get(index);
    }

    public Stream<String> stream() {
        return row.stream();
    }

    public int size() {
        return row.size();
    }
}
