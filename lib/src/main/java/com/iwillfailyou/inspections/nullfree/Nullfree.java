package com.iwillfailyou.inspections.nullfree;

import com.iwillfailyou.inspection.ExcludeSuppressed;
import com.iwillfailyou.inspection.InspectionException;
import com.iwillfailyou.inspection.InspectionFailures;
import com.iwillfailyou.inspection.SimpleViolations;
import com.iwillfailyou.inspection.Violations;
import com.iwillfailyou.inspection.badge.IwfyBadge;
import com.iwillfailyou.inspection.sources.SourceMask;
import com.iwillfailyou.inspections.nullfree.nulls.Null;
import com.iwillfailyou.inspections.nullfree.nulls.ExcludeComparisions;
import com.iwillfailyou.inspections.nullfree.nulls.JavaNulls;
import com.iwillfailyou.plugin.Failures;
import com.iwillfailyou.plugin.Inspection;
import com.iwillfailyou.plugin.IwfyException;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Nullfree implements Inspection {

    private final SourceMask sourceMask;
    private final boolean skipComparisions;
    private final int threshold;
    private final List<Null> nulls;

    public Nullfree(
        final SourceMask sourceMask,
        final boolean skipComparisions,
        final int threshold
    ) {
        this(
            sourceMask,
            skipComparisions,
            threshold,
            new ArrayList<>()
        );
    }

    public Nullfree(
        final SourceMask sourceMask,
        final boolean skipComparisions,
        final int threshold,
        final List<Null> nulls
    ) {
        this.sourceMask = sourceMask;
        this.skipComparisions = skipComparisions;
        this.threshold = threshold;
        this.nulls = nulls;
    }

    @Override
    public String name() {
        return "nullfree";
    }

    @Override
    public void accept(File file) throws IwfyException {
        final Path path = file.toPath();
        if (sourceMask.matches(path)) {
            try {
                this.nulls.addAll(new JavaNulls(path.toFile()).asList());
            } catch (InspectionException e) {
                throw new IwfyException(
                    "Could not get the nulls.",
                    e
                );
            }
        }
    }

    @Override
    public Failures failures() {
        final Violations<Null> wrapped;
        if (skipComparisions) {
            wrapped = new ExcludeComparisions(
                new ExcludeSuppressed<>(new SimpleViolations<>(nulls))
            );
        } else {
            wrapped = new ExcludeSuppressed<>(new SimpleViolations<>(nulls));
        }
        return new InspectionFailures<>(
            wrapped,
            new IwfyBadge(wrapped, threshold)
        );
    }
}
