package com.iwillfailyou.inspections.allfinal.nonfinals;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseProblemException;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.Problem;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
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

public class JavaNonfinals implements Violations<Nonfinal> {

    private final InspectionScalar<Violations<Nonfinal>> statics;

    public JavaNonfinals(final String... lines) {
        this(ParserConfiguration.LanguageLevel.RAW, lines);
    }

    public JavaNonfinals(final ParserConfiguration.LanguageLevel level, final String... lines) {
        this(
            new JavaParser(new ParserConfiguration().setLanguageLevel(level)),
            lines
        );
    }

    public JavaNonfinals(final JavaParser parser, final String... lines) {
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

    public JavaNonfinals(final String source) {
        this(ParserConfiguration.LanguageLevel.RAW, source);
    }

    public JavaNonfinals(final ParserConfiguration.LanguageLevel level, final String source) {
        this(
            new JavaParser(new ParserConfiguration().setLanguageLevel(level)),
            source
        );
    }

    public JavaNonfinals(final JavaParser parser, final String source) {
        this(
            parser,
            new InspectionScalar<>(() -> new ByteArrayInputStream(source.getBytes())),
            source
        );
    }

    public JavaNonfinals(final File file) {
        this(ParserConfiguration.LanguageLevel.RAW, file);
    }

    public JavaNonfinals(final ParserConfiguration.LanguageLevel level, final File file) {
        this(
            new JavaParser(new ParserConfiguration().setLanguageLevel(level)),
            file
        );
    }

    public JavaNonfinals(final JavaParser parser, final File file) {
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

    public JavaNonfinals(
        final JavaParser parser,
        final InspectionScalar<InputStream> source,
        final String descriptor
    ) {
        this(
            new InspectionScalar<>(() -> {
                try (final InputStream stream = source.value()) {
                    final List<Nonfinal> result = new ArrayList<>();
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
                                    if (!field.isFinal()) {
                                        result.add(new JavaNonfinal(field, root.get()));
                                    }
                                }

                                final List<VariableDeclarationExpr> vars = unit.findAll(VariableDeclarationExpr.class);
                                for (VariableDeclarationExpr var : vars) {
                                    if (!var.isFinal()) {
                                        result.add(new JavaNonfinal(var, root.get()));
                                    }
                                }

                                final List<Parameter> params = unit.findAll(Parameter.class);
                                for (Parameter param : params) {
                                    if (!param.isFinal()) {
                                        result.add(new JavaNonfinal(param, root.get()));
                                    }
                                }

                                final List<ClassOrInterfaceDeclaration> types = unit.findAll(ClassOrInterfaceDeclaration.class);
                                for (ClassOrInterfaceDeclaration type : types) {
                                    if (!type.isInterface() && !type.isFinal()) {
                                        result.add(new JavaNonfinal(type, root.get()));
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
                                "Can not count non finals in: '%s'. \nPlease," +
                                    " fix java syntax errors: \n%s",
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

    public JavaNonfinals(final InspectionScalar<Violations<Nonfinal>> statics) {
        this.statics = statics;
    }

    @Override
    public List<Nonfinal> asList() throws InspectionException {
        return statics.value().asList();
    }
}
