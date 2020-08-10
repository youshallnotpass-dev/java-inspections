package com.iwillfailyou.javaparser;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.LambdaExpr;
import javax.annotation.Nonnull;

public final class NodeCaller implements Caller {

    final Node callee;

    public NodeCaller(@Nonnull final Node callee) {
        this.callee = callee;
    }


    @Override
    public Node caller() {
        return callee.getParentNode()
                   .map((final Node parent) ->
                            (parent instanceof LambdaExpr || parent instanceof MethodDeclaration)
                                ? parent
                                : new NodeCaller(parent).caller())
                   .orElse(callee);
    }
}
