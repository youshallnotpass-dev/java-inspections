package dev.youshallnotpass.inspections.nullfree.nulls.java;

import dev.youshallnotpass.inspections.nullfree.nulls.Null;
import dev.youshallnotpass.inspections.nullfree.nulls.JavaNulls;
import org.hamcrest.core.StringContains;
import org.junit.Assert;
import org.junit.Test;

public final class JavaNullTest {

    @Test
    public void description() throws Exception {
        final Null aNull = new JavaNulls(
            "interface A {\n",
            "    private void a() {\n",
            "        String a = null;\n",
            "    }\n",
            "}\n"
        ).asList().get(0);
        Assert.assertThat(
            aNull.description(),
            StringContains.containsString("A.a(A.java:3)")
        );
    }

}