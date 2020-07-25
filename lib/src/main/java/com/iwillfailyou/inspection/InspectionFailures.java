package com.iwillfailyou.inspection;

import com.iwillfailyou.inspection.badge.Badge;
import com.iwillfailyou.inspections.nullfree.nulls.Null;
import com.iwillfailyou.plugin.Failures;
import com.iwillfailyou.plugin.IwfyException;
import com.iwillfailyou.plugin.Ui;

import java.net.URL;

public final class InspectionFailures<T extends Violation> implements Failures {

    private final Violations<T> nulls;
    private final Badge badge;

    public InspectionFailures(
        final Violations<T> nulls,
        final Badge badge
    ) {
        this.nulls = nulls;
        this.badge = badge;
    }

    @Override
    public void failIfRed() throws IwfyException {
        try {
            badge.failIfRed();
        } catch (final InspectionException e) {
            throw new IwfyException("Inspection failed. ", e);
        }
    }

    @Override
    public void show(final Ui ui) throws IwfyException {
        try {
            for (final T violation : nulls.asList()) {
                ui.println(violation.description());
            }
        } catch (final InspectionException e) {
            throw new IwfyException("Could not show the violations.", e);
        }
    }

    @Override
    public void publish(final URL url) throws IwfyException {
        try {
            badge.send(url);
        } catch (final InspectionException e) {
            throw new IwfyException(
                "Could not publish the violations.",
                e
            );
        }
    }
}
