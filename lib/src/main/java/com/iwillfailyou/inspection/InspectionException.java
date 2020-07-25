package com.iwillfailyou.inspection;

public final class InspectionException extends Exception {
    public InspectionException(final String message) {
        super(message);
    }

    public InspectionException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public InspectionException(final Throwable cause) {
        super(cause);
    }
}
