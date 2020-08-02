package com.iwillfailyou.inspections.nullfree.nulls.java;

import com.iwillfailyou.inspection.InspectionException;
import com.iwillfailyou.inspections.nullfree.nulls.Null;
import com.iwillfailyou.inspections.nullfree.nulls.JavaNulls;
import org.hamcrest.core.IsEqual;
import org.hamcrest.core.StringContains;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.PrintWriter;
import java.util.List;

public final class JavaNullsTest {

    @Rule
    public final TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void oneNullLiteral() throws Exception {
        Assert.assertThat(
            new JavaNulls(
                "class A {\n",
                "    private final String a = null;\n",
                "}\n"
            ).asList().size(),
            IsEqual.equalTo(1)
        );
    }

    @Test
    public void oneNullLiteralFromFile() throws Exception {
        final File source = folder.newFile();
        try (final PrintWriter writer = new PrintWriter(source)) {
            writer.println("class A {");
            writer.println("    private final String a = null;");
            writer.println("}");
        }
        Assert.assertThat(
            new JavaNulls(source).asList().size(),
            IsEqual.equalTo(1)
        );
    }

    @Test
    public void classSuppressedNull() throws Exception {
        final List<Null> nulls = new JavaNulls(
            "@SuppressWarnings(\"nullfree\")\n",
            "class A {\n",
            "    private final String a = null;\n",
            "}\n"
        ).asList();
        Assert.assertThat(
            nulls.size(),
            IsEqual.equalTo(1)
        );
        Assert.assertThat(
            nulls.get(0).isSuppressed(),
            IsEqual.equalTo(true)
        );
    }

    @Test
    public void methodSuppressedNull() throws Exception {
        final List<Null> nulls = new JavaNulls(
            "class A {\n",
            "    @SuppressWarnings(\"nullfree\")\n",
            "    void method() {\n",
            "        String name = null;\n",
            "    }\n",
            "}\n"
        ).asList();
        Assert.assertThat(
            nulls.size(),
            IsEqual.equalTo(1)
        );
        Assert.assertThat(
            nulls.get(0).isSuppressed(),
            IsEqual.equalTo(true)
        );
    }

    @Test
    public void assignSuppressedNull() throws Exception {
        final List<Null> nulls = new JavaNulls(
            "class A {\n",
            "    void method() {\n",
            "        @SuppressWarnings(\"nullfree\")\n",
            "        String name = null;\n",
            "    }\n",
            "}\n"
        ).asList();
        Assert.assertThat(
            nulls.size(),
            IsEqual.equalTo(1)
        );
        Assert.assertThat(
            nulls.get(0).isSuppressed(),
            IsEqual.equalTo(true)
        );
    }

    @Test
    public void fieldSuppressedNull() throws Exception {
        final List<Null> nulls = new JavaNulls(
            "class A {\n",
            "    @SuppressWarnings(\"nullfree\")\n",
            "    String name = null;\n",
            "}\n"
        ).asList();
        Assert.assertThat(
            nulls.size(),
            IsEqual.equalTo(1)
        );
        Assert.assertThat(
            nulls.get(0).isSuppressed(),
            IsEqual.equalTo(true)
        );
    }

    @Test
    public void fieldMultipleSuppressedNull() throws Exception {
        final List<Null> nulls = new JavaNulls(
            "class A {\n",
            "    @SuppressWarnings(value = {\"nullfree\", \"something_else\"})\n",
            "    String name = null;\n",
            "}\n"
        ).asList();
        Assert.assertThat(
            nulls.size(),
            IsEqual.equalTo(1)
        );
        Assert.assertThat(
            nulls.get(0).isSuppressed(),
            IsEqual.equalTo(true)
        );
    }

    @Test
    public void nullInComparison() throws Exception {
        final List<Null> nulls = new JavaNulls(
            "class A {\n",
            "    void a() {\n",
            "        String name = \"Some name\";",
            "        if (name != null) {}",
            "    }\n",
            "}\n"
        ).asList();
        Assert.assertThat(
            nulls.size(),
            IsEqual.equalTo(1)
        );
        Assert.assertThat(
            nulls.get(0).isInComparison(),
            IsEqual.equalTo(true)
        );
    }

    @Test
    public void nullNotInComparison() throws Exception {
        final List<Null> nulls = new JavaNulls(
            "class A {\n",
            "    void a() {\n",
            "        String name = \"Some name\";",
            "        if (name.equals(null)) {}",
            "    }\n",
            "}\n"
        ).asList();
        Assert.assertThat(
            nulls.size(),
            IsEqual.equalTo(1)
        );
        Assert.assertThat(
            nulls.get(0).isInComparison(),
            IsEqual.equalTo(false)
        );
    }

    @Test
    public void nullsWhenParsingError() throws Exception {
        try {
            new JavaNulls(
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
    public void nullsWhenParsingErrorInFile() throws Exception {
        final File source = folder.newFile("Main.java");
        try (final PrintWriter writer = new PrintWriter(source)) {
            writer.println("class A {");
            writer.println("    private public final String a = null;");
            writer.println("}");
        }
        try {
            new JavaNulls(source).asList();
        } catch (final InspectionException e) {
            Assert.assertThat(e.getMessage(), StringContains.containsString("Main.java"));
        } catch (final Throwable e) {
            Assert.fail();
        }
    }

    @Test
    public void nullsInPrivateInterfaceMethod() throws Exception {
        Assert.assertThat(
            new JavaNulls(
                "interface A {\n",
                "    private void a() {\n",
                "        String a = null;\n",
                "    }\n",
                "}\n"
            ).asList().size(),
            IsEqual.equalTo(1)
        );
    }

    @Test
    public void nullsInJava12SyntaxFile() throws Exception {
        Assert.assertThat(
            new JavaNulls(
                "public class Test {\n",
                "    enum Day {\n",
                "        MON, TUE, WED, THUR, FRI, SAT, SUN\n",
                "    };\n",
                "    @SuppressWarnings(\"preview\")\n",
                "    public String getDay_1 (Day today) {\n",
                "        String day = switch(today) {\n",
                "            case MON, TUE, WED, THUR, FRI -> \"Weekday\";\n",
                "            case SAT, SUN -> \"Weekend\";\n",
                "        };\n",
                "        return day;\n",
                "    }\n",
                "}"
            ).asList().size(),
            IsEqual.equalTo(0)
        );
    }

}