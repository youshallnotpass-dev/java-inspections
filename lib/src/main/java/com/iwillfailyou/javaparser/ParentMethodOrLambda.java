package com.iwillfailyou.javaparser;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.LambdaExpr;
import java.util.Optional;

public final class ParentMethodOrLambda implements Search<Node> {

    final Node node;

    public ParentMethodOrLambda(final Node node) {
        this.node = node;
    }

    @Override
    public Optional<Node> find() {
        return node.findFirst(
            Node.TreeTraversal.PARENTS,
            (final Node parent) -> {
                return Optional.of(parent)
                           .filter(p -> p instanceof LambdaExpr || p instanceof MethodDeclaration);
            });
    }
}
