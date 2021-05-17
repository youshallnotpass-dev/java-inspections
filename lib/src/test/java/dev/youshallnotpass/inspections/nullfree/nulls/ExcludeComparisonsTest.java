package dev.youshallnotpass.inspections.nullfree.nulls;

import org.hamcrest.core.IsEqual;
import org.junit.Assert;
import org.junit.Test;

public final class ExcludeComparisonsTest {

    @Test
    public void nullComparisonIsIgnored() throws Exception {
        Assert.assertThat(
            new ExcludeComparisons(
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