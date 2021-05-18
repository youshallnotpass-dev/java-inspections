package dev.youshallnotpass.inspections.staticfree;

import dev.youshallnotpass.inspection.ExcludeSuppressed;
import dev.youshallnotpass.inspection.InspectionException;
import dev.youshallnotpass.inspection.InspectionFailures;
import dev.youshallnotpass.inspection.SimpleViolations;
import dev.youshallnotpass.inspection.Violations;
import dev.youshallnotpass.inspection.badge.YsnpBadge;
import dev.youshallnotpass.inspection.sources.SourceMask;
import dev.youshallnotpass.inspections.staticfree.statics.Static;
import dev.youshallnotpass.inspections.staticfree.statics.JavaStatics;
import dev.youshallnotpass.plugin.Failures;
import dev.youshallnotpass.plugin.Inspection;
import dev.youshallnotpass.plugin.YsnpException;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class Staticfree implements Inspection {

    private final SourceMask sourceMask;
    private final int threshold;
    private final List<Static> statics;

    public Staticfree(
        final SourceMask sourceMask,
        final int threshold
    ) {
        this(
            sourceMask,
            threshold,
            new ArrayList<>()
        );
    }

    public Staticfree(
        final SourceMask sourceMask,
        final int threshold,
        final List<Static> statics
    ) {
        this.sourceMask = sourceMask;
        this.threshold = threshold;
        this.statics = statics;
    }

    @Override
    public String name() {
        return "staticfree";
    }

    @Override
    public void accept(final File file) throws YsnpException {
        final Path path = file.toPath();
        if (sourceMask.matches(path)) {
            try {
                this.statics.addAll(new JavaStatics(path.toFile()).asList());
            } catch (final InspectionException e) {
                throw new YsnpException(
                    "Could not get the statics.",
                    e
                );
            }
        }
    }

    @Override
    public Failures failures() {
        final Violations<Static> statics = new ExcludeSuppressed<>(
            new SimpleViolations<>(this.statics)
        );
        return new InspectionFailures<>(
            statics,
            new YsnpBadge(statics, threshold)
        );
    }
}
