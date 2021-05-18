package dev.youshallnotpass.inspections.nomultiplereturn;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParserConfiguration;
import dev.youshallnotpass.inspection.ExcludeSuppressed;
import dev.youshallnotpass.inspection.InspectionException;
import dev.youshallnotpass.inspection.InspectionFailures;
import dev.youshallnotpass.inspection.JavaViolations;
import dev.youshallnotpass.inspection.SimpleViolations;
import dev.youshallnotpass.inspection.Violations;
import dev.youshallnotpass.inspection.badge.YsnpBadge;
import dev.youshallnotpass.inspection.sources.SourceMask;
import dev.youshallnotpass.inspections.nomultiplereturn.multiplereturns.MultipleReturn;
import dev.youshallnotpass.inspections.nomultiplereturn.multiplereturns.MultipleReturnViolations;
import dev.youshallnotpass.plugin.Failures;
import dev.youshallnotpass.plugin.Inspection;
import dev.youshallnotpass.plugin.YsnpException;

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
    public void accept(final File file) throws YsnpException {
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
                throw new YsnpException(
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
            new YsnpBadge(multipleReturnViolations, threshold)
        );
    }
}
