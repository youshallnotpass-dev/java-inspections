package com.iwillfailyou.inspection.sources.java;

import com.iwillfailyou.inspection.sources.PathSourceMask;
import com.iwillfailyou.inspection.sources.SourceMask;

import java.nio.file.Path;

public final class JavaSourceMask implements SourceMask {

    private final SourceMask origin;

    public JavaSourceMask() {
        this(new PathSourceMask("glob:**/*.java"));
    }

    private JavaSourceMask(final SourceMask origin) {
        this.origin = origin;
    }

    @Override
    public boolean matches(final Path path) {
        return origin.matches(path);
    }
}
