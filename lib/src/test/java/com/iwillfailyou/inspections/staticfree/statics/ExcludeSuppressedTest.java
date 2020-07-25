package com.iwillfailyou.inspections.staticfree.statics;

import com.iwillfailyou.inspection.ExcludeSuppressed;
import org.hamcrest.core.IsEqual;
import org.junit.Assert;
import org.junit.Test;

public final class ExcludeSuppressedTest {
    @Test
    public void emptyStaticsWhenSuppressed() throws Exception {
        Assert.assertThat(
            new ExcludeSuppressed<>(
                new JavaStatics(
                    "class A {",
                    "    @SuppressWarnings(\"staticfree\")",
                    "    static void method() {",
                    "        String name = null;",
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
                new JavaStatics(
                    "class A {\n",
                    "    @NonNull\n",
                    "    @SuppressWarnings(\"staticfree\")\n",
                    "    static String method() {\n",
                    "        return \"result\";\n",
                    "    }\n",
                    "}\n"
                )
            ).asList().isEmpty(),
            IsEqual.equalTo(true)
        );
    }
}