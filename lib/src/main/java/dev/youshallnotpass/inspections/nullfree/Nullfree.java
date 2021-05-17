package dev.youshallnotpass.inspections.nullfree;

import dev.youshallnotpass.inspection.ExcludeSuppressed;
import dev.youshallnotpass.inspection.InspectionException;
import dev.youshallnotpass.inspection.InspectionFailures;
import dev.youshallnotpass.inspection.SimpleViolations;
import dev.youshallnotpass.inspection.Violations;
import dev.youshallnotpass.inspection.badge.IwfyBadge;
import dev.youshallnotpass.inspection.sources.SourceMask;
import dev.youshallnotpass.inspections.nullfree.nulls.ExcludeComparisons;
import dev.youshallnotpass.inspections.nullfree.nulls.JavaNulls;
import dev.youshallnotpass.inspections.nullfree.nulls.Null;
import dev.youshallnotpass.plugin.Failures;
import dev.youshallnotpass.plugin.Inspection;
import dev.youshallnotpass.plugin.IwfyException;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class Nullfree implements Inspection {

    private final SourceMask sourceMask;
    private final boolean skipComparisons;
    private final int threshold;
    private final List<Null> nulls;

    public Nullfree(
        final SourceMask sourceMask,
        final boolean skipComparisons,
        final int threshold
    ) {
        this(
            sourceMask,
            skipComparisons,
            threshold,
            new ArrayList<>()
        );
    }

    public Nullfree(
        final SourceMask sourceMask,
        final boolean skipComparisons,
        final int threshold,
        final List<Null> nulls
    ) {
        this.sourceMask = sourceMask;
        this.skipComparisons = skipComparisons;
        this.threshold = threshold;
        this.nulls = nulls;
    }

    @Override
    public String name() {
        return "nullfree";
    }

    @Override
    public void accept(final File file) throws IwfyException {
        final Path path = file.toPath();
        if (sourceMask.matches(path)) {
            try {
                this.nulls.addAll(new JavaNulls(path.toFile()).asList());
            } catch (final InspectionException e) {
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
        if (skipComparisons) {
            wrapped = new ExcludeComparisons(
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
