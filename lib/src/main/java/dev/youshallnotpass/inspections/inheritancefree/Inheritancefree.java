package dev.youshallnotpass.inspections.inheritancefree;

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
import dev.youshallnotpass.inspections.inheritancefree.inheritances.Inheritance;
import dev.youshallnotpass.inspections.inheritancefree.inheritances.InheritanceViolations;
import dev.youshallnotpass.plugin.Failures;
import dev.youshallnotpass.plugin.Inspection;
import dev.youshallnotpass.plugin.YsnpException;

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
    public void accept(final File file) throws YsnpException {
        final Path path = file.toPath();
        if (sourceMask.matches(path)) {
            try {
                this.inheritances.addAll(
                    new JavaViolations<>(
                        new JavaParser(
                            new ParserConfiguration().setLanguageLevel(
                                ParserConfiguration.LanguageLevel.RAW
                            )
                        ),
                        new InheritanceViolations(),
                        path.toFile()
                    ).asList()
                );
            } catch (final InspectionException e) {
                throw new YsnpException(
                    "Could not get the inheritance classes.",
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
            new YsnpBadge(excludeSuppressed, threshold)
        );
    }
}
