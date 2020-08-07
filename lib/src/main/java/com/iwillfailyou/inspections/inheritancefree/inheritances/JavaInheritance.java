package com.iwillfailyou.inspections.inheritancefree.inheritances;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.iwillfailyou.javaparser.Item;
import com.iwillfailyou.javaparser.NodeDescription;
import com.iwillfailyou.javaparser.NodeItem;

public final class JavaInheritance implements Inheritance {

    private final Node node;
    private final Item item;

    public JavaInheritance(
        final ClassOrInterfaceDeclaration expr,
        final TypeDeclaration<?> root
    ) {
        this(
            expr,
            new NodeItem(
                expr,
                new NodeDescription(expr, expr.getName(), root)
            )
        );
    }

    public JavaInheritance(final Node node, final Item item) {
        this.node = node;
        this.item = item;
    }

    @Override
    public String description() {
        return item.description();
    }

    @Override
    public boolean isSuppressed() {
        return item.isSuppressed("inheritancefree");
    }
}
