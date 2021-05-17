package dev.youshallnotpass.inspections.inheritancefree;

import dev.youshallnotpass.inspection.sources.java.JavaSourceMask;
import dev.youshallnotpass.plugin.Failures;
import org.cactoos.io.TeeInput;
import org.cactoos.scalar.LengthOf;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;

public final class InheritancefreeTest {

    @Rule
    public final TemporaryFolder tmp = new TemporaryFolder();

    @Test
    public void twoFilesWithoutInheritance() throws Exception {
        final Inheritancefree inspection = new Inheritancefree(
            new JavaSourceMask(),
            0
        );
        final File source1 = tmp.newFile("Temp1.java");
        new LengthOf(
            new TeeInput(
                "package com.example;" +
                    "class Temp1 {}",
                source1
            )
        ).value();
        final File source2 = tmp.newFile("Temp.java");
        new LengthOf(
            new TeeInput(
                "package com.example;" +
                    "class Temp2 {}",
                source2
            )
        ).value();
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
    public void simpleFileWithClassInheritance() throws Exception {
        final Inheritancefree inspection = new Inheritancefree(
            new JavaSourceMask(),
            0
        );
        final File source = tmp.newFile("Temp.java");
        new LengthOf(
            new TeeInput(
                "package com.example;" +
                    "class Temp {}\n\n" +
                    "class Temp2 extends Temp2 {}",
                source
            )
        ).value();
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
    public void twoFilesAndOneWithClassInheritance() throws Exception {
        final Inheritancefree inspection = new Inheritancefree(
            new JavaSourceMask(),
            0
        );
        final File source1 = tmp.newFile("Temp1.java");
        new LengthOf(
            new TeeInput(
                "package com.example;" +
                    "class Temp1 extends Temp2 {}",
                source1
            )
        ).value();
        final File source2 = tmp.newFile("Temp.java");
        new LengthOf(
            new TeeInput(
                "package com.example;" +
                    "class Temp2 {}",
                source2
            )
        ).value();
        inspection.accept(source1);
        inspection.accept(source2);
        final Failures failures = inspection.failures();
        try {
            failures.failIfRed();
            Assert.fail("Must fail!");
        } catch (final Exception e) {
            // green
        }
    }

    @Test
    public void simpleFileWithInterfaceInheritance() throws Exception {
        final Inheritancefree inspection = new Inheritancefree(
            new JavaSourceMask(),
            0
        );
        final File source = tmp.newFile("Temp.java");
        new LengthOf(
            new TeeInput(
                "package com.example;" +
                    "interface Temp {}\n\n" +
                    "interface Temp2 extends Temp2 {}",
                source
            )
        ).value();
        inspection.accept(source);
        final Failures failures = inspection.failures();
        try {
            failures.failIfRed();
            //green
        } catch (final Exception e) {
            Assert.fail("Must not failed!");
        }
    }

    @Test
    public void twoFilesAndOneWithInterfaceInheritance() throws Exception {
        final Inheritancefree inspection = new Inheritancefree(
            new JavaSourceMask(),
            0
        );
        final File source1 = tmp.newFile("Temp1.java");
        new LengthOf(
            new TeeInput(
                "package com.example;" +
                    "interface Temp1 extends Temp2 {}",
                source1
            )
        ).value();
        final File source2 = tmp.newFile("Temp.java");
        new LengthOf(
            new TeeInput(
                "package com.example;" +
                    "interface Temp2 {}",
                source2
            )
        ).value();
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

}