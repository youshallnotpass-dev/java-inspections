package com.iwillfailyou.inspections.allfinal.nonfinals;

import org.hamcrest.core.IsEqual;
import org.hamcrest.core.StringContains;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public final class JavaNonfinalsTest {
    @Test
    public void nonFinalClass() throws Exception {
        final List<Nonfinal> nonfinals = new JavaNonfinals(
            "class A {}"
        ).asList();
        Assert.assertThat(nonfinals.size(), IsEqual.equalTo(1));
        Assert.assertThat(
            nonfinals.get(0).description(),
            StringContains.containsString("A(A.java:1)")
        );
    }

    @Test
    public void finalClass() throws Exception {
        final List<Nonfinal> nonfinals = new JavaNonfinals(
            "final class A {}"
        ).asList();
        Assert.assertThat(nonfinals.isEmpty(), IsEqual.equalTo(true));
    }

    @Test
    public void nonFinalField() throws Exception {
        final List<Nonfinal> nonfinals = new JavaNonfinals(
            "final class A {\n",
            "    private int n;\n",
            "}"
        ).asList();
        Assert.assertThat(nonfinals.size(), IsEqual.equalTo(1));
        Assert.assertThat(
            nonfinals.get(0).description(),
            StringContains.containsString("A(A.java:2)")
        );
    }

    @Test
    public void finalField() throws Exception {
        final List<Nonfinal> nonfinals = new JavaNonfinals(
            "final class A {\n",
            "    private final int n;\n",
            "}"
        ).asList();
        Assert.assertThat(nonfinals.isEmpty(), IsEqual.equalTo(true));
    }

    @Test
    public void nonFinalVariable() throws Exception {
        final List<Nonfinal> nonfinals = new JavaNonfinals(
            "final class A {\n",
            "    void a() {\n",
            "        int b = 1;\n",
            "    }\n",
            "}"
        ).asList();
        Assert.assertThat(nonfinals.size(), IsEqual.equalTo(1));
        Assert.assertThat(
            nonfinals.get(0).description(),
            StringContains.containsString("A.a(A.java:3)")
        );
    }

    @Test
    public void finalVariable() throws Exception {
        final List<Nonfinal> nonfinals = new JavaNonfinals(
            "final class A {\n",
            "    void a() {\n",
            "        final int b = 1;\n",
            "    }\n",
            "}"
        ).asList();
        Assert.assertThat(nonfinals.isEmpty(), IsEqual.equalTo(true));
    }

    @Test
    public void nonFinalAttribute() throws Exception {
        final List<Nonfinal> nonfinals = new JavaNonfinals(
            "final class A {\n",
            "    void a(int b) {\n",
            "    }\n",
            "}"
        ).asList();
        Assert.assertThat(nonfinals.size(), IsEqual.equalTo(1));
        Assert.assertThat(
            nonfinals.get(0).description(),
            StringContains.containsString("A.a(A.java:2)")
        );
    }

    @Test
    public void finalAttribute() throws Exception {
        final List<Nonfinal> nonfinals = new JavaNonfinals(
            "final class A {\n",
            "    void a(final int b) {\n",
            "    }\n",
            "}"
        ).asList();
        Assert.assertThat(nonfinals.isEmpty(), IsEqual.equalTo(true));
    }

    @Test
    public void nonFinalSuppressedAttribute() throws Exception {
        final List<Nonfinal> nonfinals = new JavaNonfinals(
            "final class A {\n",
            "    @SuppressWarnings(\"allfinal\")\n",
            "    void a(int b) {\n",
            "    }\n",
            "}"
        ).asList();
        Assert.assertThat(nonfinals.size(), IsEqual.equalTo(1));
        Assert.assertThat(
            nonfinals.get(0).isSuppressed(),
            IsEqual.equalTo(true)
        );
    }

    @Test
    public void nonFinalParameterInInterface() throws Exception {
        final List<Nonfinal> nonfinals = new JavaNonfinals(
            "interface A {\n",
            "    void a(int b);\n",
            "}"
        ).asList();
        Assert.assertThat(nonfinals.size(), IsEqual.equalTo(1));
        Assert.assertThat(
            nonfinals.get(0).isInterfaceMethodParam(),
            IsEqual.equalTo(true)
        );
    }

    @Test
    public void nonFinalClassInInterface() throws Exception {
        final List<Nonfinal> nonfinals = new JavaNonfinals(
            "interface A {\n",
            "    void a(final int b);\n",
            "    class B {} \n",
            "}"
        ).asList();
        Assert.assertThat(nonfinals.size(), IsEqual.equalTo(1));
        Assert.assertThat(
            nonfinals.get(0).isInterfaceMethodParam(),
            IsEqual.equalTo(false)
        );
    }

    @Test
    public void nonFinalLambdaParam() throws Exception {
        final List<Nonfinal> nonfinals = new JavaNonfinals(
            "final class A {\n",
            "    void a() {\n",
            "        final Func a = b -> {};\n",
            "    }\n",
            "}"
        ).asList();
        Assert.assertThat(nonfinals.size(), IsEqual.equalTo(1));
        Assert.assertThat(
            nonfinals.get(0).isLambdaParam(),
            IsEqual.equalTo(true)
        );
    }
}