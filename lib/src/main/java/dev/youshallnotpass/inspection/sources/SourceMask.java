package dev.youshallnotpass.inspection.sources;

import java.nio.file.Path;

public interface SourceMask {
    boolean matches(Path path);
}
