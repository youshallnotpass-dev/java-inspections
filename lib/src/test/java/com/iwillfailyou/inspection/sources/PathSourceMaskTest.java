package com.iwillfailyou.inspection.sources;

import org.hamcrest.core.IsEqual;
import org.junit.Assert;
import org.junit.Test;

import java.nio.file.Paths;

public final class PathSourceMaskTest {
    @Test
    public void matchFileByGlobPattern() {
        final SourceMask maskForTests = new PathSourceMask(
            "glob:**/test/**/*Test.java"
        );
        Assert.assertThat(
            maskForTests.matches(
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
        Assert.assertThat(
            maskForTests.matches(
                Paths.get(
                    ".",
                    "lib",
                    "src",
                    "test",
                    "java",
                    "com",
                    "example",
                    "Main.java"
                )
            ),
            IsEqual.equalTo(false)
        );
        Assert.assertThat(
            maskForTests.matches(
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
            IsEqual.equalTo(false)
        );
    }

    @Test
    public void matchFileByGlobPatterns() {
        final SourceMask maskForTests = new PathSourceMask(
            "glob:**/test/**/*Test.java",
            "glob:**/build/**/*.java"
        );
        Assert.assertThat(
            maskForTests.matches(
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
        Assert.assertThat(
            maskForTests.matches(
                Paths.get(
                    ".",
                    "lib",
                    "build",
                    "generated",
                    "com",
                    "example",
                    "Generated.java"
                )
            ),
            IsEqual.equalTo(true)
        );
    }

}