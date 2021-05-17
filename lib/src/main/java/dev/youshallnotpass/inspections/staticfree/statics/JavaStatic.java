package dev.youshallnotpass.inspections.staticfree.statics;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.TypeDeclaration;
import dev.youshallnotpass.javaparser.Item;
import dev.youshallnotpass.javaparser.NodeDescription;
import dev.youshallnotpass.javaparser.NodeItem;

public final class JavaStatic implements Static {

    private final Item item;

    public JavaStatic(
        final Node expr,
        final TypeDeclaration<?> root
    ) {
        this(new NodeItem(expr, new NodeDescription(expr, expr, root)));
    }

    public JavaStatic(final Item item) {
        this.item = item;
    }

    @Override
    public String description() {
        return item.description();
    }

    @Override
    public boolean isSuppressed() {
        return item.isSuppressed("staticfree");
    }
}
