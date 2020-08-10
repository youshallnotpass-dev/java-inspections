package com.iwillfailyou.inspections.inheritancefree.inheritances;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.iwillfailyou.inspection.InspectionException;
import com.iwillfailyou.inspection.InspectionScalar;
import com.iwillfailyou.inspection.JavaViolations;
import com.iwillfailyou.inspection.Violations;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

public final class JavaInheritances implements Violations<Inheritance> {

    private final Violations<Inheritance> inheritances;

    public JavaInheritances(final String... lines) {
        this(ParserConfiguration.LanguageLevel.RAW, lines);
    }

    public JavaInheritances(final ParserConfiguration.LanguageLevel level, final String... lines) {
        this(
            new JavaParser(new ParserConfiguration().setLanguageLevel(level)),
            lines
        );
    }

    public JavaInheritances(final JavaParser parser, final String... lines) {
        this(
            parser,
            new InspectionScalar<>(() -> {
                final StringBuilder source = new StringBuilder();
                for (final String line : lines) {
                    source.append(line);
                }
                return new ByteArrayInputStream(source.toString().getBytes());
            }),
            String.join("", lines)
        );
    }

    public JavaInheritances(final String source) {
        this(ParserConfiguration.LanguageLevel.RAW, source);
    }

    public JavaInheritances(final ParserConfiguration.LanguageLevel level, final String source) {
        this(
            new JavaParser(new ParserConfiguration().setLanguageLevel(level)),
            source
        );
    }

    public JavaInheritances(final JavaParser parser, final String source) {
        this(
            parser,
            new InspectionScalar<>(() -> new ByteArrayInputStream(source.getBytes())),
            source
        );
    }

    public JavaInheritances(final File file) {
        this(ParserConfiguration.LanguageLevel.RAW, file);
    }

    public JavaInheritances(final ParserConfiguration.LanguageLevel level, final File file) {
        this(
            new JavaParser(new ParserConfiguration().setLanguageLevel(level)),
            file
        );
    }

    public JavaInheritances(final JavaParser parser, final File file) {
        this(
            parser,
            new InspectionScalar<>(() -> {
                try {
                    return new FileInputStream(file);
                } catch (final IOException e) {
                    throw new InspectionException(
                        String.format(
                            "Can not get an input stream from the file: %s",
                            file
                        ),
                        e
                    );
                }
            }),
            file.getAbsolutePath()
        );
    }

    public JavaInheritances(
        final JavaParser parser,
        final InspectionScalar<InputStream> source,
        final String descriptor
    ) {
        this(
            new JavaViolations<>(
                source,
                parser,
                descriptor,
                (final CompilationUnit unit, final TypeDeclaration<?> root) ->
                    unit.findAll(ClassOrInterfaceDeclaration.class)
                        .stream()
                        .filter((final ClassOrInterfaceDeclaration type) -> !type.isInterface() && type.getExtendedTypes().isNonEmpty())
                        .map((final ClassOrInterfaceDeclaration type) -> new JavaInheritance(type, root))
                        .collect(Collectors.toList())
            )
        );
    }

    public JavaInheritances(final Violations<Inheritance> inheritances) {
        this.inheritances = inheritances;
    }

    @Override
    public List<Inheritance> asList() throws InspectionException {
        return this.inheritances.asList();
    }
}
