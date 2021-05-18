package dev.youshallnotpass.plugin;

import java.io.File;

public interface Inspection {
    String name();
    void accept(File file) throws YsnpException;
    Failures failures();

    final class Fake implements Inspection {

        @Override
        public String name() {
            return "fake";
        }

        @Override
        public void accept(final File file) {
            // ignored
        }

        @Override
        public Failures failures() {
            return new Failures.Fake();
        }
    }
}
