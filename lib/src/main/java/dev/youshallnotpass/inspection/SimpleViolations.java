package dev.youshallnotpass.inspection;

import java.util.List;

public final class SimpleViolations<T extends Violation> implements Violations<T> {

    private final List<T> aViolations;

    public SimpleViolations(final List<T> aViolations) {
        this.aViolations = aViolations;
    }

    @Override
    public List<T> asList() {
        return aViolations;
    }
}
