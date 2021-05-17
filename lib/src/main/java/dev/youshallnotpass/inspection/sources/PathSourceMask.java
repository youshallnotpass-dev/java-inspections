package dev.youshallnotpass.inspection.sources;

import org.cactoos.iterable.Mapped;
import org.cactoos.list.ListOf;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.List;

public final class PathSourceMask implements SourceMask {

    private final List<PathMatcher> matchers;

    public PathSourceMask(final String... syntaxAndPatternList) {
        this(
            new ListOf<PathMatcher>(
                new Mapped<>(
                    (final String syntaxAndPattern) -> FileSystems.getDefault().getPathMatcher(
                        syntaxAndPattern
                    ),
                    new ListOf<>(syntaxAndPatternList)
                )
            )
        );
    }

    public PathSourceMask(final List<PathMatcher> matchers) {
        this.matchers = matchers;
    }

    @Override
    public boolean matches(final Path path) {
        return matchers.stream().anyMatch((final PathMatcher matcher) -> {
            return matcher.matches(path);
        });
    }
}
