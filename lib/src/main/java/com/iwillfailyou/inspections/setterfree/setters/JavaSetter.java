package com.iwillfailyou.inspections.setterfree.setters;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.iwillfailyou.javaparser.Item;
import com.iwillfailyou.javaparser.NodeDescription;
import com.iwillfailyou.javaparser.NodeItem;

import java.util.Optional;

public final class JavaSetter implements Setter {
    private final Item item;

    public JavaSetter(
        final MethodDeclaration expr,
        final TypeDeclaration<?> root
    ) {
        this(
            new NodeItem(
                expr,
                new NodeDescription(expr.getName(), Optional.empty(), root)
            )
        );
    }

    public JavaSetter(
        final MethodDeclaration expr,
        final Modifier cause,
        final TypeDeclaration<?> root
    ) {
        this(new NodeItem(expr, new NodeDescription(expr, cause, root)));
    }

    public JavaSetter(final Item item) {
        this.item = item;
    }

    @Override
    public String description() {
        return item.description();
    }

    @Override
    public boolean isSuppressed() {
        return item.isSuppressed("setterfree");
    }
}
