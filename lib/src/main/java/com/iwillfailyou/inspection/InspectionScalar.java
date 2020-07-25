package com.iwillfailyou.inspection;

import org.cactoos.Scalar;
import org.cactoos.scalar.Checked;

public final class InspectionScalar<T> implements Scalar<T> {

    private final Scalar<T> origin;

    public InspectionScalar(final Scalar<T> scalar) {
        this.origin = scalar;
    }

    @Override
    public T value() throws InspectionException {
        return new Checked<>(
            this.origin,
            InspectionException::new
        ).value();
    }
}
