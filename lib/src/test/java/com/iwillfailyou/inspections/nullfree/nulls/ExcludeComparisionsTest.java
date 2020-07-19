package com.iwillfailyou.inspections.nullfree.nulls;

import org.hamcrest.core.IsEqual;
import org.junit.Assert;
import org.junit.Test;

public class ExcludeComparisionsTest {
    @Test
    public void asdasd() throws Exception {
        Assert.assertThat(
            new ExcludeComparisions(
                new JavaNulls(
                    "class A {\n",
                    "    void method() {\n",
                    "        String name = \"some name\";\n",
                    "        if (name == null) {}",
                    "    }\n",
                    "}\n"
                )
            ).asList().isEmpty(),
            IsEqual.equalTo(true)
        );
    }
}