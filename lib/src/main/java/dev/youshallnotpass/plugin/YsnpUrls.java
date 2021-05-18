package dev.youshallnotpass.plugin;

import com.nikialeksey.goo.Origin;
import org.cactoos.text.FormattedText;
import org.cactoos.text.UncheckedText;

import java.net.MalformedURLException;
import java.net.URL;

public final class YsnpUrls implements Urls {

    private final Origin origin;
    private final String baseUrl;

    public YsnpUrls(
        final Origin origin,
        final String baseUrl
    ) {
        this.origin = origin;
        this.baseUrl = baseUrl;
    }

    @Override
    public URL forInspection(final Inspection inspection) throws YsnpException {
        try {
            return new URL(
                new UncheckedText(
                    new FormattedText(
                        "%s/%s/%s/%s",
                        baseUrl,
                        inspection.name(),
                        origin.user(),
                        origin.repo()
                    )
                ).asString()
            );
        } catch (final MalformedURLException e) {
            throw new YsnpException(
                "Could not make the url for publishing failures.",
                e
            );
        }
    }
}
