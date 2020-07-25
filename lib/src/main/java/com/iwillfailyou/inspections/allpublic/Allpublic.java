package com.iwillfailyou.inspections.allpublic;

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
import com.iwillfailyou.inspections.allpublic.nonpublics.Nonpublic;
import com.iwillfailyou.inspections.allpublic.nonpublics.NonpublicViolations;
import com.iwillfailyou.plugin.Failures;
import com.iwillfailyou.plugin.Inspection;
import com.iwillfailyou.plugin.IwfyException;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class Allpublic implements Inspection {

    private final SourceMask sourceMask;
    private final int threshold;
    private final List<Nonpublic> nonpublics;

    public Allpublic(
        final SourceMask sourceMask,
        final int threshold
    ) {
        this(
            sourceMask,
            threshold,
            new ArrayList<>()
        );
    }

    public Allpublic(
        final SourceMask sourceMask,
        final int threshold,
        final List<Nonpublic> nonpublics
    ) {
        this.sourceMask = sourceMask;
        this.threshold = threshold;
        this.nonpublics = nonpublics;
    }

    @Override
    public String name() {
        return "privatefree";
    }

    @Override
    public void accept(final File file) throws IwfyException {
        final Path path = file.toPath();
        if (sourceMask.matches(path)) {
            try {
                this.nonpublics.addAll(
                    new JavaViolations<>(
                        new JavaParser(
                            new ParserConfiguration().setLanguageLevel(
                                ParserConfiguration.LanguageLevel.RAW
                            )
                        ),
                        new NonpublicViolations(),
                        path.toFile()
                    ).asList()
                );
            } catch (final InspectionException e) {
                throw new IwfyException(
                    "Could not get the non public methods.",
                    e
                );
            }
        }
    }

    @Override
    public Failures failures() {
        final Violations<Nonpublic> nonpublics = new ExcludeSuppressed<>(
            new SimpleViolations<>(this.nonpublics)
        );
        return new InspectionFailures<>(
            nonpublics,
            new IwfyBadge(nonpublics, threshold)
        );
    }
}
