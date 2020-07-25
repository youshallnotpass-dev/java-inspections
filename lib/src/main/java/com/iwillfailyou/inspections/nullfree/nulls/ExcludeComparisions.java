package com.iwillfailyou.inspections.nullfree.nulls;

import com.iwillfailyou.inspection.InspectionException;
import com.iwillfailyou.inspection.Violations;
import org.cactoos.collection.Filtered;
import org.cactoos.list.ListOf;

import java.util.List;

public final class ExcludeComparisions implements Violations<Null> {

    private final Violations<Null> origin;

    public ExcludeComparisions(final Violations<Null> origin) {
        this.origin = origin;
    }

    @Override
    public List<Null> asList() throws InspectionException {
        return new ListOf<>(
            new Filtered<>(
                (final Null valuation) -> !valuation.isInComparision(),
                origin.asList()
            )
        );
    }
}
