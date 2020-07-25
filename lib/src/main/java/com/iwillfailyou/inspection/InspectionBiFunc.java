package com.iwillfailyou.inspection;

import org.cactoos.BiFunc;
import org.cactoos.scalar.Checked;

public final class InspectionBiFunc<X, Y, Z> implements BiFunc<X, Y, Z> {

    private final BiFunc<X, Y, Z> func;

    public InspectionBiFunc(final BiFunc<X, Y, Z> fnc) {
        this.func = fnc;
    }

    @Override
    public Z apply(final X first, final Y second) throws InspectionException {
        return new Checked<>(
            () -> this.func.apply(first, second),
            InspectionException::new
        ).value();
    }
}
