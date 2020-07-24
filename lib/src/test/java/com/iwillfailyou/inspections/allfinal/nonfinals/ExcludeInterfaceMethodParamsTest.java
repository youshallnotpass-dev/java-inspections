package com.iwillfailyou.inspections.allfinal.nonfinals;

import org.hamcrest.core.IsEqual;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class ExcludeInterfaceMethodParamsTest {

    @Test
    public void withoutInterfaceMethodParams() throws Exception {
        final List<Nonfinal> nonfinals = new ExcludeInterfaceMethodParams(
            new JavaNonfinals(
                "interface A {\n",
                "    void a(int b);\n",
                "}"
            )
        ).asList();
        Assert.assertThat(nonfinals.isEmpty(), IsEqual.equalTo(true));
    }

}