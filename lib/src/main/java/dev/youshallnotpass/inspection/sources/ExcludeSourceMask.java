package dev.youshallnotpass.inspection.sources;

import java.nio.file.Path;

public final class ExcludeSourceMask implements SourceMask {
    private final SourceMask exclude;
    private final SourceMask origin;

    public ExcludeSourceMask(
        final SourceMask exclude,
        final SourceMask origin
    ) {
        this.exclude = exclude;
        this.origin = origin;
    }

    @Override
    public boolean matches(final Path path) {
        return origin.matches(path) && !exclude.matches(path);
    }
}
