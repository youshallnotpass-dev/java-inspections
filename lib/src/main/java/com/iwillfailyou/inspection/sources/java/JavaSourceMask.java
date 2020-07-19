package com.iwillfailyou.inspection.sources.java;

import com.iwillfailyou.inspection.sources.SourceMask;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.PathMatcher;

public class JavaSourceMask implements SourceMask {

    private final PathMatcher matcher;

    public JavaSourceMask() {
        this(FileSystems.getDefault().getPathMatcher("glob:**/*.java"));
    }

    private JavaSourceMask(PathMatcher matcher) {
        this.matcher = matcher;
    }

    @Override
    public boolean matches(final Path path) {
        return matcher.matches(path);
    }
}
