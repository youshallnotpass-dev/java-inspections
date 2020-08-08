package com.iwillfailyou.inspections.setterfree.setters;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParserConfiguration;
import com.iwillfailyou.inspection.JavaViolations;
import org.hamcrest.core.IsEqual;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public final class SetterViolationsTest {

    @Test
    public void setterWithThis() throws Exception {
        final List<Setter> setters = new JavaViolations<>(
            new JavaParser(
                new ParserConfiguration().setLanguageLevel(
                    ParserConfiguration.LanguageLevel.RAW
                )
            ),
            new SetterViolations(),
            "class A {\n",
            "    private String name;\n",
            "    void setName(String name) {\n",
            "        this.name = name;\n",
            "    } \n",
            "}"
        ).asList();

        Assert.assertThat(
            setters.size(),
            IsEqual.equalTo(1)
        );
        Assert.assertThat(
            setters.get(0).description(),
            IsEqual.equalTo("A.setName(A.java:3)")
        );
    }

    @Test
    public void setterWithClassThis() throws Exception {
        final List<Setter> setters = new JavaViolations<>(
            new JavaParser(
                new ParserConfiguration().setLanguageLevel(
                    ParserConfiguration.LanguageLevel.RAW
                )
            ),
            new SetterViolations(),
            "class A {\n",
            "    private String name;\n",
            "    void setName(String name) {\n",
            "        A.this.name = name;\n",
            "    } \n",
            "}"
        ).asList();

        Assert.assertThat(
            setters.size(),
            IsEqual.equalTo(1)
        );
        Assert.assertThat(
            setters.get(0).description(),
            IsEqual.equalTo("A.setName(A.java:3)")
        );
    }

    @Test
    public void setterWithoutThis() throws Exception {
        final List<Setter> setters = new JavaViolations<>(
            new JavaParser(
                new ParserConfiguration().setLanguageLevel(
                    ParserConfiguration.LanguageLevel.RAW
                )
            ),
            new SetterViolations(),
            "class A {\n",
            "    private String name;\n",
            "    void setName(String n) {\n",
            "        name = n;\n",
            "    } \n",
            "}"
        ).asList();

        Assert.assertThat(
            setters.size(),
            IsEqual.equalTo(1)
        );
        Assert.assertThat(
            setters.get(0).description(),
            IsEqual.equalTo("A.setName(A.java:3)")
        );
    }

    @Test
    public void setterWithoutAssignClassField() throws Exception {
        final List<Setter> setters = new JavaViolations<>(
            new JavaParser(
                new ParserConfiguration().setLanguageLevel(
                    ParserConfiguration.LanguageLevel.RAW
                )
            ),
            new SetterViolations(),
            "class A {\n",
            "    private String name;\n",
            "    void setName(String n) {\n",
            "        final String name = n;\n",
            "    } \n",
            "}"
        ).asList();

        Assert.assertThat(
            setters.size(),
            IsEqual.equalTo(0)
        );
    }

    @Test
    public void setterWithAssignClassFieldButNonArgumentValue() throws Exception {
        final List<Setter> setters = new JavaViolations<>(
            new JavaParser(
                new ParserConfiguration().setLanguageLevel(
                    ParserConfiguration.LanguageLevel.RAW
                )
            ),
            new SetterViolations(),
            "class A {\n",
            "    private String name;\n",
            "    void setName(String n) {\n",
            "        this.name = \"fake\";\n",
            "    } \n",
            "}"
        ).asList();

        Assert.assertThat(
            setters.size(),
            IsEqual.equalTo(0)
        );
    }

    @Test
    public void builderMethod() throws Exception {
        final List<Setter> setters = new JavaViolations<>(
            new JavaParser(
                new ParserConfiguration().setLanguageLevel(
                    ParserConfiguration.LanguageLevel.RAW
                )
            ),
            new SetterViolations(),
            "class A {\n",
            "    private String name;\n",
            "    void setName(String n) {\n",
            "        this.name = n;\n",
            "        return this;",
            "    } \n",
            "}"
        ).asList();

        Assert.assertThat(
            setters.size(),
            IsEqual.equalTo(1)
        );
    }

}