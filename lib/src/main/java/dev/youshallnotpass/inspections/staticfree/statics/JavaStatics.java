package dev.youshallnotpass.inspections.staticfree.statics;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.TypeDeclaration;
import dev.youshallnotpass.inspection.InspectionException;
import dev.youshallnotpass.inspection.InspectionScalar;
import dev.youshallnotpass.inspection.JavaViolations;
import dev.youshallnotpass.inspection.Violations;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public final class JavaStatics implements Violations<Static> {

    private final Violations<Static> statics;

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
                return new ByteArrayInputStream(
                    source.toString().getBytes(StandardCharsets.UTF_8)
                );
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
            new InspectionScalar<>(() -> {
                return new ByteArrayInputStream(
                    source.getBytes(StandardCharsets.UTF_8)
                );
            }),
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
            new JavaViolations<>(
                source,
                parser,
                descriptor,
                (final CompilationUnit unit, final TypeDeclaration<?> root) -> {
                    final List<Static> result = new ArrayList<>();
                    unit.findAll(Modifier.class)
                        .stream()
                        .filter((final Modifier modifier) -> {
                            return modifier.getKeyword() == Modifier.Keyword.STATIC;
                        })
                        .forEach((final Modifier modifier) -> {
                            result.add(new JavaStatic(modifier, root));
                        });
                    return result;
                }
            )
        );
    }

    public JavaStatics(final Violations<Static> statics) {
        this.statics = statics;
    }

    @Override
    public List<Static> asList() throws InspectionException {
        return statics.asList();
    }
}
