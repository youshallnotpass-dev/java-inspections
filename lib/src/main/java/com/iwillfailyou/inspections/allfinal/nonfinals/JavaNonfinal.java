package com.iwillfailyou.inspections.allfinal.nonfinals;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.iwillfailyou.javaparser.Item;
import com.iwillfailyou.javaparser.NodeDescription;
import com.iwillfailyou.javaparser.NodeItem;

public class JavaNonfinal implements Nonfinal {

    private final Item item;

    public JavaNonfinal(
        final FieldDeclaration expr,
        final TypeDeclaration<?> root
    ) {
        this(new NodeItem(expr, new NodeDescription(expr, expr, root)));
    }

    public JavaNonfinal(
        final Parameter expr,
        final TypeDeclaration<?> root
    ) {
        this(new NodeItem(expr, new NodeDescription(expr, expr, root)));
    }

    public JavaNonfinal(
        final VariableDeclarationExpr expr,
        final TypeDeclaration<?> root
    ) {
        this(new NodeItem(expr, new NodeDescription(expr, expr, root)));
    }

    public JavaNonfinal(
        final ClassOrInterfaceDeclaration expr,
        final TypeDeclaration<?> root
    ) {
        this(new NodeItem(expr, new NodeDescription(expr, expr, root)));
    }

    public JavaNonfinal(final Item item) {
        this.item = item;
    }

    @Override
    public String description() {
        return item.description();
    }

    @Override
    public boolean isSuppressed() {
        return item.isSuppressed("allfinal");
    }
}
