package com.iwillfailyou.inspections.inheritancefree.inheritances.java;

import com.iwillfailyou.inspection.InspectionException;
import com.iwillfailyou.inspections.inheritancefree.inheritances.Inheritance;
import com.iwillfailyou.inspections.inheritancefree.inheritances.JavaInheritances;
import java.io.File;
import java.io.PrintWriter;
import java.util.List;
import org.hamcrest.core.IsEqual;
import org.hamcrest.core.StringContains;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public final class JavaInheritancesTest {

    @Rule
    public final TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void oneClassInheritance() throws Exception {
        Assert.assertThat(
            new JavaInheritances(
                "class A extends B {}"
            ).asList().size(),
            IsEqual.equalTo(1)
        );
    }

    @Test
    public void oneClassInheritanceFromFile() throws Exception {
        final File source = folder.newFile();
        try (final PrintWriter writer = new PrintWriter(source)) {
            writer.println("class A extends B {}");
        }
        Assert.assertThat(
            new JavaInheritances(source).asList().size(),
            IsEqual.equalTo(1)
        );
    }

    @Test
    public void classSuppressedInheritance() throws Exception {
        final List<Inheritance> inheritances = new JavaInheritances(
            "@SuppressWarnings(\"inheritancefree\")\n",
            "class A extends B{}"
        ).asList();
        Assert.assertThat(
            inheritances.size(),
            IsEqual.equalTo(1)
        );
        Assert.assertThat(
            inheritances.get(0).isSuppressed(),
            IsEqual.equalTo(true)
        );
    }

    @Test
    public void multipleSuppressedInheritance() throws Exception {
        final List<Inheritance> inheritances = new JavaInheritances(
            "@SuppressWarnings(value = {\"inheritancefree\", \"something_else\"})\n",
            "class A extends B {}"
        ).asList();
        Assert.assertThat(
            inheritances.size(),
            IsEqual.equalTo(1)
        );
        Assert.assertThat(
            inheritances.get(0).isSuppressed(),
            IsEqual.equalTo(true)
        );
    }

    @Test
    public void inheritancesWhenParsingError() {
        try {
            new JavaInheritances(
                "class A extends B {\n",
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
    public void inheritancesWhenParsingErrorInFile() throws Exception {
        final File source = folder.newFile("Main.java");
        try (final PrintWriter writer = new PrintWriter(source)) {
            writer.println("class A extends B{");
            writer.println("    private public final String a = null;");
            writer.println("}");
        }
        try {
            new JavaInheritances(source).asList();
        } catch (final InspectionException e) {
            Assert.assertThat(e.getMessage(), StringContains.containsString("Main.java"));
        } catch (final Throwable e) {
            Assert.fail();
        }
    }
}