package dev.youshallnotpass.plugin;

import java.net.URL;

public final class PublicFailures implements Failures {

    private final Urls urls;
    private final Failures origin;
    private final Inspection inspection;

    public PublicFailures(
        final Urls urls,
        final Failures origin,
        final Inspection inspection
    ) {
        this.urls = urls;
        this.origin = origin;
        this.inspection = inspection;
    }


    @Override
    public void failIfRed() throws YsnpException {
        origin.failIfRed();
    }

    @Override
    public void show(final Ui ui) throws YsnpException {
        origin.show(ui);
        publish(urls.forInspection(inspection));
    }

    @Override
    public void publish(final URL url) throws YsnpException {
        origin.publish(url);
    }
}
