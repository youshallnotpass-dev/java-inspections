package com.iwillfailyou.inspections.inheritancefree.inheritances.java;

import com.iwillfailyou.inspections.inheritancefree.inheritances.Inheritance;
import com.iwillfailyou.inspections.inheritancefree.inheritances.JavaInheritances;
import org.hamcrest.core.StringContains;
import org.junit.Assert;
import org.junit.Test;

public final class JavaInheritanceTest {

    @Test
    public void description() throws Exception {
        final Inheritance aInheritance = new JavaInheritances(
            "class A extends B{\n",
            "    private void a() {\n",
            "        return 5;\n",
            "    }\n",
            "}\n"
        ).asList().get(0);
        Assert.assertThat(
            aInheritance.description(),
            StringContains.containsString("A(A.java:1")
        );
    }

}