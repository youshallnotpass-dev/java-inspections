package com.iwillfailyou.inspections.nullfree.nulls.java;

import com.iwillfailyou.inspections.nullfree.nulls.Null;
import com.iwillfailyou.inspections.nullfree.nulls.JavaNulls;
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