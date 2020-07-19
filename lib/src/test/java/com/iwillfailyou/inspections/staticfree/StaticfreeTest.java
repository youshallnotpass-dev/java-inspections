package com.iwillfailyou.inspections.staticfree;

import com.iwillfailyou.inspection.sources.java.JavaSourceMask;
import com.iwillfailyou.plugin.Failures;
import org.cactoos.io.TeeInput;
import org.cactoos.scalar.LengthOf;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;

public class StaticfreeTest {

    @Rule
    public final TemporaryFolder tmp = new TemporaryFolder();

    @Test
    public void simpleFileWithStatic() throws Exception {
        final Staticfree inspection = new Staticfree(
            new JavaSourceMask(),
            0
        );
        final File source = tmp.newFile("Temp.java");
        new LengthOf(
            new TeeInput(
                "package com.example;" +
                    "class Temp {" +
                    "    static void method() {" +
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
        } catch (Exception e) {
            // green
        }
    }

    @Test
    public void twoFilesWithoutStatic() throws Exception {
        final Staticfree inspection = new Staticfree(
            new JavaSourceMask(),
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
        } catch (Exception e) {
            Assert.fail("Must not fail!");
        }
    }

    @Test
    public void twoFilesAndOneWithStatic() throws Exception {
        final Staticfree inspection = new Staticfree(
            new JavaSourceMask(),
            0
        );
        final File source1 = tmp.newFile("Temp1.java");
        new LengthOf(
            new TeeInput(
                "package com.example;" +
                    "class Temp1 {" +
                    "    static void method() {" +
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
            Assert.fail("Must fail!");
        } catch (Exception e) {
            // green
        }
    }

}