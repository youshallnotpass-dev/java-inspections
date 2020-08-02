package com.iwillfailyou.inspections.allfinal.nonfinals;

import com.iwillfailyou.inspection.InspectionException;
import com.iwillfailyou.inspection.Violations;
import org.cactoos.collection.Filtered;
import org.cactoos.list.ListOf;

import java.util.List;

public final class ExcludeCatchParams implements Violations<Nonfinal> {

    private final Violations<Nonfinal> origin;

    public ExcludeCatchParams(final Violations<Nonfinal> origin) {
        this.origin = origin;
    }

    @Override
    public List<Nonfinal> asList() throws InspectionException {
        return new ListOf<>(
            new Filtered<>(
                (final Nonfinal valuation) -> !valuation.isCatchParam(),
                origin.asList()
            )
        );
    }
}
