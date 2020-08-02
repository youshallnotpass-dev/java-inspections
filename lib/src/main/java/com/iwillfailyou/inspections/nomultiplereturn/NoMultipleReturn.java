package com.iwillfailyou.inspections.nomultiplereturn;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParserConfiguration;
import com.iwillfailyou.inspection.ExcludeSuppressed;
import com.iwillfailyou.inspection.InspectionException;
import com.iwillfailyou.inspection.InspectionFailures;
import com.iwillfailyou.inspection.JavaViolations;
import com.iwillfailyou.inspection.SimpleViolations;
import com.iwillfailyou.inspection.Violations;
import com.iwillfailyou.inspection.badge.IwfyBadge;
import com.iwillfailyou.inspection.sources.SourceMask;
import com.iwillfailyou.inspections.nomultiplereturn.multiplereturns.MultipleReturn;
import com.iwillfailyou.inspections.nomultiplereturn.multiplereturns.MultipleReturnViolations;
import com.iwillfailyou.plugin.Failures;
import com.iwillfailyou.plugin.Inspection;
import com.iwillfailyou.plugin.IwfyException;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class NoMultipleReturn implements Inspection {

    private final SourceMask sourceMask;
    private final int threshold;
    private final List<MultipleReturn> multipleReturns;

    public NoMultipleReturn(
        final SourceMask sourceMask,
        final int threshold
    ) {
        this(
            sourceMask,
            threshold,
            new ArrayList<>()
        );
    }

    public NoMultipleReturn(
        final SourceMask sourceMask,
        final int threshold,
        final List<MultipleReturn> multipleReturns
    ) {
        this.sourceMask = sourceMask;
        this.threshold = threshold;
        this.multipleReturns = multipleReturns;
    }

    @Override
    public String name() {
        return "nomultiplereturn";
    }

    @Override
    public void accept(final File file) throws IwfyException {
        final Path path = file.toPath();
        if (sourceMask.matches(path)) {
            try {
                this.multipleReturns.addAll(
                    new JavaViolations<>(
                        new JavaParser(
                            new ParserConfiguration().setLanguageLevel(
                                ParserConfiguration.LanguageLevel.RAW
                            )
                        ),
                        new MultipleReturnViolations(),
                        path.toFile()
                    ).asList()
                );
            } catch (final InspectionException e) {
                throw new IwfyException(
                    "Could not get the multiple return methods.",
                    e
                );
            }
        }
    }

    @Override
    public Failures failures() {
        final Violations<MultipleReturn> multipleReturnViolations = new ExcludeSuppressed<>(
            new SimpleViolations<>(this.multipleReturns)
        );
        return new InspectionFailures<>(
            multipleReturnViolations,
            new IwfyBadge(multipleReturnViolations, threshold)
        );
    }
}
