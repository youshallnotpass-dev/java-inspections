package com.iwillfailyou.inspections.staticfree;

import com.iwillfailyou.inspection.ExcludeSuppressed;
import com.iwillfailyou.inspection.InspectionException;
import com.iwillfailyou.inspection.InspectionFailures;
import com.iwillfailyou.inspection.SimpleViolations;
import com.iwillfailyou.inspection.Violations;
import com.iwillfailyou.inspection.badge.IwfyBadge;
import com.iwillfailyou.inspection.sources.SourceMask;
import com.iwillfailyou.inspections.staticfree.statics.Static;
import com.iwillfailyou.inspections.staticfree.statics.JavaStatics;
import com.iwillfailyou.plugin.Failures;
import com.iwillfailyou.plugin.Inspection;
import com.iwillfailyou.plugin.IwfyException;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Staticfree implements Inspection {

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
    public void accept(File file) throws IwfyException {
        final Path path = file.toPath();
        if (sourceMask.matches(path)) {
            try {
                this.statics.addAll(new JavaStatics(path.toFile()).asList());
            } catch (InspectionException e) {
                throw new IwfyException(
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
            new IwfyBadge(statics, threshold)
        );
    }
}
