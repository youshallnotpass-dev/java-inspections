package com.iwillfailyou.inspections.allpublic.nonpublics;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParserConfiguration;
import com.iwillfailyou.inspection.JavaViolations;
import org.hamcrest.core.IsEqual;
import org.hamcrest.core.StringContains;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public final class NonpublicViolationsTest {

    @Test
    public void privateMethod() throws Exception {
        final List<Nonpublic> nonpublics = new JavaViolations<>(
            new JavaParser(
                new ParserConfiguration().setLanguageLevel(
                    ParserConfiguration.LanguageLevel.RAW
                )
            ),
            new NonpublicViolations(),
            "class A {\n",
            "    private void a() {} \n",
            "}"
        ).asList();

        Assert.assertThat(
            nonpublics.size(),
            IsEqual.equalTo(1)
        );
        Assert.assertThat(
            nonpublics.get(0).description(),
            StringContains.containsString("A.a(A.java:2)")
        );
    }

    @Test
    public void suppressedPrivateMethod() throws Exception {
        final List<Nonpublic> nonpublics = new JavaViolations<>(
            new JavaParser(
                new ParserConfiguration().setLanguageLevel(
                    ParserConfiguration.LanguageLevel.RAW
                )
            ),
            new NonpublicViolations(),
            "class A {\n",
            "    @SuppressWarnings(\"privatefree\")\n",
            "    private void a() {} \n",
            "}"
        ).asList();

        Assert.assertThat(
            nonpublics.size(),
            IsEqual.equalTo(1)
        );
        Assert.assertThat(
            nonpublics.get(0).isSuppressed(),
            IsEqual.equalTo(true)
        );
    }

    @Test
    public void protectedMethod() throws Exception {
        final List<Nonpublic> nonpublics = new JavaViolations<>(
            new JavaParser(
                new ParserConfiguration().setLanguageLevel(
                    ParserConfiguration.LanguageLevel.RAW
                )
            ),
            new NonpublicViolations(),
            "class A {\n",
            "    protected void a() {} \n",
            "}"
        ).asList();

        Assert.assertThat(
            nonpublics.size(),
            IsEqual.equalTo(1)
        );
    }

    @Test
    public void interfaceMethod() throws Exception {
        final List<Nonpublic> nonpublics = new JavaViolations<>(
            new JavaParser(
                new ParserConfiguration().setLanguageLevel(
                    ParserConfiguration.LanguageLevel.RAW
                )
            ),
            new NonpublicViolations(),
            "interface A {\n",
            "    void a();\n",
            "}"
        ).asList();

        Assert.assertThat(
            nonpublics.size(),
            IsEqual.equalTo(0)
        );
    }

}