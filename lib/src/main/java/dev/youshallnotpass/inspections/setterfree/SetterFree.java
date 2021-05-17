package dev.youshallnotpass.inspections.setterfree;

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
import dev.youshallnotpass.inspections.setterfree.setters.Setter;
import dev.youshallnotpass.inspections.setterfree.setters.SetterViolations;
import dev.youshallnotpass.plugin.Failures;
import dev.youshallnotpass.plugin.Inspection;
import dev.youshallnotpass.plugin.IwfyException;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class SetterFree implements Inspection {

    private final SourceMask sourceMask;
    private final int threshold;
    private final List<Setter> setters;

    public SetterFree(
        final SourceMask sourceMask,
        final int threshold
    ) {
        this(
            sourceMask,
            threshold,
            new ArrayList<>()
        );
    }

    public SetterFree(
        final SourceMask sourceMask,
        final int threshold,
        final List<Setter> setters
    ) {
        this.sourceMask = sourceMask;
        this.threshold = threshold;
        this.setters = setters;
    }

    @Override
    public String name() {
        return "setterfree";
    }

    @Override
    public void accept(final File file) throws IwfyException {
        final Path path = file.toPath();
        if (sourceMask.matches(path)) {
            try {
                this.setters.addAll(
                    new JavaViolations<>(
                        new JavaParser(
                            new ParserConfiguration().setLanguageLevel(
                                ParserConfiguration.LanguageLevel.RAW
                            )
                        ),
                        new SetterViolations(),
                        path.toFile()
                    ).asList()
                );
            } catch (final InspectionException e) {
                throw new IwfyException(
                    "Could not get the setters.",
                    e
                );
            }
        }
    }

    @Override
    public Failures failures() {
        final Violations<Setter> setterViolations = new ExcludeSuppressed<>(
            new SimpleViolations<>(this.setters)
        );
        return new InspectionFailures<>(
            setterViolations,
            new IwfyBadge(setterViolations, threshold)
        );
    }
}
