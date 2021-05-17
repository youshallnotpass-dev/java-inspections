package dev.youshallnotpass.inspections.inheritancefree.inheritances;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import dev.youshallnotpass.javaparser.Item;
import dev.youshallnotpass.javaparser.NodeDescription;
import dev.youshallnotpass.javaparser.NodeItem;

public final class JavaInheritance implements Inheritance {

    private final Item item;

    public JavaInheritance(
        final ClassOrInterfaceDeclaration expr,
        final TypeDeclaration<?> root
    ) {
        this(
            new NodeItem(
                expr,
                new NodeDescription(expr, expr.getName(), root)
            )
        );
    }

    public JavaInheritance(final Item item) {
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
