package com.iwillfailyou.inspections.allfinal.nonfinals;

import com.iwillfailyou.inspection.InspectionException;
import com.iwillfailyou.inspection.Violations;
import com.iwillfailyou.inspections.nullfree.nulls.Null;
import org.cactoos.collection.Filtered;
import org.cactoos.list.ListOf;

import java.util.List;

public final class ExcludeInterfaceMethodParams implements Violations<Nonfinal> {

    private final Violations<Nonfinal> origin;

    public ExcludeInterfaceMethodParams(final Violations<Nonfinal> origin) {
        this.origin = origin;
    }

    @Override
    public List<Nonfinal> asList() throws InspectionException {
        return new ListOf<>(
            new Filtered<>(
                (final Nonfinal valuation) -> !valuation.isInterfaceMethodParam(),
                origin.asList()
            )
        );
    }
}
