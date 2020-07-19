package com.iwillfailyou.inspection.badge;

import com.iwillfailyou.inspection.InspectionException;
import com.iwillfailyou.inspection.Violation;
import com.iwillfailyou.inspection.Violations;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class IwfyBadge implements Badge {

    private final Violations<? extends Violation> violations;
    private final int threshold;

    public IwfyBadge(final Violations<? extends Violation> violations) {
        this(violations, 0);
    }

    public IwfyBadge(
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
            httpClient.execute(saveBadgeInfo).close();
        } catch (ClientProtocolException e) {
            throw new InspectionException(
                "Static service error when sending badge info.",
                e
            );
        } catch (URISyntaxException | IOException e) {
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
