package com.iwillfailyou.inspections.nomultiplereturn.multiplereturns;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.iwillfailyou.javaparser.Item;
import com.iwillfailyou.javaparser.NodeDescription;
import com.iwillfailyou.javaparser.NodeItem;
import java.util.Optional;

public final class JavaMultipleReturn implements MultipleReturn {

    private final Item item;

    public JavaMultipleReturn(
        final Node node,
        final TypeDeclaration<?> root
    ) {
        this(
            new NodeItem(
                node,
                new NodeDescription(node, Optional.empty(), root)
            )
        );
    }

    public JavaMultipleReturn(final Item item) {
        this.item = item;
    }

    @Override
    public String description() {
        return item.description();
    }

    @Override
    public boolean isSuppressed() {
        return item.isSuppressed("nomultiplereturn");
    }
}
