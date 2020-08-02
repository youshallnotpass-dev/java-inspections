package com.iwillfailyou.inspections.allfinal.nonfinals;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.expr.LambdaExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.CatchClause;
import com.iwillfailyou.javaparser.Item;
import com.iwillfailyou.javaparser.NodeDescription;
import com.iwillfailyou.javaparser.NodeItem;

import java.util.Optional;

public final class JavaNonfinal implements Nonfinal {

    private final Node expr;
    private final Item item;

    public JavaNonfinal(
        final FieldDeclaration expr,
        final TypeDeclaration<?> root
    ) {
        this(
            expr,
            new NodeItem(
                expr,
                new NodeDescription(expr, expr.getVariable(0), root)
            )
        );
    }

    public JavaNonfinal(
        final Parameter expr,
        final TypeDeclaration<?> root
    ) {
        this(
            expr,
            new NodeItem(
                expr,
                new NodeDescription(expr, expr, root)
            )
        );
    }

    public JavaNonfinal(
        final VariableDeclarationExpr expr,
        final TypeDeclaration<?> root
    ) {
        this(
            expr,
            new NodeItem(
                expr,
                new NodeDescription(expr, expr, root)
            )
        );
    }

    public JavaNonfinal(
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

    public JavaNonfinal(final Node expr, final Item item) {
        this.expr = expr;
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

    @Override
    public boolean isInterfaceMethodParam() {
        final boolean interfaceMethodParam;
        if (expr instanceof Parameter) {
            final Optional<ClassOrInterfaceDeclaration> parentType = expr.findFirst(
                Node.TreeTraversal.PARENTS,
                (final Node node) -> {
                    final Optional<ClassOrInterfaceDeclaration> result;
                    if (node instanceof ClassOrInterfaceDeclaration) {
                        result = Optional.of((ClassOrInterfaceDeclaration) node);
                    } else {
                        result = Optional.empty();
                    }
                    return result;
                }
            );
            interfaceMethodParam = parentType.isPresent() && parentType.get().isInterface();
        } else {
            interfaceMethodParam = false;
        }
        return interfaceMethodParam;
    }

    @Override
    public boolean isLambdaParam() {
        final boolean lambdaParam;
        if (expr instanceof Parameter) {
            lambdaParam = expr.getParentNode()
                .filter(
                    (final Node node) -> node instanceof LambdaExpr
                )
                .isPresent();
        } else {
            lambdaParam = false;
        }
        return lambdaParam;
    }

    @Override
    public boolean isCatchParam() {
        final boolean catchParam;
        if (expr instanceof Parameter) {
            catchParam = expr.getParentNode()
                .filter(
                    (final Node node) -> node instanceof CatchClause
                )
                .isPresent();
        } else {
            catchParam = false;
        }
        return catchParam;
    }
}
