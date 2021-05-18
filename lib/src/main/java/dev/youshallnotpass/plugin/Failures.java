package dev.youshallnotpass.plugin;

import java.net.URL;

public interface Failures {
    void failIfRed() throws YsnpException;
    void show(Ui ui) throws YsnpException;
    void publish(URL url) throws YsnpException;

    final class Fake implements Failures {

        @Override
        public void failIfRed() {
            // ignored
        }

        @Override
        public void show(final Ui ui) {
            // ignored
        }

        @Override
        public void publish(final URL url) {
            // ignored
        }
    }
}
