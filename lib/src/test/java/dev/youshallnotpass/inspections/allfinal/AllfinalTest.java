package dev.youshallnotpass.inspections.allfinal;

import dev.youshallnotpass.inspection.sources.java.JavaSourceMask;
import dev.youshallnotpass.plugin.YsnpException;
import org.cactoos.io.TeeInput;
import org.cactoos.scalar.LengthOf;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;

public final class AllfinalTest {

    @Rule
    public final TemporaryFolder tmp = new TemporaryFolder();

    @Test
    public void skipInterfaceMethodParamsSkipLambdaParams() throws Exception {
        final File source = tmp.newFile("Temp.java");
        new LengthOf(
            new TeeInput(
                "package com.example;\n" +
                    "interface Temp {\n" +
                    "    void method(int a);\n" +
                    "    final class Fake implements Temp {\n" +
                    "        void method(final int a) {\n" +
                    "            final Func f = x -> x + 1;\n" +
                    "        }\n" +
                    "    }\n" +
                    "}",
                source
            )
        ).value();
        final Allfinal allfinal = new Allfinal(
            new JavaSourceMask(),
            0,
            true,
            true,
            false
        );
        allfinal.accept(source);

        try {
            allfinal.failures().failIfRed();
            // green
        } catch (final YsnpException e) {
            Assert.fail();
        }
    }

    @Test
    public void interfaceParamsAndLambdaParams() throws Exception {
        final File source = tmp.newFile("Temp.java");
        new LengthOf(
            new TeeInput(
                "package com.example;\n" +
                    "interface Temp {\n" +
                    "    void method(int a);\n" +
                    "    final class Fake implements Temp {\n" +
                    "        void method(final int a) {\n" +
                    "            final Func f = x -> x + 1;\n" +
                    "        }\n" +
                    "    }\n" +
                    "}",
                source
            )
        ).value();
        final Allfinal allfinal = new Allfinal(
            new JavaSourceMask(),
            0,
            false,
            false,
            false
        );
        allfinal.accept(source);

        try {
            allfinal.failures().failIfRed();
            Assert.fail();
        } catch (final YsnpException e) {
            // green
        }
    }

    @Test
    public void catchParams() throws Exception {
        final File source = tmp.newFile("Temp.java");
        new LengthOf(
            new TeeInput(
                "package com.example;\n" +
                    "interface Temp {\n" +
                    "    void method(int a);\n" +
                    "    final class Fake implements Temp {\n" +
                    "        void method(final int a) {\n" +
                    "            try {\n " +
                    "                final Func f = (final int x) -> x + 1;\n" +
                    "            } catch (RuntimeException e) {\n" +
                    "                e.printStackTrace();\n" +
                    "            }\n" +
                    "        }\n" +
                    "    }\n" +
                    "}",
                source
            )
        ).value();
        final Allfinal allfinal = new Allfinal(
            new JavaSourceMask(),
            0,
            false,
            false,
            true
        );
        allfinal.accept(source);

        try {
            allfinal.failures().failIfRed();
            Assert.fail();
        } catch (final YsnpException e) {
            // green
        }
    }

}