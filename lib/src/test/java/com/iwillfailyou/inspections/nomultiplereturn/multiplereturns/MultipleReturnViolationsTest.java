package com.iwillfailyou.inspections.nomultiplereturn.multiplereturns;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParserConfiguration;
import com.iwillfailyou.inspection.JavaViolations;
import org.hamcrest.core.IsEqual;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public final class MultipleReturnViolationsTest {

    @Test
    public void withoutReturns() throws Exception {
        final List<MultipleReturn> multipleReturns = new JavaViolations<>(
            new JavaParser(
                new ParserConfiguration().setLanguageLevel(
                    ParserConfiguration.LanguageLevel.RAW
                )
            ),
            new MultipleReturnViolations(),
            "class A {\n",
            "    void a() {} \n",
            "}"
        ).asList();

        Assert.assertThat(
            multipleReturns.size(),
            IsEqual.equalTo(0)
        );
    }

    @Test
    public void withReturn() throws Exception {
        final List<MultipleReturn> multipleReturns = new JavaViolations<>(
            new JavaParser(
                new ParserConfiguration().setLanguageLevel(
                    ParserConfiguration.LanguageLevel.RAW
                )
            ),
            new MultipleReturnViolations(),
            "class A {\n",
            "    int a() {\n",
            "        return 1;\n",
            "    } \n",
            "}"
        ).asList();

        Assert.assertThat(
            multipleReturns.size(),
            IsEqual.equalTo(0)
        );
    }

    @Test
    public void withTwoReturns() throws Exception {
        final List<MultipleReturn> multipleReturns = new JavaViolations<>(
            new JavaParser(
                new ParserConfiguration().setLanguageLevel(
                    ParserConfiguration.LanguageLevel.RAW
                )
            ),
            new MultipleReturnViolations(),
            "class A {\n",
            "    int a() {\n",
            "        if (someStatement()) {\n",
            "            return 1;\n",
            "        } else {\n",
            "            return 2;\n",
            "        }\n",
            "    } \n",
            "}"
        ).asList();

        Assert.assertThat(
            multipleReturns.size(),
            IsEqual.equalTo(1)
        );
        Assert.assertThat(
            multipleReturns.get(0).description(),
            IsEqual.equalTo("A.a(A.java:2)")
        );
    }

    @Test
    public void withTwoReturnsSuppressed() throws Exception {
        final List<MultipleReturn> multipleReturns = new JavaViolations<>(
            new JavaParser(
                new ParserConfiguration().setLanguageLevel(
                    ParserConfiguration.LanguageLevel.RAW
                )
            ),
            new MultipleReturnViolations(),
            "class A {\n",
            "    @SuppressWarnings(\"nomultiplereturn\")\n",
            "    int a() {\n",
            "        if (someStatement()) {\n",
            "            return 1;\n",
            "        } else {\n",
            "            return 2;\n",
            "        }\n",
            "    } \n",
            "}"
        ).asList();

        Assert.assertThat(
            multipleReturns.size(),
            IsEqual.equalTo(1)
        );
        Assert.assertThat(
            multipleReturns.get(0).isSuppressed(),
            IsEqual.equalTo(true)
        );
    }

}