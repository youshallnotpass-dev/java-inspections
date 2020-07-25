package com.iwillfailyou.inspections.nullfree.nulls;

import com.iwillfailyou.inspection.ExcludeSuppressed;
import org.hamcrest.core.IsEqual;
import org.junit.Assert;
import org.junit.Test;

public final class ExcludeSuppressedTest {
    @Test
    public void emptyNullsWhenSuppressed() throws Exception {
        Assert.assertThat(
            new ExcludeSuppressed<>(
                new JavaNulls(
                    "class A {\n",
                    "    void method() {\n",
                    "        @SuppressWarnings(\"nullfree\")\n",
                    "        String name = null;\n",
                    "    }\n",
                    "}\n"
                )
            ).asList().isEmpty(),
            IsEqual.equalTo(true)
        );
    }

    @Test
    public void suppressedWithOverride() throws Exception {
        Assert.assertThat(
            new ExcludeSuppressed<>(
                new JavaNulls(
                    "class A {\n",
                    "    @Override\n",
                    "    @SuppressWarnings(\"nullfree\")\n",
                    "    void method() {\n",
                    "        String name = \"asd\";\n",
                    "        if (name == null) {}\n",
                    "    }\n",
                    "}\n"
                )
            ).asList().isEmpty(),
            IsEqual.equalTo(true)
        );
    }
}