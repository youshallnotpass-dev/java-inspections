package com.iwillfailyou.inspection.sources;

import java.nio.file.Path;
import java.nio.file.PathMatcher;

public interface SourceMask {
    boolean matches(Path path);
}
