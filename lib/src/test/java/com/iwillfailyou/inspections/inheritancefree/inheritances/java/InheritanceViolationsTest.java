package com.iwillfailyou.inspections.inheritancefree.inheritances.java;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParserConfiguration;
import com.iwillfailyou.inspection.InspectionException;
import com.iwillfailyou.inspection.JavaViolations;
import com.iwillfailyou.inspections.inheritancefree.inheritances.Inheritance;
import com.iwillfailyou.inspections.inheritancefree.inheritances.InheritanceViolations;
import org.hamcrest.core.IsEqual;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.util.List;

public final class InheritanceViolationsTest {

    @Rule
    public final TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void oneClassInheritance() throws Exception {
        final List<Inheritance> inheritances = new JavaViolations<>(
            new JavaParser(
                new ParserConfiguration().setLanguageLevel(
                    ParserConfiguration.LanguageLevel.RAW
                )
            ),
            new InheritanceViolations(),
            "class A extends B {}"
        ).asList();

        Assert.assertThat(
            inheritances.size(),
            IsEqual.equalTo(1)
        );
        Assert.assertThat(
            inheritances.get(0).description(),
            IsEqual.equalTo("A(A.java:1) > A")
        );
    }

    @Test
    public void classSuppressedInheritance() throws Exception {
        final List<Inheritance> inheritances = new JavaViolations<>(
            new JavaParser(
                new ParserConfiguration().setLanguageLevel(
                    ParserConfiguration.LanguageLevel.RAW
                )
            ),
            new InheritanceViolations(),
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
        final List<Inheritance> inheritances = new JavaViolations<>(
            new JavaParser(
                new ParserConfiguration().setLanguageLevel(
                    ParserConfiguration.LanguageLevel.RAW
                )
            ),
            new InheritanceViolations(),
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
            new JavaViolations<>(
                new JavaParser(
                    new ParserConfiguration().setLanguageLevel(
                        ParserConfiguration.LanguageLevel.RAW
                    )
                ),
                new InheritanceViolations(),
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
}