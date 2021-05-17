package dev.youshallnotpass.inspections.staticfree.statics.java;

import dev.youshallnotpass.inspection.InspectionException;
import dev.youshallnotpass.inspections.staticfree.statics.Static;
import dev.youshallnotpass.inspections.staticfree.statics.JavaStatics;
import org.hamcrest.core.IsEqual;
import org.hamcrest.core.StringContains;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.PrintWriter;
import java.util.List;

public final class JavaSourceFileTest {

    @Rule
    public final TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void oneStaticLiteral() throws InspectionException {
        Assert.assertThat(
            new JavaStatics(
                "class A {\n",
                "    private static final String a = null;\n",
                "}\n"
            ).asList().size(),
            IsEqual.equalTo(1)
        );
    }

    @Test
    public void oneStaticLiteralFromFile() throws Exception {
        final File source = folder.newFile();
        try (final PrintWriter writer = new PrintWriter(source)) {
            writer.println("class A {");
            writer.println("    private static final String a = null;");
            writer.println("}");
        }
        Assert.assertThat(
            new JavaStatics(source).asList().size(),
            IsEqual.equalTo(1)
        );
    }

    @Test
    public void classSuppressedStaticField() throws Exception {
        final List<Static> aStatics = new JavaStatics(
            "@SuppressWarnings(\"staticfree\")\n",
            "class A {\n",
            "    private static final String a = null;\n",
            "}\n"
        ).asList();
        Assert.assertThat(
            aStatics.size(),
            IsEqual.equalTo(1)
        );
        Assert.assertThat(
            aStatics.get(0).isSuppressed(),
            IsEqual.equalTo(true)
        );
    }

    @Test
    public void staticMethodSuppressed() throws Exception {
        final List<Static> aStatics = new JavaStatics(
            "class A {\n",
            "    @SuppressWarnings(\"staticfree\")\n",
            "    static void method() {\n",
            "    }\n",
            "}\n"
        ).asList();
        Assert.assertThat(
            aStatics.size(),
            IsEqual.equalTo(1)
        );
        Assert.assertThat(
            aStatics.get(0).isSuppressed(),
            IsEqual.equalTo(true)
        );
    }

    @Test
    public void suppressedStaticField() throws Exception {
        final List<Static> aStatics = new JavaStatics(
            "class A {\n",
            "    @SuppressWarnings(\"staticfree\")\n",
            "    static String name = null;\n",
            "}\n"
        ).asList();
        Assert.assertThat(
            aStatics.size(),
            IsEqual.equalTo(1)
        );
        Assert.assertThat(
            aStatics.get(0).isSuppressed(),
            IsEqual.equalTo(true)
        );
    }

    @Test
    public void fieldMultipleSuppressedStatic() throws Exception {
        final List<Static> aStatics = new JavaStatics(
            "class A {\n",
            "    @SuppressWarnings(value = {\"staticfree\", \"something_else\"})\n",
            "    static String name = null;\n",
            "}\n"
        ).asList();
        Assert.assertThat(
            aStatics.size(),
            IsEqual.equalTo(1)
        );
        Assert.assertThat(
            aStatics.get(0).isSuppressed(),
            IsEqual.equalTo(true)
        );
    }

    @Test
    public void staticsWhenParsingError() throws Exception {
        try {
            new JavaStatics(
                "class A {\n",
                "    void int a() {\n",
                "    }\n",
                "}\n"
            ).asList();
        } catch (final InspectionException e) {
            // green
        } catch (final Throwable e) {
            Assert.fail();
        }
    }

    @Test
    public void staticsWhenParsingErrorInFile() throws Exception {
        final File source = folder.newFile("Main.java");
        try (final PrintWriter writer = new PrintWriter(source)) {
            writer.println("class A {");
            writer.println("    private public static final String a = null;");
            writer.println("}");
        }
        try {
            new JavaStatics(source).asList();
        } catch (final InspectionException e) {
            Assert.assertThat(e.getMessage(), StringContains.containsString("Main.java"));
        } catch (final Throwable e) {
            Assert.fail();
        }
    }

    @Test
    public void staticsMethodInInterface() throws Exception {
        Assert.assertThat(
            new JavaStatics(
                "interface A {\n",
                "    static void a() {\n",
                "        String a = null;\n",
                "    }\n",
                "}\n"
            ).asList().size(),
            IsEqual.equalTo(1)
        );
    }

}