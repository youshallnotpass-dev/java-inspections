package dev.youshallnotpass.inspection;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseProblemException;
import com.github.javaparser.ParseResult;
import com.github.javaparser.Problem;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.TypeDeclaration;
import org.cactoos.BiFunc;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class JavaViolations<T extends Violation> implements Violations<T> {

    private final InspectionScalar<InputStream> source;
    private final JavaParser parser;
    private final String descriptor;
    private final InspectionBiFunc<CompilationUnit, TypeDeclaration<?>, List<T>> violations;

    public JavaViolations(
        final JavaParser parser,
        final BiFunc<CompilationUnit, TypeDeclaration<?>, List<T>> violations,
        final String... lines
    ) {
        this(
            new InspectionScalar<>(() -> {
                final StringBuilder source = new StringBuilder();
                for (final String line : lines) {
                    source.append(line);
                }
                return new ByteArrayInputStream(
                    source.toString().getBytes(StandardCharsets.UTF_8)
                );
            }),
            parser,
            String.join("", lines),
            violations
        );
    }

    public JavaViolations(
        final JavaParser parser,
        final BiFunc<CompilationUnit, TypeDeclaration<?>, List<T>> violations,
        final File file
    ) {
        this(
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
            parser,
            file.getAbsolutePath(),
            violations
        );
    }

    public JavaViolations(
        final InspectionScalar<InputStream> source,
        final JavaParser parser,
        final String descriptor,
        final BiFunc<CompilationUnit, TypeDeclaration<?>, List<T>> violations
    ) {
        this(
            source,
            parser,
            descriptor,
            new InspectionBiFunc<>(violations)
        );
    }

    public JavaViolations(
        final InspectionScalar<InputStream> source,
        final JavaParser parser,
        final String descriptor,
        final InspectionBiFunc<CompilationUnit, TypeDeclaration<?>, List<T>> violations
    ) {
        this.source = source;
        this.parser = parser;
        this.descriptor = descriptor;
        this.violations = violations;
    }

    @Override
    public List<T> asList() throws InspectionException {
        try (final InputStream stream = source.value()) {
            final List<T> result = new ArrayList<>();
            final ParseResult<CompilationUnit> parsed = parser.parse(stream);
            if (parsed.isSuccessful()) {
                final Optional<CompilationUnit> optionalUnit = parsed.getResult();
                if (optionalUnit.isPresent()) {
                    final CompilationUnit unit = optionalUnit.get();
                    final Optional<TypeDeclaration> root = unit.findFirst(
                        TypeDeclaration.class
                    );

                    if (root.isPresent()) {
                        result.addAll(violations.apply(unit, root.get()));
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
                        "Can not count violations in: '%s'. " +
                            "\nPlease, fix java syntax errors: \n%s",
                        descriptor,
                        problems.toString()
                    )
                );
            }
            return result;
        } catch (final IOException e) {
            throw new InspectionException(
                String.format(
                    "Can not count violations in: '%s'.",
                    descriptor
                ),
                e
            );
        } catch (final ParseProblemException e) {
            throw new InspectionException(
                String.format(
                    "Can not count violations in: '%s'. " +
                        "\nPlease, fix java syntax errors and try again.",
                    descriptor
                ),
                e
            );
        }
    }
}
