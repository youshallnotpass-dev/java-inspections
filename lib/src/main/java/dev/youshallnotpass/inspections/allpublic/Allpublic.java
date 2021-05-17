package dev.youshallnotpass.inspections.allpublic;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParserConfiguration;
import dev.youshallnotpass.inspection.ExcludeSuppressed;
import dev.youshallnotpass.inspection.InspectionException;
import dev.youshallnotpass.inspection.InspectionFailures;
import dev.youshallnotpass.inspection.JavaViolations;
import dev.youshallnotpass.inspection.SimpleViolations;
import dev.youshallnotpass.inspection.Violations;
import dev.youshallnotpass.inspection.badge.IwfyBadge;
import dev.youshallnotpass.inspection.sources.SourceMask;
import dev.youshallnotpass.inspections.allpublic.nonpublics.Nonpublic;
import dev.youshallnotpass.inspections.allpublic.nonpublics.NonpublicViolations;
import dev.youshallnotpass.plugin.Failures;
import dev.youshallnotpass.plugin.Inspection;
import dev.youshallnotpass.plugin.IwfyException;

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
        return "allpublic";
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
