package dev.youshallnotpass.inspections.nullfree.nulls;

import dev.youshallnotpass.inspection.Violation;

public interface Null extends Violation {
    boolean isInComparison();
}
