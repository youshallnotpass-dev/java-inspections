package com.iwillfailyou.inspections.nomultiplereturn.multiplereturns;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParserConfiguration;
import com.iwillfailyou.inspection.JavaViolations;
import com.iwillfailyou.inspection.Violation;
import org.hamcrest.core.IsCollectionContaining;
import org.hamcrest.core.IsEqual;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

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

    @Test
    public void withReturnInLambdaExpr() throws Exception {
        final List<MultipleReturn> multipleReturns = new JavaViolations<>(
            new JavaParser(
                new ParserConfiguration().setLanguageLevel(
                    ParserConfiguration.LanguageLevel.RAW
                )
            ),
            new MultipleReturnViolations(),
            "class A {\n",
            "    int a() {\n",
            "        return someStatement(() -> {\n",
            "           return 3;\n",
            "        });\n",
            "    } \n",
            "}"
        ).asList();

        Assert.assertThat(
            multipleReturns.size(),
            IsEqual.equalTo(0)
        );
    }

    @Test
    public void withTwoReturnsAndTwoInLambdaExpr() throws Exception {
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
            "            return method(\n",
            "                () -> {\n",
            "                    if (otherStatement()) {\n",
            "                        return 1;\n",
            "                    } else {\n",
            "                        return 2;\n",
            "                    }\n",
            "                });\n",
            "        } else {\n",
            "            return 3;\n",
            "        }\n",
            "    }\n",
            "}"
        ).asList();

        Assert.assertThat(
            multipleReturns.size(),
            IsEqual.equalTo(2)
        );
        Assert.assertThat(
            multipleReturns.stream().map(Violation::description).collect(Collectors.toList()),
            IsCollectionContaining.hasItems("A.a(A.java:2)", "A.a(A.java:5)"));
    }

    @Test
    public void withTwoReturnsInLambdaExpr() throws Exception {
        final List<MultipleReturn> multipleReturns = new JavaViolations<>(
            new JavaParser(
                new ParserConfiguration().setLanguageLevel(
                    ParserConfiguration.LanguageLevel.RAW
                )
            ),
            new MultipleReturnViolations(),
            "class A {\n",
            "    int a() {\n",
            "         return method(\n",
            "            () -> {\n",
            "                if (otherStatement()) {\n",
            "                    return 1;\n",
            "                } else {\n",
            "                    return 2;\n",
            "                }\n",
            "            });\n",
            "    }\n",
            "}"
        ).asList();

        Assert.assertThat(
            multipleReturns.size(),
            IsEqual.equalTo(1)
        );
        Assert.assertThat(
            multipleReturns.get(0).description(),
            IsEqual.equalTo("A.a(A.java:4)")
        );
    }

    @Test
    public void withConditionalReturn() throws Exception {
        final List<MultipleReturn> multipleReturns = new JavaViolations<>(
            new JavaParser(
                new ParserConfiguration().setLanguageLevel(
                    ParserConfiguration.LanguageLevel.RAW
                )
            ),
            new MultipleReturnViolations(),
            "class A {\n",
            "    int a() {\n",
            "        return someStatement()\n",
            "            ? 1\n",
            "            : 2;\n",
            "    } \n",
            "}"
        ).asList();

        Assert.assertThat(
            multipleReturns.size(),
            IsEqual.equalTo(0)
        );
    }
}