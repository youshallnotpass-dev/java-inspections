package dev.youshallnotpass.plugin;

import java.io.File;

public final class PublicInspection implements Inspection {

    private final Urls urls;
    private final Inspection origin;

    public PublicInspection(
        final Urls urls,
        final Inspection origin
    ) {
        this.urls = urls;
        this.origin = origin;
    }

    @Override
    public String name() {
        return origin.name();
    }

    @Override
    public void accept(final File file) throws YsnpException {
        origin.accept(file);
    }

    @Override
    public Failures failures() {
        return new PublicFailures(urls, origin.failures(), this);
    }
}
