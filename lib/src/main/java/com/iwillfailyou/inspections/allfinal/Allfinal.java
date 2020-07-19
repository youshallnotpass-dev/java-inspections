package com.iwillfailyou.inspections.allfinal;

import com.iwillfailyou.inspection.ExcludeSuppressed;
import com.iwillfailyou.inspection.InspectionException;
import com.iwillfailyou.inspection.InspectionFailures;
import com.iwillfailyou.inspection.SimpleViolations;
import com.iwillfailyou.inspection.Violations;
import com.iwillfailyou.inspection.badge.IwfyBadge;
import com.iwillfailyou.inspection.sources.SourceMask;
import com.iwillfailyou.inspections.allfinal.nonfinals.JavaNonfinals;
import com.iwillfailyou.inspections.allfinal.nonfinals.Nonfinal;
import com.iwillfailyou.plugin.Failures;
import com.iwillfailyou.plugin.Inspection;
import com.iwillfailyou.plugin.IwfyException;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Allfinal implements Inspection {

    private final SourceMask sourceMask;
    private final int threshold;
    private final List<Nonfinal> nonfinals;

    public Allfinal(
        final SourceMask sourceMask,
        final int threshold
    ) {
        this(
            sourceMask,
            threshold,
            new ArrayList<>()
        );
    }

    public Allfinal(
        final SourceMask sourceMask,
        final int threshold,
        final List<Nonfinal> nonfinals
    ) {
        this.sourceMask = sourceMask;
        this.threshold = threshold;
        this.nonfinals = nonfinals;
    }

    @Override
    public String name() {
        return "allfinal";
    }

    @Override
    public void accept(final File file) throws IwfyException {
        final Path path = file.toPath();
        if (sourceMask.matches(path)) {
            try {
                this.nonfinals.addAll(new JavaNonfinals(path.toFile()).asList());
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
        final Violations<Nonfinal> nonfinals = new ExcludeSuppressed<>(
            new SimpleViolations<>(this.nonfinals)
        );
        return new InspectionFailures<>(
            nonfinals,
            new IwfyBadge(nonfinals, threshold)
        );
    }
}
