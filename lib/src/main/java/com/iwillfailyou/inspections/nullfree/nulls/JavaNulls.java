package com.iwillfailyou.inspections.nullfree.nulls;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseProblemException;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.Problem;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.iwillfailyou.inspection.InspectionException;
import com.iwillfailyou.inspection.InspectionScalar;
import com.iwillfailyou.inspection.SimpleViolations;
import com.iwillfailyou.inspection.Violations;
import com.iwillfailyou.javaparser.NodeDescription;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JavaNulls implements Violations<Null> {

    private final InspectionScalar<Violations<Null>> nulls;

    public JavaNulls(final String... lines) {
        this(ParserConfiguration.LanguageLevel.RAW, lines);
    }

    public JavaNulls(final ParserConfiguration.LanguageLevel level, final String... lines) {
        this(
            new JavaParser(new ParserConfiguration().setLanguageLevel(level)),
            lines
        );
    }

    public JavaNulls(final JavaParser parser, final String... lines) {
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

    public JavaNulls(final String source) {
        this(ParserConfiguration.LanguageLevel.RAW, source);
    }

    public JavaNulls(final ParserConfiguration.LanguageLevel level, final String source) {
        this(
            new JavaParser(new ParserConfiguration().setLanguageLevel(level)),
            source
        );
    }

    public JavaNulls(final JavaParser parser, final String source) {
        this(
            parser,
            new InspectionScalar<>(() -> new ByteArrayInputStream(source.getBytes())),
            source
        );
    }

    public JavaNulls(final File file) {
        this(ParserConfiguration.LanguageLevel.RAW, file);
    }

    public JavaNulls(final ParserConfiguration.LanguageLevel level, final File file) {
        this(
            new JavaParser(new ParserConfiguration().setLanguageLevel(level)),
            file
        );
    }

    public JavaNulls(final JavaParser parser, final File file) {
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

    public JavaNulls(
        final JavaParser parser,
        final InspectionScalar<InputStream> source,
        final String descriptor
    ) {
        this(
            new InspectionScalar<>(() -> {
                try (final InputStream stream = source.value()) {
                    final List<Null> result = new ArrayList<>();
                    final ParseResult<CompilationUnit> parsed = parser.parse(stream);
                    if (parsed.isSuccessful()) {
                        final Optional<CompilationUnit> optionalUnit = parsed.getResult();
                        if (optionalUnit.isPresent()) {
                            final CompilationUnit unit = optionalUnit.get();
                            final Optional<TypeDeclaration> type = unit.findFirst(
                                TypeDeclaration.class
                            );
                            if (type.isPresent()) {
                                final List<NullLiteralExpr> nulls = unit.findAll(
                                    NullLiteralExpr.class
                                );
                                for (NullLiteralExpr aNull : nulls) {
                                    result.add(
                                        new JavaNull(
                                            aNull,
                                            new NodeDescription(aNull, type.get())
                                        )
                                    );
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
                                "Can not count nulls in: '%s'. \nPlease, fix java syntax errors: \n%s",
                                descriptor,
                                problems.toString()
                            )
                        );
                    }
                    return new SimpleViolations<Null>(result);
                } catch (final IOException e) {
                    throw new InspectionException(
                        String.format(
                            "Can not count nulls in: '%s'.", descriptor
                        ),
                        e
                    );
                } catch (ParseProblemException e) {
                    throw new InspectionException(
                        String.format(
                            "Can not count nulls in: '%s'. \nPlease, fix java syntax errors and try again.", descriptor
                        ),
                        e
                    );
                }
            })
        );
    }

    public JavaNulls(final InspectionScalar<Violations<Null>> nulls) {
        this.nulls = nulls;
    }

    @Override
    public List<Null> asList() throws InspectionException {
        return nulls.value().asList();
    }
}
