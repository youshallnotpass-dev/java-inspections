package com.iwillfailyou.inspections.allfinal.nonfinals;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.iwillfailyou.inspection.InspectionException;
import com.iwillfailyou.inspection.InspectionScalar;
import com.iwillfailyou.inspection.JavaViolations;
import com.iwillfailyou.inspection.Violations;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public final class JavaNonfinals implements Violations<Nonfinal> {

    private final Violations<Nonfinal> statics;

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
            new JavaViolations<>(
                source,
                parser,
                descriptor,
                (final CompilationUnit unit, final TypeDeclaration<?> root) -> {
                    final List<Nonfinal> result = new ArrayList<>();
                    final List<FieldDeclaration> fields = unit.findAll(FieldDeclaration.class);
                    for (final FieldDeclaration field : fields) {
                        if (!field.isFinal()) {
                            result.add(new JavaNonfinal(field, root));
                        }
                    }

                    final List<VariableDeclarationExpr> vars = unit.findAll(VariableDeclarationExpr.class);
                    for (final VariableDeclarationExpr var : vars) {
                        if (!var.isFinal()) {
                            result.add(new JavaNonfinal(var, root));
                        }
                    }

                    final List<Parameter> params = unit.findAll(Parameter.class);
                    for (final Parameter param : params) {
                        if (!param.isFinal()) {
                            result.add(new JavaNonfinal(param, root));
                        }
                    }

                    final List<ClassOrInterfaceDeclaration> types = unit.findAll(ClassOrInterfaceDeclaration.class);
                    for (final ClassOrInterfaceDeclaration type : types) {
                        if (!type.isInterface() && !type.isFinal() && !type.isAbstract()) {
                            result.add(new JavaNonfinal(type, root));
                        }
                    }
                    return result;
                }
            )
        );
    }

    public JavaNonfinals(final Violations<Nonfinal> statics) {
        this.statics = statics;
    }

    @Override
    public List<Nonfinal> asList() throws InspectionException {
        return statics.asList();
    }
}
