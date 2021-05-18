package dev.youshallnotpass.inspections.allfinal;

import dev.youshallnotpass.inspection.ExcludeSuppressed;
import dev.youshallnotpass.inspection.InspectionException;
import dev.youshallnotpass.inspection.InspectionFailures;
import dev.youshallnotpass.inspection.SimpleViolations;
import dev.youshallnotpass.inspection.Violations;
import dev.youshallnotpass.inspection.badge.YsnpBadge;
import dev.youshallnotpass.inspection.sources.SourceMask;
import dev.youshallnotpass.inspections.allfinal.nonfinals.ExcludeCatchParams;
import dev.youshallnotpass.inspections.allfinal.nonfinals.ExcludeInterfaceMethodParams;
import dev.youshallnotpass.inspections.allfinal.nonfinals.ExcludeLambdaParams;
import dev.youshallnotpass.inspections.allfinal.nonfinals.JavaNonfinals;
import dev.youshallnotpass.inspections.allfinal.nonfinals.Nonfinal;
import dev.youshallnotpass.plugin.Failures;
import dev.youshallnotpass.plugin.Inspection;
import dev.youshallnotpass.plugin.YsnpException;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class Allfinal implements Inspection {

    private final SourceMask sourceMask;
    private final int threshold;
    private final boolean skipInterfaceMethodParams;
    private final boolean skipLambdaParams;
    private final boolean skipCatchParams;
    private final List<Nonfinal> nonfinals;

    public Allfinal(
        final SourceMask sourceMask,
        final int threshold,
        final boolean skipInterfaceMethodParams,
        final boolean skipLambdaParams,
        final boolean skipCatchParams
    ) {
        this(
            sourceMask,
            threshold,
            skipInterfaceMethodParams,
            skipLambdaParams,
            skipCatchParams,
            new ArrayList<>()
        );
    }

    public Allfinal(
        final SourceMask sourceMask,
        final int threshold,
        final boolean skipInterfaceMethodParams,
        final boolean skipLambdaParams,
        final boolean skipCatchParams,
        final List<Nonfinal> nonfinals
    ) {
        this.sourceMask = sourceMask;
        this.threshold = threshold;
        this.skipInterfaceMethodParams = skipInterfaceMethodParams;
        this.skipLambdaParams = skipLambdaParams;
        this.skipCatchParams = skipCatchParams;
        this.nonfinals = nonfinals;
    }

    @Override
    public String name() {
        return "allfinal";
    }

    @Override
    public void accept(final File file) throws YsnpException {
        final Path path = file.toPath();
        if (sourceMask.matches(path)) {
            try {
                this.nonfinals.addAll(new JavaNonfinals(path.toFile()).asList());
            } catch (final InspectionException e) {
                throw new YsnpException(
                    "Could not get the nulls.",
                    e
                );
            }
        }
    }

    @Override
    public Failures failures() {
        final Violations<Nonfinal> excludeSuppressed = new ExcludeSuppressed<>(
            new SimpleViolations<>(this.nonfinals)
        );
        final Violations<Nonfinal> excludeInterfaceMethodParams;
        if (skipInterfaceMethodParams) {
            excludeInterfaceMethodParams = new ExcludeInterfaceMethodParams(
                excludeSuppressed
            );
        } else {
            excludeInterfaceMethodParams = excludeSuppressed;
        }
        final Violations<Nonfinal> excludeLambdaParams;
        if (skipLambdaParams) {
            excludeLambdaParams = new ExcludeLambdaParams(
                excludeInterfaceMethodParams
            );
        } else {
            excludeLambdaParams = excludeInterfaceMethodParams;
        }
        final Violations<Nonfinal> excludeCatchParams;
        if (skipCatchParams) {
            excludeCatchParams = new ExcludeCatchParams(
                excludeLambdaParams
            );
        } else {
            excludeCatchParams = excludeLambdaParams;
        }
        return new InspectionFailures<>(
            excludeCatchParams,
            new YsnpBadge(excludeCatchParams, threshold)
        );
    }
}
