package dev.youshallnotpass.inspection.badge;

import dev.youshallnotpass.inspection.InspectionException;
import dev.youshallnotpass.inspection.Violation;
import dev.youshallnotpass.inspection.Violations;
import org.apache.hc.client5.http.ClientProtocolException;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.cactoos.text.FormattedText;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public final class YsnpBadge implements Badge {

    private final Violations<? extends Violation> violations;
    private final int threshold;

    public YsnpBadge(final Violations<? extends Violation> violations) {
        this(violations, 0);
    }

    public YsnpBadge(
        final Violations<? extends Violation> violations,
        final int threshold
    ) {
        this.violations = violations;
        this.threshold = threshold;
    }

    @Override
    public void send(final URL url) throws InspectionException {
        try (
            final CloseableHttpClient httpClient = HttpClients.createDefault()
        ) {
            final List<NameValuePair> form = new ArrayList<>();
            for (final Violation violation : violations.asList()) {
                form.add(
                    new BasicNameValuePair(
                        "violation",
                        violation.description()
                    )
                );
            }
            form.add(
                new BasicNameValuePair(
                    "threshold",
                    String.valueOf(threshold)
                )
            );
            final HttpPost saveBadgeInfo = new HttpPost(url.toURI());
            saveBadgeInfo.setEntity(
                new UrlEncodedFormEntity(
                    form
                )
            );
            try (final CloseableHttpResponse response = httpClient.execute(saveBadgeInfo)) {
                if (response.getCode() != HttpStatus.SC_OK) {
                    throw new InspectionException(
                        new FormattedText(
                            "Could not send the badge, response: %f",
                            response.toString()
                        ).toString()
                    );
                }
            }
        } catch (final ClientProtocolException e) {
            throw new InspectionException(
                "Static service error when sending badge info.",
                e
            );
        } catch (final URISyntaxException | IOException e) {
            throw new InspectionException("Can not send the badge request.", e);
        }
    }

    @Override
    public void failIfRed() throws InspectionException {
        final List<? extends Violation> violations = this.violations.asList();
        if (violations.size() > threshold) {
            final StringBuilder message = new StringBuilder();
            for (final Violation violation : violations) {
                message.append("Found:\n");
                message.append(violation.description());
                message.append('\n');
            }
            throw new InspectionException(
                String.format(
                    "Found statics:\n%s",
                    message.toString()
                )
            );
        }
    }
}
