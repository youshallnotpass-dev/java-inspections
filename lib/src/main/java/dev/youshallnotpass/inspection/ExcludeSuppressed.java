package dev.youshallnotpass.inspection;

import org.cactoos.iterable.Filtered;
import org.cactoos.list.ListOf;

import java.util.List;

public final class ExcludeSuppressed<T extends Violation> implements Violations<T> {

    private final Violations<T> origin;

    public ExcludeSuppressed(final Violations<T> origin) {
        this.origin = origin;
    }

    @Override
    public List<T> asList() throws InspectionException {
        return new ListOf<>(
            new Filtered<>(
                (final T valuation) -> !valuation.isSuppressed(),
                origin.asList()
            )
        );
    }
}
