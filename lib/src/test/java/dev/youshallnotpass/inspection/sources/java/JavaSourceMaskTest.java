package dev.youshallnotpass.inspection.sources.java;

import dev.youshallnotpass.inspection.sources.SourceMask;
import org.hamcrest.core.IsEqual;
import org.junit.Assert;
import org.junit.Test;

import java.nio.file.Paths;

public final class JavaSourceMaskTest {

    @Test
    public void matchSimpleJavaFile() {
        final SourceMask mask = new JavaSourceMask();
        Assert.assertThat(
            mask.matches(
              Paths.get(
                  ".",
                  "lib",
                  "src",
                  "main",
                  "java",
                  "com",
                  "example",
                  "Main.java"
              )
            ),
            IsEqual.equalTo(true)
        );
    }

    @Test
    public void matchTestJavaFile() {
        final SourceMask mask = new JavaSourceMask();
        Assert.assertThat(
            mask.matches(
                Paths.get(
                    ".",
                    "lib",
                    "src",
                    "test",
                    "java",
                    "com",
                    "example",
                    "MainTest.java"
                )
            ),
            IsEqual.equalTo(true)
        );
    }

}