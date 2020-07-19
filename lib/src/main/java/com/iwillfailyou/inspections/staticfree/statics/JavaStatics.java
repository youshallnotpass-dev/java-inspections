package com.iwillfailyou.inspections.staticfree.statics;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseProblemException;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.Problem;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.iwillfailyou.inspection.InspectionException;
import com.iwillfailyou.inspection.InspectionScalar;
import com.iwillfailyou.inspection.SimpleViolations;
import com.iwillfailyou.inspection.Violations;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JavaStatics implements Violations<Static> {

    private final InspectionScalar<Violations<Static>> statics;

    public JavaStatics(final String... lines) {
        this(ParserConfiguration.LanguageLevel.RAW, lines);
    }

    public JavaStatics(final ParserConfiguration.LanguageLevel level, final String... lines) {
        this(
            new JavaParser(new ParserConfiguration().setLanguageLevel(level)),
            lines
        );
    }

    public JavaStatics(final JavaParser parser, final String... lines) {
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

    public JavaStatics(final String source) {
        this(ParserConfiguration.LanguageLevel.RAW, source);
    }

    public JavaStatics(final ParserConfiguration.LanguageLevel level, final String source) {
        this(
            new JavaParser(new ParserConfiguration().setLanguageLevel(level)),
            source
        );
    }

    public JavaStatics(final JavaParser parser, final String source) {
        this(
            parser,
            new InspectionScalar<>(() -> new ByteArrayInputStream(source.getBytes())),
            source
        );
    }

    public JavaStatics(final File file) {
        this(ParserConfiguration.LanguageLevel.RAW, file);
    }

    public JavaStatics(final ParserConfiguration.LanguageLevel level, final File file) {
        this(
            new JavaParser(new ParserConfiguration().setLanguageLevel(level)),
            file
        );
    }

    public JavaStatics(final JavaParser parser, final File file) {
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

    public JavaStatics(
        final JavaParser parser,
        final InspectionScalar<InputStream> source,
        final String descriptor
    ) {
        this(
            new InspectionScalar<>(() -> {
                try (final InputStream stream = source.value()) {
                    final List<Static> result = new ArrayList<>();
                    final ParseResult<CompilationUnit> parsed = parser.parse(stream);
                    if (parsed.isSuccessful()) {
                        final Optional<CompilationUnit> optionalUnit = parsed.getResult();
                        if (optionalUnit.isPresent()) {
                            final CompilationUnit unit = optionalUnit.get();
                            final Optional<TypeDeclaration> root = unit.findFirst(
                                TypeDeclaration.class
                            );

                            if (root.isPresent()) {
                                final List<FieldDeclaration> fields = unit.findAll(FieldDeclaration.class);
                                for (FieldDeclaration field : fields) {
                                    final boolean isStatic = field.getModifiers()
                                        .stream()
                                        .map(Modifier::getKeyword)
                                        .anyMatch(keyword -> keyword == Modifier.Keyword.STATIC);
                                    if (isStatic) {
                                        result.add(new JavaStatic(field, root.get()));
                                    }
                                }

                                final List<MethodDeclaration> methods = unit.findAll(MethodDeclaration.class);
                                for (MethodDeclaration method : methods) {
                                    final boolean isStatic = method.getModifiers()
                                        .stream()
                                        .map(Modifier::getKeyword)
                                        .anyMatch(keyword -> keyword == Modifier.Keyword.STATIC);
                                    if (isStatic) {
                                        result.add(new JavaStatic(method, root.get()));
                                    }
                                }

                                final List<TypeDeclaration> types = unit.findAll(TypeDeclaration.class);
                                for (TypeDeclaration<?> type : types) {
                                    final boolean isStatic = type.getModifiers()
                                        .stream()
                                        .map(Modifier::getKeyword)
                                        .anyMatch(keyword -> keyword == Modifier.Keyword.STATIC);
                                    if (isStatic) {
                                        result.add(new JavaStatic(type, root.get()));
                                    }
                                }
                            }
                        }
                    } else {
                        final StringBuilder problems = new StringBuilder();
                        for (final Problem problem : parsed.getProblems()) {
                            problems.append(problem.toString());
                            problems.append("\n");
                        }
                        throw new InspectionException(
                            String.format(
                                "Can not count statics in: '%s'. \nPlease, fix java " +
                                    "syntax errors: \n%s",
                                descriptor,
                                problems.toString()
                            )
                        );
                    }
                    return new SimpleViolations<>(result);
                } catch (final IOException e) {
                    throw new InspectionException(
                        String.format(
                            "Can not count statics in: '%s'.", descriptor
                        ),
                        e
                    );
                } catch (ParseProblemException e) {
                    throw new InspectionException(
                        String.format(
                            "Can not count statics in: '%s'. \nPlease, fix java " +
                                "syntax errors and try again.", descriptor
                        ),
                        e
                    );
                }
            })
        );
    }

    public JavaStatics(final InspectionScalar<Violations<Static>> statics) {
        this.statics = statics;
    }

    @Override
    public List<Static> asList() throws InspectionException {
        return statics.value().asList();
    }
}
