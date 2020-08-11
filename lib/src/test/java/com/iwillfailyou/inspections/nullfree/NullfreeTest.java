package com.iwillfailyou.inspections.nullfree;

import com.iwillfailyou.inspection.sources.java.JavaSourceMask;
import com.iwillfailyou.plugin.Failures;
import org.cactoos.io.TeeInput;
import org.cactoos.scalar.LengthOf;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;

public final class NullfreeTest {

    @Rule
    public final TemporaryFolder tmp = new TemporaryFolder();

    @Test
    public void simpleFileWithNull() throws Exception {
        final Nullfree inspection = new Nullfree(
            new JavaSourceMask(),
            false,
            0
        );
        final File source = tmp.newFile("Temp.java");
        new LengthOf(
            new TeeInput(
                "package com.example;" +
                    "class Temp {" +
                    "    void method() {" +
                    "        String a = null;" +
                    "    }" +
                    "}",
                source
            )
        ).intValue();
        inspection.accept(source);
        final Failures failures = inspection.failures();
        try {
            failures.failIfRed();
            Assert.fail("Must fail!");
        } catch (final Exception e) {
            // green
        }
    }

    @Test
    public void twoFilesWithoutNull() throws Exception {
        final Nullfree inspection = new Nullfree(
            new JavaSourceMask(),
            false,
            0
        );
        final File source1 = tmp.newFile("Temp1.java");
        new LengthOf(
            new TeeInput(
                "package com.example;" +
                    "class Temp1 {" +
                    "    void method() {" +
                    "        String a = \"\";" +
                    "    }" +
                    "}",
                source1
            )
        ).intValue();
        final File source2 = tmp.newFile("Temp.java");
        new LengthOf(
            new TeeInput(
                "package com.example;" +
                    "class Temp2 {" +
                    "    void method() {" +
                    "        String a = \"\";" +
                    "    }" +
                    "}",
                source2
            )
        ).intValue();
        inspection.accept(source1);
        inspection.accept(source2);
        final Failures failures = inspection.failures();
        try {
            failures.failIfRed();
            //green
        } catch (final Exception e) {
            Assert.fail("Must not failed!");
        }
    }

    @Test
    public void twoFilesAndOneWithNull() throws Exception {
        final Nullfree inspection = new Nullfree(
            new JavaSourceMask(),
            false,
            0
        );
        final File source1 = tmp.newFile("Temp1.java");
        new LengthOf(
            new TeeInput(
                "package com.example;" +
                    "class Temp1 {" +
                    "    void method() {" +
                    "        String a = null;" +
                    "    }" +
                    "}",
                source1
            )
        ).intValue();
        final File source2 = tmp.newFile("Temp.java");
        new LengthOf(
            new TeeInput(
                "package com.example;" +
                    "class Temp2 {" +
                    "    void method() {" +
                    "        String a = \"\";" +
                    "    }" +
                    "}",
                source2
            )
        ).intValue();
        inspection.accept(source1);
        inspection.accept(source2);
        final Failures failures = inspection.failures();
        try {
            failures.failIfRed();
            Assert.fail("Must not failed!");
        } catch (final Exception e) {
            // green
        }
    }

}