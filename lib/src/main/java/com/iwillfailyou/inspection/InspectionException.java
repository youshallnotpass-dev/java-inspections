package com.iwillfailyou.inspection;

public class InspectionException extends Exception {
    public InspectionException(String message) {
        super(message);
    }

    public InspectionException(String message, Throwable cause) {
        super(message, cause);
    }

    public InspectionException(final Throwable cause) {
        super(cause);
    }
}
