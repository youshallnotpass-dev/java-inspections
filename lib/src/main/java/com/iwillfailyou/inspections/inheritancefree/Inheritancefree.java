package com.iwillfailyou.inspections.inheritancefree;

import com.iwillfailyou.inspection.ExcludeSuppressed;
import com.iwillfailyou.inspection.InspectionException;
import com.iwillfailyou.inspection.InspectionFailures;
import com.iwillfailyou.inspection.SimpleViolations;
import com.iwillfailyou.inspection.Violations;
import com.iwillfailyou.inspection.badge.IwfyBadge;
import com.iwillfailyou.inspection.sources.SourceMask;
import com.iwillfailyou.inspections.inheritancefree.inheritances.Inheritance;
import com.iwillfailyou.inspections.inheritancefree.inheritances.JavaInheritances;
import com.iwillfailyou.plugin.Failures;
import com.iwillfailyou.plugin.Inspection;
import com.iwillfailyou.plugin.IwfyException;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class Inheritancefree implements Inspection {

    private final SourceMask sourceMask;
    private final int threshold;
    private final List<Inheritance> inheritances;

    public Inheritancefree(
        final SourceMask sourceMask,
        final int threshold
    ) {
        this(sourceMask, threshold, new ArrayList<>());
    }

    public Inheritancefree(
        final SourceMask sourceMask,
        final int threshold,
        final List<Inheritance> inheritances
    ) {
        this.sourceMask = sourceMask;
        this.threshold = threshold;
        this.inheritances = inheritances;
    }

    @Override
    public String name() {
        return "inheritancefree";
    }

    @Override
    public void accept(final File file) throws IwfyException {
        final Path path = file.toPath();
        if (sourceMask.matches(path)) {
            try {
                this.inheritances.addAll(new JavaInheritances(path.toFile()).asList());
            } catch (final InspectionException e) {
                throw new IwfyException(
                    "Could not get the inheritances.",
                    e
                );
            }
        }
    }

    @Override
    public Failures failures() {
        final Violations<Inheritance> excludeSuppressed = new ExcludeSuppressed<>(new SimpleViolations<>(this.inheritances));
        return new InspectionFailures<>(
            excludeSuppressed,
            new IwfyBadge(excludeSuppressed, threshold)
        );
    }
}
